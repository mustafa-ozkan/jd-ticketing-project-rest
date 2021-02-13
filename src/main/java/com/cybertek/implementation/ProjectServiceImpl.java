package com.cybertek.implementation;

import com.cybertek.dto.ProjectDTO;
import com.cybertek.dto.UserDTO;
import com.cybertek.entity.Project;
import com.cybertek.entity.User;
import com.cybertek.enums.Status;
import com.cybertek.mapper.ProjectMapper;
import com.cybertek.mapper.UserMapper;
import com.cybertek.repository.ProjectRepository;
import com.cybertek.service.ProjectService;
import com.cybertek.service.TaskService;
import com.cybertek.service.UserService;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {

    private ProjectMapper projectMapper;
    private ProjectRepository projectRepository;
    private UserService userService;
    private UserMapper userMapper;
    private TaskService taskService;

    public ProjectServiceImpl(ProjectMapper projectMapper, ProjectRepository projectRepository, UserService userService, UserMapper userMapper, TaskService taskService) {
        this.projectMapper = projectMapper;
        this.projectRepository = projectRepository;
        this.userService = userService;
        this.userMapper = userMapper;
        this.taskService = taskService;
    }

    @Override
    public ProjectDTO getByProjectCode(String projectCode) {
        //get the project entity first
        Project project = projectRepository.findByProjectCode(projectCode);
        //convert entity to dto
        return projectMapper.convertToDto(project);
    }

    @Override
    public List<ProjectDTO> listAllProjects() {
        List<Project> list = projectRepository.findAll(Sort.by("projectCode"));
        return list.stream().map(obj -> projectMapper.convertToDto(obj)).collect(Collectors.toList());
    }

    @Override
    public List<ProjectDTO> listAllProjectDetails() {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDTO currentUserDto = userService.findByUserName(username);
        User user = userMapper.convertToEntity(currentUserDto);
        List<Project> projectList = projectRepository.findAllByAssignedManager(user);

        return projectList.stream().map(project -> {
            ProjectDTO obj = projectMapper.convertToDto(project);
            obj.setUnfinishedTaskCounts(taskService.totalNonCompletedTasks(project.getProjectCode()));
            obj.setCompleteTaskCounts(taskService.totalCompletedTasks(project.getProjectCode()));
            return obj;
        }).collect(Collectors.toList());

    }


    @Override
    public List<ProjectDTO> readAllByManager(User manager) {

        List<Project> projectList = projectRepository.findAllByAssignedManager(manager);
        return projectList.stream().map(projectMapper::convertToDto).collect(Collectors.toList());
    }

    @Override
    public void save(ProjectDTO projectDTO) {
        projectDTO.setProjectStatus(Status.OPEN);
        Project project = projectMapper.convertToEntity(projectDTO);
        projectRepository.save(project);
    }

    @Override
    public void update(ProjectDTO projectDTO) {
        //get this one from the repository
        Project project = projectRepository.findByProjectCode(projectDTO.getProjectCode());
        //convert to entity
        Project convertedProject = projectMapper.convertToEntity(projectDTO);
        //set convertedproject id to current one(to keep the id)
        convertedProject.setId(project.getId());
        //set converted project status to current one(to keep the status)
        convertedProject.setProjectStatus(project.getProjectStatus());
        //save this project
        projectRepository.save(convertedProject);

    }

    @Override
    public void delete(String projectCode) {
//get this one from the repository
        Project project = projectRepository.findByProjectCode(projectCode);
        //set si_deleted to true
        project.setIsDeleted(true);

        //you can do below to be able to use the same projectCode again for a new project
        project.setProjectCode(project.getProjectCode() + "-" + project.getId());

        //delete all the tasks under the project
        taskService.deleteByProject(projectMapper.convertToDto(project));
//save this project
        projectRepository.save(project);
    }

    @Override
    public void complete(String projectCode) {
        //get this one from the repository
        Project project = projectRepository.findByProjectCode(projectCode);
        //set status to complete
        project.setProjectStatus(Status.COMPLETE);
        //save this project
        projectRepository.save(project);
    }

    @Override
    public List<ProjectDTO> listAllNonCompletedProjects() {

        return projectRepository.findAllByProjectStatusIsNot(Status.COMPLETE)
                .stream()
                .map(project -> projectMapper.convertToDto(project))
                .collect(Collectors.toList());
    }


}
