package com.os.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 磁头移动路径类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiskPath {
    private int track; // 磁道号
    private boolean isRequest; // 是否是请求点
}
