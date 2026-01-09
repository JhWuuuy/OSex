package com.os.controller;

import com.os.dto.MemoryDTO.AllocationRequest;
import com.os.dto.MemoryDTO.DeallocationRequest;
import com.os.dto.MemoryDTO.InitializationRequest;
import com.os.dto.Result;
import com.os.entity.MemoryBlock;
import com.os.service.MemoryService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/memory")
public class MemoryController {

    private MemoryService memoryService;

    @Resource
    public void setMemoryService(MemoryService memoryService) {
        this.memoryService = memoryService;
    }

    /**
     * 初始化内存
     */
    @PostMapping("/initialize")
    public Result initialize(@RequestBody InitializationRequest request) {
        List<MemoryBlock> memoryBlocks = memoryService.initialize(request);

        if(!memoryBlocks.isEmpty()) return Result.ok("内存初始化成功", memoryBlocks);
        return Result.fail("内存初始化失败");
    }

    /**
     * 分配内存
     */
    @PostMapping("/allocate")
    public Result allocate(@RequestBody AllocationRequest request) {
        String message = memoryService.allocate(request);
        List<MemoryBlock> memoryBlocks = memoryService.getStatus();
        return Result.ok(message, memoryBlocks);
    }
    
    /**
     * 根据作业ID和大小分配内存
     */
    @PostMapping("/allocateByJob")
    public Result allocateByJob(@RequestBody AllocationRequest request) {
        // 这个方法与allocate方法功能相同，只是名称不同，用于前端调用
        return allocate(request);
    }

    /**
     * 回收内存
     */
    @PostMapping("/free")
    public Result free(@RequestBody DeallocationRequest request) {
        String message = memoryService.free(request);
        List<MemoryBlock> memoryBlocks = memoryService.getStatus();
        return Result.ok(message, memoryBlocks);
    }

    /**
     * 获取内存状态
     */
    @GetMapping("/status")
    public Result getStatus() {
        List<MemoryBlock> memoryBlocks = memoryService.getStatus();
        return Result.ok("获取内存状态成功", memoryBlocks);
    }

    /**
     * 重置内存
     */
    @PostMapping("/reset")
    public Result reset() {
        List<MemoryBlock> memoryBlocks = memoryService.reset();
        return Result.ok("内存重置成功", memoryBlocks);
    }
}
