package com.os.service;

import com.os.dto.DiskDTO.ScheduleRequest;
import com.os.entity.DiskResult;

/**
 * 磁盘管理服务接口
 */
public interface DiskManageService {
    /**
     * 磁盘调度算法模拟
     */
    DiskResult schedule(ScheduleRequest request);
}
