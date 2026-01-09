package com.os.controller;

import com.os.dto.Result;
import com.os.dto.VirtualMemoryDTO.SimulateRequest;
import com.os.service.VirtualMemoryService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/vm")
public class VirtualMemoryController {

    private VirtualMemoryService virtualMemoryService;

    @Resource
    public void setVirtualMemoryService(VirtualMemoryService virtualMemoryService) {
        this.virtualMemoryService = virtualMemoryService;
    }

    @PostMapping("/simulate")
    public Result simulate(@RequestBody SimulateRequest request) {
        List<Integer> pages = request.getPages();
        int blocks = request.getBlocks();
        SimulateRequest.Algorithm algorithm = request.getAlgorithm();

        return Result.ok("模拟成功", virtualMemoryService.simulate(pages, blocks, algorithm));
    }
}
