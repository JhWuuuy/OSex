# OsEx - 操作系统仿真演示平台

## 📖 项目简介
OsEx (Operating System Exercise) 是一个基于 Java Spring Boot 开发的操作系统原理仿真演示平台。该项目旨在通过可视化的方式，模拟操作系统核心组件的工作原理，帮助理解抽象的 OS 概念。

## ✨ 主要功能 (Features)

本项目包含四大核心模块的仿真：

### 1. 进程管理 (Process Management)
*   **核心类**: `ProcessController`, `ProcessService`
*   **功能**: 模拟进程的创建、调度、终止。
*   **支持算法**: (在此处填写你实现的算法，如：FCFS, RR, SJF 等)
*   **模拟模式**: 支持单步执行 (`step`) 和 连续执行 (`all`)。

### 2. 内存管理 (Memory Management)
*   **核心类**: `MemoryController`, `MemoryService`
*   **功能**: 模拟内存块的分配 (Allocation) 与回收 (Deallocation)。
*   **核心实体**: `MemoryBlock`

### 3. 虚拟内存 (Virtual Memory)
*   **核心类**: `VirtualMemoryController`, `VirtualMemoryService`
*   **功能**: 模拟页面置换算法及虚拟内存映射。

### 4. 磁盘管理 (Disk Management)
*   **核心类**: `DiskManageController`, `DiskManageService`
*   **功能**: 模拟磁盘寻道算法与文件存储路径。

## 🛠 技术栈 (Tech Stack)

*   **后端**: Java (JDK 1.8+), Spring Boot
*   **构建工具**: Maven
*   **前端**: HTML, JavaScript (位于 `src/main/resources/static/page.html`)
*   **模板引擎**: Thymeleaf (可选)

## 📂 项目结构 (Project Structure)

