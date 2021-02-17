package com.cybertek.implementation;

import com.cybertek.dto.ProjectDTO;
import com.cybertek.dto.TaskDTO;
import com.cybertek.dto.UserDTO;
import com.cybertek.entity.User;
import com.cybertek.exception.TicketingProjectException;
import com.cybertek.mapper.MapperUtil;
import com.cybertek.mapper.ProjectMapper;
import com.cybertek.mapper.TaskMapper;
import com.cybertek.repository.UserRepository;
import com.cybertek.service.ProjectService;
import com.cybertek.service.TaskService;
import com.cybertek.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {


    UserRepository userRepository;
    MapperUtil mapperUtil;


    ProjectService projectService;
    ProjectMapper projectMapper;
    TaskService taskService;
    TaskMapper taskMapper;

    PasswordEncoder passwordEncoder;

    /*
    @Lazy to fix the error by not creating the projectService bean when it is not needed
     */

    public UserServiceImpl(UserRepository userRepository, MapperUtil mapperUtil, @Lazy ProjectService projectService, ProjectMapper projectMapper, TaskService taskService, TaskMapper taskMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.mapperUtil = mapperUtil;
        this.projectService = projectService;
        this.projectMapper = projectMapper;
        this.taskService = taskService;
        this.taskMapper = taskMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UserDTO> listAllUsers() {
        List<User> list = userRepository.findAll(Sort.by("firstName"));
        return list.stream().map(obj -> {
            return mapperUtil.convert(obj, new UserDTO());
        }).collect(Collectors.toList());
    }

    @Override
    public UserDTO findByUserName(String username) {
        User user = userRepository.findByUserName(username);
        return mapperUtil.convert(user,new UserDTO());
    }

    @Override
    public UserDTO save(UserDTO dto) throws TicketingProjectException {


        User foundUser = userRepository.findByUserName(dto.getUserName());
        dto.setEnabled(true);

        if(foundUser!=null){
            throw new TicketingProjectException("User already  exists");
        }

        User user = mapperUtil.convert(dto,new User());
        user.setPassWord(passwordEncoder.encode(user.getPassWord()));
        User savedUser = userRepository.save(user);

        return mapperUtil.convert(savedUser,new UserDTO());

    }

    @Override
    public UserDTO update(UserDTO dto) {

        //Find current user
        User user = userRepository.findByUserName(dto.getUserName());
        //Map update user dto to entity object
        User convertedUser = mapperUtil.convert(dto,new User());
        //set id to the converted object
        convertedUser.setId(user.getId());
        //set isEnabled true
        convertedUser.setEnabled(true);
        //save updated user
        userRepository.save(convertedUser);

        return findByUserName(dto.getUserName());
    }


    @Override
    public void delete(String username) throws TicketingProjectException{
        User user = userRepository.findByUserName(username);
        if(user == null){
            throw new TicketingProjectException("User Does Not Exist.");
        }

        if(!checkIfUserCanBeDeleted(user)){
            throw new TicketingProjectException("User Can not be deleted. User is linked by a project or a task");

        }

        user.setUserName(user.getUserName() + "-" + user.getId());
        user.setIsDeleted(true);
        userRepository.save(user);
    }

    @Override
    public List<UserDTO> listAllByRole(String role) {
        List<User> users = userRepository.findAllByRoleDescriptionIgnoreCase(role);
        return users.stream().map(obj -> {
            return mapperUtil.convert(obj, new UserDTO());
        }).collect(Collectors.toList());
    }

    /*
   check manager or employee
   check if any task available
   check if all the tasks are completed
    */
    @Override
    public Boolean checkIfUserCanBeDeleted(User user) {

        switch (user.getRole().getDescription()) {
//            it needs to match to dropdown list in UI
            case "Manager":
                List<ProjectDTO> projectDTOList = projectService.readAllByManager(user);
                return projectDTOList.size() == 0;
            case "Employee":
                List<TaskDTO> taskDTOList =taskService.readAllTasksByEmployee(user);
                return taskDTOList.size() == 0;

        }
        return null;
    }

    @Override
    public UserDTO confirm(User user) {

        user.setEnabled(true);
        User confirmedUser = userRepository.save(user);

        return mapperUtil.convert(confirmedUser, new UserDTO());
    }

    //hard delete
    public void deleteByUserName(String username) {
        userRepository.deleteByUserName(username);
    }
}