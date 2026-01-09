package com.os.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 磁盘调度结果实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiskResult {
    private int totalMovement; // 磁头总移动道数
    List<Integer> serviceOrder; // 磁道访问顺序
}
