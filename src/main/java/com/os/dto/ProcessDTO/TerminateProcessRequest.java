package com.os.dto.ProcessDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 终止进程请求类
 * 用于封装终止进程请求所需的信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TerminateProcessRequest {
    // 进程名称
    private String name;
}