package com.os.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 虚拟存储器模拟步骤实体类
 * 用于记录页面置换算法执行过程中的每一步状态
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VMSimulationStep {
    private int page; // 当前访问的页面号
    private int[] frames; // 物理块状态，-1表示空闲
    private String status; // 状态：MISS-缺页, HIT-命中, 淘汰页面X-置换
}
