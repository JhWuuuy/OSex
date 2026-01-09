package com.os.service;

import com.os.dto.MemoryDTO.AllocationRequest;
import com.os.dto.MemoryDTO.DeallocationRequest;
import com.os.dto.MemoryDTO.InitializationRequest;
import com.os.entity.MemoryBlock;
import java.util.List;

public interface MemoryService {
    /**
     * 初始化内存
     * @param request 初始化请求
     * @return 内存块列表
     */
    List<MemoryBlock> initialize(InitializationRequest request);

    /**
     * 分配内存
     * @param request 分配请求
     * @return 分配结果信息
     */
    String allocate(AllocationRequest request);

    /**
     * 回收内存
     * @param request 回收请求
     * @return 回收结果信息
     */
    String free(DeallocationRequest request);

    /**
     * 获取内存状态
     * @return 内存块列表
     */
    List<MemoryBlock> getStatus();

    /**
     * 重置内存
     * @return 重置后的内存块列表
     */
    List<MemoryBlock> reset();
}

