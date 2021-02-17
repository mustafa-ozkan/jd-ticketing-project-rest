package com.cybertek.service;


import com.cybertek.dto.UserDTO;
import com.cybertek.entity.User;
import com.cybertek.exception.TicketingProjectException;

import java.util.List;

public interface UserService {

    List<UserDTO> listAllUsers();
    UserDTO findByUserName(String userName);
    UserDTO save(UserDTO userDTO) throws TicketingProjectException;
    UserDTO update(UserDTO userDTO);
    void delete(String userName) throws TicketingProjectException;
    List<UserDTO> listAllByRole(String role);

    Boolean checkIfUserCanBeDeleted(User user);

    UserDTO confirm(User user);
}
