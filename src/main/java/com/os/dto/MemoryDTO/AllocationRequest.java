
package com.os.dto.MemoryDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 内存分配请求
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllocationRequest {
    private Integer jobId;  // 作业ID
    private double size;    // 请求的内存大小(K)
    private String algorithm; // 分配算法: FF, BF, WF
}
