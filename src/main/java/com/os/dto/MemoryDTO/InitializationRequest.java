
package com.os.dto.MemoryDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 内存初始化请求
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InitializationRequest {
    private boolean isFixed;  // 是否为固定分区方式
    private List<Double> partitionSizes;  // 固定分区的大小列表(K)
}
