package com.os.service.impl;

import com.os.dto.VirtualMemoryDTO.SimulateRequest;
import com.os.entity.VMSimulationResult;
import com.os.entity.VMSimulationStep;
import com.os.service.VirtualMemoryService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class VirtualMemoryServiceImpl implements VirtualMemoryService {

    @Override
    public VMSimulationResult simulate(List<Integer> pages, int blocks, SimulateRequest.Algorithm algorithm) {
        if(algorithm.equals(SimulateRequest.Algorithm.FIFO)){
            return simulateFIFO(pages, blocks);
        } else if(algorithm.equals(SimulateRequest.Algorithm.LRU)){
            return simulateLRU(pages, blocks);
        } else if(algorithm.equals(SimulateRequest.Algorithm.LFU)){
            return simulateLFU(pages, blocks);
        } else {
            return null;
        }
    }

    /**
     * FIFO（先进先出）页面置换算法模拟
     */
    private VMSimulationResult simulateFIFO(List<Integer> pages, int blocks) {
        List<VMSimulationStep> steps = new ArrayList<>(); // 模拟步骤数组
        int pageFaults = 0; //总缺页次数

        // 使用队列来维护物理块中的页面，队首是最早进入的页面
        Queue<Integer> frameQueue = new LinkedList<>();

        // 初始化物理块，全部为-1（空闲）
        int[] frames = new int[blocks];
        Arrays.fill(frames, -1);

        // 遍历页面访问序列
        for (int page : pages) {
            // 检查页面是否已在物理块中
            boolean pageFound = false; //是否命中
            for (int i = 0; i < frames.length; i++) {
                if (frames[i] == page) {
                    pageFound = true;
                    break;
                }
            }

            // 创建当前步骤的物理块状态副本
            int[] currentFrames = new int[blocks];
            System.arraycopy(frames, 0, currentFrames, 0, blocks);
            /*
            将源数组 frames 中，从索引 0 开始的 blocksNums 个元素，
            复制到目标数组 currentFrames 中，
            并从目标数组的索引 0 位置开始存放。
            */

            VMSimulationStep step = new VMSimulationStep();
            step.setPage(page); //当前访问页面号
            step.setFrames(currentFrames); //当前物理块状态

            if (pageFound) {
                // 页面命中
                step.setStatus("HIT");
            } else {
                // 页面未命中，需要置换
                pageFaults++;

                if (frameQueue.size() < blocks) {
                    // 还有空闲块，直接装入
                    for (int i = 0; i < frames.length; i++) {
                        if (frames[i] == -1) {
                            frames[i] = page;
                            frameQueue.add(page);
                            step.setStatus("MISS");
                            break;
                        }
                    }
                } else {
                    // 没有空闲块，需要置换最早进入的页面
                    int replacedPage = frameQueue.poll(); // 移除队首
                    frameQueue.add(page); // 将新页面加入队尾

                    // 更新物理块状态
                    for (int i = 0; i < frames.length; i++) {
                        if (frames[i] == replacedPage) {
                            frames[i] = page;
                            step.setStatus("淘汰页面" + replacedPage);
                            break;
                        }
                    }
                }
            }

            // 更新当前步骤块的物理块状态
            System.arraycopy(frames, 0, step.getFrames(), 0, blocks);
            steps.add(step);
        }

        // 计算缺页率
        double pageFaultRate = pages.isEmpty() ? 0 : (double) pageFaults / pages.size();

        // 创建并返回模拟结果
        return new VMSimulationResult(pageFaults, pageFaultRate, steps);
    }

    /**
     * LRU（最近最少使用）页面置换算法模拟
     */
    private VMSimulationResult simulateLRU(List<Integer> pages, int blocks) {
        List<VMSimulationStep> steps = new ArrayList<>(); // 模拟步骤数组
        int pageFaults = 0; //总缺页次数

        // 使用链表来维护物理块中的页面，链表头部是最近使用的页面，尾部是最久未使用的页面
        LinkedList<Integer> frameLinkedList = new LinkedList<>();

        // 初始化物理块，全部为-1（空闲）
        int[] frames = new int[blocks];
        Arrays.fill(frames, -1);

        // 遍历页面访问序列
        for (int page : pages) {
            // 检查页面是否已在物理块中
            boolean pageFound = false;

            for (int i = 0; i < frames.length; i++) {
                if (frames[i] == page) {
                    pageFound = true;
                    break;
                }
            }

            // 创建当前步骤的物理块状态副本
            int[] currentFrames = new int[blocks];
            System.arraycopy(frames, 0, currentFrames, 0, blocks);

            VMSimulationStep step = new VMSimulationStep();
            step.setPage(page);
            step.setFrames(currentFrames);

            if (pageFound) {
                // 页面命中，更新链表，将该页面移到链表头部
                frameLinkedList.remove(Integer.valueOf(page));
                frameLinkedList.addFirst(page);
                step.setStatus("HIT");
            } else {
                // 页面未命中，需要处理
                pageFaults++;

                if (frameLinkedList.size() < blocks) {
                    // 还有空闲块，直接装入
                    for (int i = 0; i < frames.length; i++) {
                        if (frames[i] == -1) {
                            frames[i] = page;
                            frameLinkedList.addFirst(page);
                            step.setStatus("MISS");
                            break;
                        }
                    }
                } else {
                    // 没有空闲块，需要置换最久未使用的页面，也就是链表尾部
                    int replacedPage = frameLinkedList.removeLast(); // 移除链表尾部
                    frameLinkedList.addFirst(page); // 将新页面加入链表头部

                    // 更新物理块状态
                    for (int i = 0; i < frames.length; i++) {
                        if (frames[i] == replacedPage) {
                            frames[i] = page;
                            step.setStatus("淘汰页面" + replacedPage);
                            break;
                        }
                    }
                }
            }

            // 更新当前步骤的物理块状态
            System.arraycopy(frames, 0, step.getFrames(), 0, blocks);
            steps.add(step);
        }

        // 计算缺页率
        double pageFaultRate = pages.isEmpty() ? 0 : (double) pageFaults / pages.size();

        // 创建并返回模拟结果
        return new VMSimulationResult(pageFaults, pageFaultRate, steps);
    }

    /**
     * LFU（最不常用）页面置换算法模拟
     */
    private VMSimulationResult simulateLFU(List<Integer> pages, int blocks) {
        List<VMSimulationStep> steps = new ArrayList<>(); // 模拟步骤数组
        int pageFaults = 0; //总缺页次数

        // 使用HashMap来维护物理块中的页面，键为页面号，值为该页面被访问的次数
        HashMap<Integer, Integer> frameHashMap = new HashMap<>();

        // 初始化物理块，全部为-1（空闲）
        int[] frames = new int[blocks];
        Arrays.fill(frames, -1);

        // 遍历页面访问序列
        for (int page : pages) {
            // 检查页面是否已在物理块中
            boolean pageFound = false;

            for (int i = 0; i < frames.length; i++) {
                if (frames[i] == page) {
                    pageFound = true;
                    break;
                }
            }

            // 创建当前步骤的物理块状态副本
            int[] currentFrames = new int[blocks];
            System.arraycopy(frames, 0, currentFrames, 0, blocks);

            VMSimulationStep step = new VMSimulationStep();
            step.setPage(page);
            step.setFrames(currentFrames);

            if (pageFound) {
                // 页面命中，增加该页面的访问次数
                int currentCount = frameHashMap.getOrDefault(page, 0);
                frameHashMap.put(page, currentCount + 1);
                step.setStatus("HIT");
            } else {
                // 页面未命中，需要处理
                pageFaults++;

                // 检查是否有空闲块
                boolean hasEmptyBlock = false;
                for (int i = 0; i < frames.length; i++) {
                    if (frames[i] == -1) {
                        hasEmptyBlock = true;
                        break;
                    }
                }

                if (hasEmptyBlock) {
                    // 还有空闲块，直接装入
                    for (int i = 0; i < frames.length; i++) {
                        if (frames[i] == -1) {
                            frames[i] = page;
                            frameHashMap.put(page, 1); // 新页面访问次数设为1
                            step.setStatus("MISS");
                            break;
                        }
                    }
                } else {
                    // 没有空闲块，需要置换访问次数最少的页面
                    int minAccessCount = Integer.MAX_VALUE;
                    int replacedPage = -1;
                    int replacedIndex = -1;

                    // 在物理块中查找访问次数最少的页面
                    for (int i = 0; i < frames.length; i++) {
                        int framePage = frames[i];
                        int accessCount = frameHashMap.getOrDefault(framePage, 0);
                        if (accessCount < minAccessCount) {
                            minAccessCount = accessCount;
                            replacedPage = framePage;
                            replacedIndex = i;
                        }
                    }

                    // 从HashMap中移除被淘汰的页面
                    frameHashMap.remove(replacedPage);
                    // 更新物理块
                    frames[replacedIndex] = page;
                    // 新页面访问次数设为1
                    frameHashMap.put(page, 1);
                    step.setStatus("淘汰页面" + replacedPage);
                }
            }

            // 更新当前步骤的物理块状态
            System.arraycopy(frames, 0, step.getFrames(), 0, blocks);
            steps.add(step);
        }

        // 计算缺页率
        double pageFaultRate = pages.isEmpty() ? 0 : (double) pageFaults / pages.size();

        // 创建并返回模拟结果
        return new VMSimulationResult(pageFaults, pageFaultRate, steps);
    }
}
