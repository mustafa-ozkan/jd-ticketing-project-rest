package com.cybertek.service;

import com.cybertek.dto.ProjectDTO;
import com.cybertek.dto.TaskDTO;
import com.cybertek.entity.Task;
import com.cybertek.entity.User;
import com.cybertek.enums.Status;

import java.util.List;

public interface TaskService {
    //I need crud operators
    TaskDTO findById(Long taskId);
    List<TaskDTO> listAllTasks();
    Task save(TaskDTO taskDto);
    void update(TaskDTO taskDTO);
    void delete(Long taskId);

    int totalNonCompletedTasks(String projectCode);
    int totalCompletedTasks(String projectCode);
    void deleteByProject(ProjectDTO projectDTO);
    List<TaskDTO> listAllTaskByProject(ProjectDTO projectDTO);
    List<TaskDTO> listAllTasksByStatusIsNot(Status taskStatus);
    List<TaskDTO> listAllTasksByProjectManager();

    List<TaskDTO> readAllTasksByEmployee(User employee);
    void updateTaskStatus(TaskDTO taskDTO);

    List<TaskDTO> listAllTasksByStatus(Status taskStatus);


}
