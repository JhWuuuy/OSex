package com.os.service.impl;

import com.os.dto.MemoryDTO.AllocationRequest;
import com.os.dto.MemoryDTO.DeallocationRequest;
import com.os.dto.MemoryDTO.InitializationRequest;
import com.os.entity.MemoryBlock;
import com.os.service.MemoryService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MemoryServiceImpl implements MemoryService {

    // 内存总大小：128K
    private static final int TOTAL_MEMORY = 128;
    // OS占用内存：4K
    private static final int OS_MEMORY = 4;
    // 用户可用内存起始地址：4K
    private static final double USER_MEMORY_START = OS_MEMORY;

    // 内存块列表
    private List<MemoryBlock> memoryBlocks;
    // 是否为固定分区模式
    private boolean isFixedPartitionMode;

    private final List<Double> defaultPartitionSizes = List.of(124.0);

    /**
     * 构造函数，初始化内存
     */
    public MemoryServiceImpl() {
        reset();
    }

    @Override
    public List<MemoryBlock> getStatus() {
        return new ArrayList<>(memoryBlocks);
    }

    @Override
    public List<MemoryBlock> reset() {
        // 默认使用可变分区模式初始化
        InitializationRequest request = new InitializationRequest(false, defaultPartitionSizes);
        return initialize(request);
    }

    @Override
    public List<MemoryBlock> initialize(InitializationRequest request) {
        isFixedPartitionMode = request.isFixed();

        // 创建新的内存块列表
        memoryBlocks = new ArrayList<>();

        // 添加OS占用的内存块
        memoryBlocks.add(new MemoryBlock(0, OS_MEMORY, MemoryBlock.Status.OS, null, null));

        double sizeSum = 0;
        if (isFixedPartitionMode) {
            // 固定分区模式
            double currentStart = USER_MEMORY_START;
            int partitionId = 1;


            for (double size : request.getPartitionSizes()) {
                memoryBlocks.add(new MemoryBlock(currentStart, size, MemoryBlock.Status.FREE, null, partitionId));
                currentStart += size;
                partitionId++;

                sizeSum += size;
            }

            // 判断请求内存分区大小是否符合要求
            if(sizeSum != TOTAL_MEMORY - OS_MEMORY) {
                return null;
            }

        } else {
            // 可变分区模式，初始时整个用户空间是一个大的空闲块
            memoryBlocks.add(new MemoryBlock(USER_MEMORY_START, TOTAL_MEMORY - OS_MEMORY, MemoryBlock.Status.FREE, null, null));
        }

        return new ArrayList<>(memoryBlocks);
    }

    @Override
    public String allocate(AllocationRequest request) {
        // 检查请求是否有效
        if (request.getSize() <= 0) {
            return "分配失败：请求的内存大小必须大于0";
        }
        
        // 将请求的大小转换为整数，向上取整确保有足够的空间
        double requiredSize = request.getSize();

        // 根据不同的分配算法查找合适的内存块
        MemoryBlock selectedBlock = null;
        String algorithm = request.getAlgorithm();

        if ("FF".equals(algorithm)) {
            // 首次适应算法：从低地址开始查找第一个足够大的空闲块
            selectedBlock = memoryBlocks.stream()
                    .filter(block -> block.getStatus() == MemoryBlock.Status.FREE && block.getSize() >= requiredSize)
                    .findFirst()
                    .orElse(null);
        } else if ("BF".equals(algorithm)) {
            // 最佳适应算法：选择满足条件的最小空闲块
            selectedBlock = memoryBlocks.stream()
                    .filter(block -> block.getStatus() == MemoryBlock.Status.FREE && block.getSize() >= requiredSize)
                    .min(Comparator.comparingDouble(MemoryBlock::getSize))
                    .orElse(null);
        } else if ("WF".equals(algorithm)) {
            // 最坏适应算法：选择满足条件的最大空闲块
            selectedBlock = memoryBlocks.stream()
                    .filter(block -> block.getStatus() == MemoryBlock.Status.FREE && block.getSize() >= requiredSize)
                    .max(Comparator.comparingDouble(MemoryBlock::getSize))
                    .orElse(null);
        }

        if (selectedBlock == null) {
            return "分配失败：没有足够的内存空间";
        }

        // 固定分区模式
        if (isFixedPartitionMode) {
            selectedBlock.setStatus(MemoryBlock.Status.BUSY);
            selectedBlock.setJobId(request.getJobId());
            return "分配成功：作业" + request.getJobId() + "分配了" + request.getSize() + "K内存";
        }

        // 可变分区模式
        double blockSize = selectedBlock.getSize();
        double blockStart = selectedBlock.getStart();

        // 如果选中块的大小正好等于请求大小，直接分配
        if (blockSize == requiredSize) {
            selectedBlock.setStatus(MemoryBlock.Status.BUSY);
            selectedBlock.setJobId(request.getJobId());
            return "分配成功：作业" + request.getJobId() + "分配了" + request.getSize() + "K内存";
        }

        // 否则需要分割内存块
        // 1. 更新原块为已分配状态
        selectedBlock.setSize(requiredSize);
        selectedBlock.setStatus(MemoryBlock.Status.BUSY);
        selectedBlock.setJobId(request.getJobId());

        // 2. 创建新的空闲块
        double newBlockStart = blockStart + requiredSize;
        double newBlockSize = blockSize - requiredSize;
        MemoryBlock newBlock = new MemoryBlock(newBlockStart, newBlockSize, MemoryBlock.Status.FREE, null, null);

        // 3. 将新块插入到原块之后
        int index = memoryBlocks.indexOf(selectedBlock);
        memoryBlocks.add(index + 1, newBlock);

        return "分配成功：作业" + request.getJobId() + "分配了" + request.getSize() + "K内存";
    }

    @Override
    public String free(DeallocationRequest request) {
        // 查找指定作业ID的内存块
        List<MemoryBlock> jobBlocks = memoryBlocks.stream()
                .filter(block -> block.getJobId() != null && block.getJobId().equals(request.getJobId()))
                .toList();

        if (jobBlocks.isEmpty()) {
            return "回收失败：未找到作业ID为" + request.getJobId() + "的内存块";
        }

        // 标记这些块为空闲
        jobBlocks.forEach(block -> {
            block.setStatus(MemoryBlock.Status.FREE);
            block.setJobId(null);
        });

        // 在可变分区模式下，需要合并相邻的空闲块
        if (!isFixedPartitionMode) {
            mergeAdjacentFreeBlocks();
        }

        return "回收成功：作业ID为" + request.getJobId() + "的内存已回收";
    }

    /**
     * 合并相邻的空闲块（仅用于可变分区模式）
     */
    private void mergeAdjacentFreeBlocks() {
        // 按起始地址排序
        memoryBlocks.sort(Comparator.comparingDouble(MemoryBlock::getStart));

        List<MemoryBlock> mergedBlocks = new ArrayList<>();
        MemoryBlock currentBlock = null;

        for (MemoryBlock block : memoryBlocks) {
            if (currentBlock == null) {
                currentBlock = block;
            } else if (currentBlock.getStatus() == MemoryBlock.Status.FREE && 
                      block.getStatus() == MemoryBlock.Status.FREE ) {
                // 合并两个相邻的空闲块
                currentBlock.setSize(currentBlock.getSize() + block.getSize());
            } else {
                mergedBlocks.add(currentBlock);
                currentBlock = block;
            }
        }

        if (currentBlock != null) {
            mergedBlocks.add(currentBlock);
        }

        memoryBlocks = mergedBlocks;
    }
}

