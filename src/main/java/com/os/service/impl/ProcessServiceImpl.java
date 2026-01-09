package com.os.service.impl;

import com.os.entity.ProcessPCB;
import com.os.entity.ProcessSimulationState;
import com.os.service.ProcessService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("ProcessServiceImplA")
public class ProcessServiceImpl implements ProcessService {

    // 模拟系统的全局时钟
    private int currentTime = 0;
    // 使用 LinkedHashMap 保持添加顺序
    private final Map<String, ProcessPCB> processMap = new LinkedHashMap<>();


    // RR算法的时间片大小
    private static final int TIME_QUANTUM = 2;
    // 排队队列：专门用来存“正在排队”的进程名字，解决RR算法排队问题
    private final LinkedList<String> readyQueue = new LinkedList<>();


    /**
     * 添加新进程到系统中
     */
    @Override
    public void addProcess(String name, int arriveTime, int serviceTime) {
        addProcess(name, arriveTime, serviceTime, 1); // 默认优先级为1
    }

    // 添加支持优先级的方法
    public void addProcess(String name, int arriveTime, int serviceTime, int priority) {
        // 检查进程名是否已存在，如果存在则抛出异常
        if (processMap.containsKey(name)) {
            throw new IllegalArgumentException("进程名 " + name + " 已存在");
        }
        // 初始化 PCB，状态为 New
        ProcessPCB pcb = new ProcessPCB();
        pcb.setName(name);
        pcb.setState("New");
        pcb.setArriveTime(arriveTime);
        pcb.setServiceTime(serviceTime);

        // 初始化数值字段
        pcb.setExecutedTime(0);
        pcb.setRemainingTime(serviceTime); // 剩余时间初始等于服务时间
        pcb.setLastRunTime(0);
        pcb.setPriority(priority); // 设置优先级

        // 初始化布尔值
        pcb.setStarted(false);

        // 初始化统计指标
        pcb.setWaitingTime(0);
        pcb.setTurnaroundTime(0);
        pcb.setResponseTime(0);

        processMap.put(name, pcb);
    }

    /**
     * 运行进程模拟的主方法
     */
    @Override
    public ProcessSimulationState runSimulation(String algorithm, String mode) {
        // 简单校验：如果没有未完成的进程，且没有新进程，无法运行
        boolean allFinished = isAllFinished();

        if (processMap.isEmpty() || allFinished) {
            // 如果已经全部结束，直接返回当前状态，或者抛异常提示前端
            return getSimulationState();
        }

        if ("step".equals(mode)) {
            // 单步执行模式：执行一个时间单位的调度
            runOneStep(algorithm);
        } else if ("all".equals(mode)) {
            // 循环运行直到所有进程结束（设置个上限防止死循环）
            int limit = 1000;
            while (!isAllFinished() && limit > 0) {
                runOneStep(algorithm);
                limit--;
            }
        }
        return getSimulationState();
    }


    /**
     * 获取当前进程模拟状态的方法
     */
    @Override
    public ProcessSimulationState getSimulationState() {
        // 将进程映射表中的值转换为列表,组装返回给前端的对象
        List<ProcessPCB> list = new ArrayList<>(processMap.values());

        // 找到当前正在运行的进程
        ProcessPCB running = getRunningProcess();
        // 创建并返回包含当前时间、运行中进程和所有进程列表的状态对象

        return new ProcessSimulationState(currentTime, running, list);
    }

    @Override
    public void reset() {
        this.currentTime = 0;
        this.processMap.clear();
        this.readyQueue.clear();
    }

    @Override
    public void terminateProcess(String name) {
        ProcessPCB pcb = processMap.get(name);
        if (pcb != null) {
            pcb.setState("Terminated");
            pcb.setRemainingTime(0);
        }
    }

    // ================== 调度逻辑 ==================

    /**
     * 运行一个时间步 (1s)
     */
    private void runOneStep(String algorithm) {

        // 1.查看是否有新进程入队，有就进行排队
        checkNewArrivals();

        if("SJF".equals(algorithm)) {
            checkServiceTimePreemption();
        }

        if("Priority".equals(algorithm)) {
            checkPriorityPreemption();
        }

        // 3.如果现在CPU是空的，从队伍里选一个人出来
        if(getRunningProcess() == null) {
            scheduleNextProcess(algorithm);
        }

        // 4.如果有人在运行让他运行1秒
        executeCurrentProcess(algorithm);

        // 5.全局时钟+1
        currentTime++;

        // 6.再次更新就绪队列
        checkNewArrivals();
    }

    /**
     * 1.检查是否有新进程到达，如果有则将其状态改为“就绪”，并加入就绪队列
     */
    private void checkNewArrivals() {
        for(ProcessPCB p : processMap.values()){
            if("New".equals(p.getState()) && p.getArriveTime()<=currentTime) {
                p.setState("Ready");
                readyQueue.addLast(p.getName());
            }
        }
    }

