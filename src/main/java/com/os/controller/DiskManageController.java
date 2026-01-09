package com.os.controller;

import com.os.dto.DiskDTO.ScheduleRequest;
import com.os.dto.Result;
import com.os.entity.DiskResult;
import com.os.service.DiskManageService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 磁盘管理控制器
 */
@RestController
@RequestMapping("/api/disk")
public class DiskManageController {

    private DiskManageService diskManageService;

    @Resource
    public void setDiskManageService(DiskManageService diskManageService) {
        this.diskManageService = diskManageService;
    }

    /**
     * 磁盘调度算法模拟
     */
    @PostMapping("/schedule")
    public Result schedule(@RequestBody ScheduleRequest request) {
        DiskResult result = diskManageService.schedule(request);
        return Result.ok("磁盘调度成功", result);
    }
}
