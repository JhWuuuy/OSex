package com.os.service.impl;

import com.os.dto.DiskDTO.ScheduleRequest;
import com.os.entity.DiskResult;
import com.os.service.DiskManageService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 磁盘管理服务实现类
 */
@Service
public class DiskManageServiceImpl implements DiskManageService {

    @Override
    public DiskResult schedule(ScheduleRequest request) {
        String algorithm = request.getAlgorithm(); // 获取调度算法
        int startTrack = request.getStartTrack(); // 获取磁道开始位置
        List<Integer> trackSequence = new ArrayList<>(request.getTrackSequence()); // 获取磁道序列

        switch (algorithm) {
            case "FCFS":
                return scheduleFCFS(startTrack, trackSequence);
            case "SSTF":
                return scheduleSSTF(startTrack, trackSequence);
            case "SCAN":
                return scheduleSCAN(startTrack, trackSequence);
            default:
                return scheduleFCFS(startTrack, trackSequence); // 默认使用FCFS
        }
    }

    /**
     * FCFS（先来先服务）磁盘调度算法
     */
    private DiskResult scheduleFCFS(int startTrack, List<Integer> trackSequence) {
        List<Integer> serviceOrder = new ArrayList<>(); // 记录服务顺序
        int totalMovement = 0; // 记录总移动距离
        int currentTrack = startTrack;  // 当前磁道位置

        // 按照请求顺序依次处理
        for (int track : trackSequence) {
            // 计算移动距离
            int movement = Math.abs(track - currentTrack);
            totalMovement += movement;

            // 更新当前磁道位置
            currentTrack = track;

            // 添加到服务顺序
            serviceOrder.add(track);
        }

        return new DiskResult(totalMovement, serviceOrder);
    }

    /**
     * SSTF（最短寻道时间优先）磁盘调度算法
     */
    private DiskResult scheduleSSTF(int startTrack, List<Integer> trackSequence) {
        List<Integer> serviceOrder = new ArrayList<>(); // 记录服务顺序
        List<Integer> pendingTracks = new ArrayList<>(trackSequence); // 待处理的磁道请求
        int totalMovement = 0; // 记录总移动距离
        int currentTrack = startTrack; // 当前磁道位置

        // 循环处理所有请求
        while (!pendingTracks.isEmpty()) {
            int closestTrack = -1;
            int minDistance = Integer.MAX_VALUE;
            int closestIndex = -1;

            // 找到距离当前磁道最近的请求
            for (int i = 0; i < pendingTracks.size(); i++) {
                int distance = Math.abs(pendingTracks.get(i) - currentTrack);
                if (distance < minDistance) {
                    minDistance = distance;
                    closestTrack = pendingTracks.get(i);
                    closestIndex = i;
                }
            }

            // 计算移动距离
            totalMovement += minDistance;

            // 更新当前磁道位置
            currentTrack = closestTrack;

            // 添加到服务顺序
            serviceOrder.add(closestTrack);

            // 从待处理列表中移除已处理的请求
            pendingTracks.remove(closestIndex);
        }

        return new DiskResult(totalMovement, serviceOrder);
    }

    /**
     * SCAN（电梯）磁盘调度算法
     */
    private DiskResult scheduleSCAN(int startTrack, List<Integer> trackSequence) {
        List<Integer> serviceOrder = new ArrayList<>(); // 记录服务顺序
        List<Integer> pendingTracks = new ArrayList<>(trackSequence); // 待处理的磁道请求
        int totalMovement = 0; // 记录总移动距离
        int currentTrack = startTrack; // 当前磁道位置
        boolean movingUp = true; // 默认先向上（向磁道号增大的方向）移动

        // 将磁道请求分为两个列表：大于等于当前磁道的和小于当前磁道的
        List<Integer> upTracks = new ArrayList<>();
        List<Integer> downTracks = new ArrayList<>();

        for (int track : pendingTracks) {
            if (track >= currentTrack) {
                upTracks.add(track);
            } else {
                downTracks.add(track);
            }
        }

        // 对两个列表分别排序
        upTracks.sort(Integer::compareTo);
        downTracks.sort(Integer::compareTo);
        downTracks.sort(Collections.reverseOrder()); // 降序排序，从大到小

        // 先处理向上的请求
        for (int track : upTracks) {
            totalMovement += Math.abs(track - currentTrack);
            currentTrack = track;
            serviceOrder.add(track);
        }

        // 处理向下的请求
        for (int track : downTracks) {
            totalMovement += Math.abs(track - currentTrack);
            currentTrack = track;
            serviceOrder.add(track);
        }

        return new DiskResult(totalMovement, serviceOrder);
    }
}