    /**
     * 2.调度器，从 readyQueue 进行选择
     */
    private void scheduleNextProcess(String algorithm) {
       if (readyQueue.isEmpty()) return;

       String nextName = null;

        // SJF: 最短作业优先,遍历队伍，找出服务时间最短的那个
       if("SJF".equals(algorithm)) {
           nextName = findShortestJobInQueue();
           readyQueue.remove(nextName);
       } else if("Priority".equals(algorithm)) {
           // 最高优先级调度算法，找出优先级最高的进程
           nextName = findHighestPriorityJobInQueue();
           readyQueue.remove(nextName);
       } else {
           // FCFS 和 RR: 直接取队头并移除 (先来先服务，或者轮转的下一个)
           nextName = readyQueue.pollFirst();
       }

       if(nextName != null) {
           ProcessPCB next = processMap.get(nextName);
           next.setState("Running");
           next.setLastRunTime(0); // 重置时间片计数

           // 记录首次响应时间
           if (!next.isStarted()) {
               next.setStarted(true);
               // 响应时间 = 当前时间 - 到达时间
               next.setResponseTime(currentTime - next.getArriveTime());
           }
       }
    }

    /**
     * 步骤3：执行当前进程
     */
    private void executeCurrentProcess(String algorithm) {
        ProcessPCB running = getRunningProcess();
        if (running == null) return;

        // 运行 1 秒
        running.setExecutedTime(running.getExecutedTime() + 1);
        running.setRemainingTime(running.getRemainingTime() - 1);

        // 时间片计数+1
        running.setLastRunTime(running.getLastRunTime() + 1);

        // --- 判断是否结束或被抢占 ---

        if (running.getRemainingTime() <= 0) {
            // 服务时间结束，设为终止态
            running.setState("Terminated");
            running.setTurnaroundTime((currentTime + 1) - running.getArriveTime()); // 结束时间 - 到达时间
        }
        else if ("RR".equals(algorithm) && running.getLastRunTime() >= TIME_QUANTUM) {
            // 未结束，但时间片用完
            running.setState("Ready");
            // 排回队尾
            readyQueue.addLast(running.getName());
        }
    }

    /**
     * 检查是否需要基于服务时间抢占
     */
    private void checkServiceTimePreemption() {
        ProcessPCB running = getRunningProcess();

        // 如果CPU为空,不需要抢占
        if(running == null) return;

        // 找到队列中剩余时间最短的进程
        String shortestJob = findShortestJobInQueue();

        if(shortestJob !=null ) {
            ProcessPCB shortestJobPCB = processMap.get(shortestJob);

            // 如果当前运行进程的剩余时间比队列中最短进程的剩余时间长，则进行抢占
            if(running.getRemainingTime() > shortestJobPCB.getRemainingTime()) {
                // 1.将当前进程挂起，放回就绪队列
                readyQueue.addFirst(running.getName());
                running.setState("Ready");

                // 2.把队列里的短进程拿出来运行
                readyQueue.remove(shortestJob);
                shortestJobPCB.setState("Running");
            }
        }

    }

    /**
     * 检查是否需要基于优先级进行抢占
     */
    private void checkPriorityPreemption() {
        ProcessPCB running = getRunningProcess();

        // 如果CPU为空,不需要抢占
        if(running == null) return;

        // 找到队列中优先级最高的进程
        String highestPriorityJob = findHighestPriorityJobInQueue();

        if(highestPriorityJob != null) {
            ProcessPCB highestPriorityPCB = processMap.get(highestPriorityJob);

            // 如果当前运行进程的优先级比队列中最高优先级进程的优先级低，则进行抢占
            if(running.getPriority() < highestPriorityPCB.getPriority()) {
                // 1.将当前进程挂起，放回就绪队列
                readyQueue.addFirst(running.getName());
                running.setState("Ready");

                // 2.把队列里的高优先级进程拿出来运行
                readyQueue.remove(highestPriorityJob);
                highestPriorityPCB.setState("Running");
            }
        }
    }

    /**
     * 找到就绪队列中服务时间最短的进程
     */
    private String findShortestJobInQueue() {
        String shortestName = null;
        int minTime = Integer.MAX_VALUE;

        for(String name : readyQueue) {
            ProcessPCB p = processMap.get(name);
            if(p.getRemainingTime() < minTime) {
                minTime = p.getRemainingTime();
                shortestName = name;
            }
        }

        return shortestName;
    }

    /**
     * 找到就绪队列中优先级最高的进程
     */
    private String findHighestPriorityJobInQueue() {
        String highestPriorityName = null;
        int maxPriority = Integer.MIN_VALUE;

        for(String name : readyQueue) {
            ProcessPCB p = processMap.get(name);
            if(p.getPriority() > maxPriority) {
                maxPriority = p.getPriority();
                highestPriorityName = name;
            }
        }

        return highestPriorityName;
    }

    // ================== 辅助方法 ==================

    private ProcessPCB getRunningProcess() {
        return processMap.values().stream()
                .filter(p -> "Running".equals(p.getState())) // 筛选条件：进程状态为"Running"
                .findFirst() // 获取第一个符合条件的进程
                .orElse(null); // 如果没有符合条件的进程，则返回null
    }

    private boolean isAllFinished() {
        return processMap.values().stream()
                .allMatch(p -> "Terminated".equals(p.getState()));
        //通过values获得该map所有的值，通过stream流操作确保所有的pcb状态都为终止
    }
}