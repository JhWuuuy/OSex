package com.os.dto.VirtualMemoryDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 模拟请求类，用于封装内存分配模拟所需的参数
 * 使用了Lombok注解自动生成getter、setter、构造器等方法
 */
@Data // 自动为所有字段生成getter、setter、toString、equals、hashCode等方法
@AllArgsConstructor // 生成一个包含所有字段参数的全参构造器
@NoArgsConstructor // 生成一个无参构造器
public class SimulateRequest {
    private List<Integer> pages;  // 页面列表，用于存储页面引用序列
    private int blocks;     // 物理块的数量
    private Algorithm algorithm;     // 置换算法名称

    public enum Algorithm {
        FIFO,LRU,LFU
    }
}
