package com.os.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessSimulationState {
    private int currentTime; // 当前模拟时间
    private ProcessPCB runningProcess; // 当前正在运行的进程，可能为null
    private List<ProcessPCB> processes; // 所有进程的当前状态列表
}