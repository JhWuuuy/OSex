package com.os.service;

import com.os.dto.Result;
import com.os.dto.VirtualMemoryDTO.SimulateRequest;
import com.os.entity.VMSimulationResult;

import java.util.List;

public interface VirtualMemoryService {
    VMSimulationResult simulate(List<Integer> pages, int blocks, SimulateRequest.Algorithm algorithm);
}
