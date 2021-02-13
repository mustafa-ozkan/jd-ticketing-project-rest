package com.cybertek.implementation;

import com.cybertek.dto.ProjectDTO;
import com.cybertek.dto.TaskDTO;
import com.cybertek.dto.UserDTO;
import com.cybertek.entity.Task;
import com.cybertek.entity.User;
import com.cybertek.enums.Status;
import com.cybertek.mapper.ProjectMapper;
import com.cybertek.mapper.TaskMapper;
import com.cybertek.repository.TaskRepository;
import com.cybertek.repository.UserRepository;
import com.cybertek.service.TaskService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    TaskRepository taskRepository;
    TaskMapper taskMapper;
    ProjectMapper projectMapper;
    UserRepository userRepository;

    public TaskServiceImpl(TaskRepository taskRepository, TaskMapper taskMapper, ProjectMapper projectMapper, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.projectMapper = projectMapper;
        this.userRepository = userRepository;
    }

    @Override
    public TaskDTO findById(Long taskId) {
        Optional<Task> task = taskRepository.findById(taskId);
        if (task.isPresent()) {
            return taskMapper.convertToDto(task.get());
        }
        return null;
    }

    @Override
    public List<TaskDTO> listAllTasks() {

        List<Task> taskList = taskRepository.findAll();

        return taskList.stream().map(obj -> {
            return taskMapper.convertToDto(obj);
        }).collect(Collectors.toList());
    }

    @Override
    public Task save(TaskDTO taskDto) {
        //we do not have status on the view then we need to set
        taskDto.setTaskStatus(Status.OPEN);
        //we do not have assigneddate so we need to set
        taskDto.setAssignedDate(LocalDate.now());
        Task task = taskMapper.convertToEntity(taskDto);


        return taskRepository.save(task);
    }

    @Override
    public void update(TaskDTO taskDTO) {

        Optional<Task> task = taskRepository.findById(taskDTO.getId());

        Task convertedTask = taskMapper.convertToEntity(taskDTO);
        if (task.isPresent()) {
            convertedTask.setId(task.get().getId());
            convertedTask.setTaskStatus(task.get().getTaskStatus());
            convertedTask.setAssignedDate(task.get().getAssignedDate());
            taskRepository.save(convertedTask);
        }
    }



    @Override
    public void delete(Long taskId) {
        Optional<Task> foundTask = taskRepository.findById(taskId);
        if (foundTask.isPresent()) {
            foundTask.get().setIsDeleted(true);
            taskRepository.save(foundTask.get());
        }

    }

    @Override
    public int totalNonCompletedTasks(String projectCode) {


        return taskRepository.totalNonCompletedTasks(projectCode);
    }

    @Override
    public int totalCompletedTasks(String projectCode) {


        return taskRepository.totalCompletedTasks(projectCode);
    }

    @Override
    public void deleteByProject(ProjectDTO projectDTO) {
        List<TaskDTO> taskDTOList = listAllTaskByProject(projectDTO);
        taskDTOList.stream().forEach(taskDTO -> delete(taskDTO.getId()));
    }

    @Override
    public List<TaskDTO> listAllTaskByProject(ProjectDTO projectDTO) {
        List<Task> taskList = taskRepository.findAllByProject(projectMapper.convertToEntity(projectDTO));

        return taskList.stream().map(obj -> {
            return taskMapper.convertToDto(obj);
        }).collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> listAllTasksByStatusIsNot(Status taskStatus) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUserName(username);
        List<Task> taskList = taskRepository.findAllByTaskStatusIsNotAndAssignedEmployee(taskStatus, user);
//return a dto basically a coverter needed
        return taskList.stream().map(taskMapper::convertToDto).collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> listAllTasksByProjectManager() {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUserName(username);
        List<Task> taskList = taskRepository.findAllByAssignedEmployee(user);


        return taskList.stream().map(taskMapper::convertToDto).collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> readAllTasksByEmployee(User employee) {
        //bring from the database and return it
        List<Task> taskList = taskRepository.findAllByAssignedEmployee(employee);
        return taskList.stream().map(taskMapper::convertToDto).collect(Collectors.toList());
    }

    @Override
    public void updateTaskStatus(TaskDTO taskDTO) {
        Optional<Task> foundTask = taskRepository.findById(taskDTO.getId());
        if(foundTask.isPresent()){
            foundTask.get().setTaskStatus(Status.COMPLETE);
            taskRepository.save(foundTask.get());
        }
    }

    @Override
    public List<TaskDTO> listAllTasksByStatus(Status taskStatus) {
        //bring all the tasks for certain employees

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUserName(username);
        //list all the tasks for the user by status
        List<Task> taskList = taskRepository.findAllByTaskStatusAndAssignedEmployee(taskStatus,user);
        //convert every task to dto
        return taskList.stream().map((taskMapper::convertToDto)).collect(Collectors.toList());
    }
}
