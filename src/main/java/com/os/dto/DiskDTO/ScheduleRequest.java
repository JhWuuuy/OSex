package com.os.dto.DiskDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 磁盘调度请求
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleRequest {
    private int startTrack; // 起始磁道位置
    private List<Integer> trackSequence; // 磁道访问序列
    String algorithm; // 磁盘调度算法名称，如FCFS、SSTF、SCAN等
}
