package com.os.dto.ProcessDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 进程请求类
 * 用于封装添加进程时所需的各项参数信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddProcessRequest {
    private String name; //进程名字，用于标识和区分不同进程
    private int arriveTime; //到达时间，表示进程进入系统的时刻
    private int serviceTime; //服务时间，表示进程需要运行的总时间
    private int priority; //进程优先级，数值越大优先级越高
}