package com.os.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 虚拟存储器模拟结果实体类
 * 用于存储页面置换算法执行后的完整结果
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VMSimulationResult {
    private int totalPageFaults; //总缺页次数
    private double pageFaultRate; //缺页率
    private List<VMSimulationStep> steps; //模拟步骤数组
}
