
package com.os.dto.MemoryDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 内存回收请求
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeallocationRequest {
    private Integer jobId;  // 要回收的作业ID
}
