package com.os.controller;

import com.os.dto.ProcessDTO.AddProcessRequest;
import com.os.dto.ProcessDTO.RunSimulationRequest;
import com.os.dto.ProcessDTO.TerminateProcessRequest;
import com.os.dto.Result;
import com.os.service.ProcessService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/api/process")
public class ProcessController {

    private ProcessService processService;

    @Resource(name = "ProcessServiceImplA")
    public void setProcessService(ProcessService processService) {
        this.processService = processService;
    }

    /**
     * POST /api/process/add - 创建进程
     * 根据用户输入创建新进程，并加入模拟系统。
     */

    @PostMapping("/add")
    public Result createProcess(@RequestBody AddProcessRequest request){
        try {
            // 使用支持优先级的方法
            if (request.getPriority() > 0) {
                processService.addProcess(request.getName(), request.getArriveTime(), request.getServiceTime(), request.getPriority());
            } else {
                processService.addProcess(request.getName(), request.getArriveTime(), request.getServiceTime());
            }
            return Result.ok("进程 " + request.getName() + " 创建成功！");
        } catch (IllegalArgumentException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            // 捕获其他未知异常
            e.printStackTrace(); // 打印堆栈信息便于调试
            return Result.fail("创建进程失败: " + e.getMessage());
        }
    }

    /**
     * POST /api/process/run - 运行模拟 (单步/全部)
     * 根据选定的调度算法运行一步或全部模拟，更新进程状态。
     */

    @PostMapping("/run")
    public Result runSimulation(@RequestBody RunSimulationRequest request) {
        try {
            // 验证算法和模式参数
            if (!Arrays.asList("FCFS", "RR", "SJF", "Priority").contains(request.getAlgorithm())) {
                return Result.fail("不支持的调度算法: " + request.getAlgorithm());
            }
            if (!Arrays.asList("step", "all").contains(request.getMode())) {
                return Result.fail("不支持的运行模式: " + request.getMode());
            }

            return Result.ok("模拟步骤执行成功", processService.runSimulation(request.getAlgorithm(), request.getMode()));
        } catch (IllegalStateException e) {
            // 捕获业务逻辑中抛出的状态异常，如“所有进程已完成”
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("运行模拟失败: " + e.getMessage());
        }
    }

    /**
     * GET /api/process/list - 获取当前所有进程状态
     */

    @GetMapping("/list")
    public Result getProcessList() {
        try {
            return Result.ok("进程列表获取成功", processService.getSimulationState());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("获取进程列表失败: " + e.getMessage());
        }
    }

    /**
     * POST /api/process/reset - 重置模拟
     */
    @PostMapping("/reset")
    public Result resetSimulation() {
        try {
            processService.reset();
            return Result.ok("进程模拟已重置！");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("重置模拟失败: " + e.getMessage());
        }
    }

    /**
     * POST /api/process/terminate - 终止进程
     * 强制终止指定名称的进程。
     */
    @PostMapping("/terminate")
    public Result terminateProcess(@RequestBody TerminateProcessRequest request) {
        try {
            processService.terminateProcess(request.getName());
            return Result.ok("进程 " + request.getName() + " 已成功终止。");
        } catch (IllegalArgumentException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("终止进程失败: " + e.getMessage());
        }
    }
}
