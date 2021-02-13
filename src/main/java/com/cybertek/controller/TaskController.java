package com.cybertek.controller;

import com.cybertek.dto.TaskDTO;
import com.cybertek.entity.Project;
import com.cybertek.enums.Status;
import com.cybertek.service.ProjectService;
import com.cybertek.service.TaskService;
import com.cybertek.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/task")
public class TaskController {


    TaskService taskService;
    ProjectService projectService;
    UserService userService;

    //Injecting for spring
    public TaskController(TaskService taskService, ProjectService projectService, UserService userService) {
        this.taskService = taskService;
        this.projectService = projectService;
        this.userService = userService;
    }

    @GetMapping("/create")
    public String createTask(Model model) {

        model.addAttribute("task", new TaskDTO());
        model.addAttribute("projects", projectService.listAllNonCompletedProjects());
        model.addAttribute("employees", userService.listAllByRole("employee"));
        model.addAttribute("tasks", taskService.listAllTasks());

        return "task/create";
    }

    @PostMapping("/create")
    public String insertTask(TaskDTO task) {

        taskService.save(task);

        return "redirect:/task/create";
    }

    @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable("id") Long taskId) {
        taskService.delete(taskId);
        return "redirect:/task/create";
    }

    @GetMapping("/update/{id}")
    public String editTask(@PathVariable("id") Long id, Model model) {

        model.addAttribute("task", taskService.findById(id));
        model.addAttribute("projects", projectService.listAllNonCompletedProjects());
        model.addAttribute("employees", userService.listAllByRole("employee"));
        model.addAttribute("tasks", taskService.listAllTasks());

        return "task/update";
    }

    @PostMapping("/update/{id}")
    public String updateTask(TaskDTO task) {

        taskService.update(task);

        return "redirect:/task/create";


    }

    @GetMapping("/employee")
    public String edit(Model model) {
        //first of all i need to take all taskDto
        List<TaskDTO> taskDTOList = taskService.listAllTasksByStatusIsNot(Status.COMPLETE);
        model.addAttribute("tasks", taskDTOList);

        return "task/employee-tasks";

    }

    @GetMapping("/employee/edit/{id}")
    public String employee_update(@PathVariable("id") Long id, Model model) {
        TaskDTO taskDTO = taskService.findById(id);
//
        List<TaskDTO> taskDTOList = taskService.listAllTasksByStatusIsNot(Status.COMPLETE);

        model.addAttribute("task", taskDTO);
        model.addAttribute("users", userService.listAllByRole("employee"));
        model.addAttribute("projects", projectService.listAllNonCompletedProjects());
        model.addAttribute("tasks",taskDTOList);
        model.addAttribute("statuses",Status.values());

        return "task/employee-update";


    }

    @PostMapping("/employee/update/{id}")
    public String employee_update(@PathVariable("id") Long id, TaskDTO taskDTO){
        taskService.updateTaskStatus(taskDTO);
        return "redirect:/task/employee";
    }

    @GetMapping("/employee/archive")
    public String employee_archive(Model model){
        //Show me all the task dtos comes from taskservice
        List<TaskDTO> taskDTOList = taskService.listAllTasksByStatus(Status.COMPLETE);

        model.addAttribute("tasks",taskDTOList);

        return "task/employee-archive";


    }


}
