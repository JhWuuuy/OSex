package com.os.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 内存块类，用于表示内存分配的基本单位
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemoryBlock {
    private double start; // 内存块的起始地址
    private double size;  // 内存块的大小
    private Status status; // 内存块的状态：操作系统占用、空闲、已分配
    private Integer jobId;     // 分配给该内存块的作业ID
    private Integer partitionId;     // 分配给该内存块的分区ID

    /**
     * 内存块状态枚举
     * OS: 操作系统占用
     * FREE: 空闲状态
     * BUSY: 已分配状态
     */
    public enum Status {
        OS,FREE,BUSY
    }
}
