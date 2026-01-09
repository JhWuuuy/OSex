package com.os.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessPCB {
    private String name; // 进程名
    private String state; // 进程状态，如 "New", "Ready", "Running", "Terminated"
    private int arriveTime; // 到达时间
    private int serviceTime; // 总服务时间（所需CPU时间）
    private int executedTime; // 已运行时间
    private int remainingTime; // 剩余运行时间
    private int priority; // 进程优先级，数值越大优先级越高

    // 调度算法辅助字段
    private int lastRunTime; // 对于RR，记录当前时间片已运行时间
    private boolean started; // 标记进程是否已首次运行，用于计算响应时间

    // 性能指标字段
    private int waitingTime; // 等待时间
    private int turnaroundTime; // 周转时间
    private int responseTime; // 响应时间
}