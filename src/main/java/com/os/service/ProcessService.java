package com.os.service;

import com.os.entity.ProcessSimulationState;

public interface ProcessService {
    void addProcess(String name, int arriveTime, int serviceTime);
    
    // 支持优先级的添加进程方法
    void addProcess(String name, int arriveTime, int serviceTime, int priority);

    ProcessSimulationState getSimulationState();

    ProcessSimulationState  runSimulation(String algorithm, String mode);

    void reset();

    void terminateProcess(String name);
}
