package com.os.dto.ProcessDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 运行模拟请求类
 * 用于封装运行模拟所需的参数信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RunSimulationRequest {
    private String algorithm; // "FCFS", "RR", "SJF"
    private String mode;      // "step" (单步) 或 "all" (运行到结束)
}