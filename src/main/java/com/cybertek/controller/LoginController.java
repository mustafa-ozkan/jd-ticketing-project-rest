package com.cybertek.controller;

import com.cybertek.annotation.DefaultExceptionMessage;
import com.cybertek.dto.UserDTO;
import com.cybertek.entity.ResponseWrapper;
import com.cybertek.entity.User;
import com.cybertek.entity.common.AuthenticationRequest;
import com.cybertek.exception.TicketingProjectException;
import com.cybertek.mapper.MapperUtil;
import com.cybertek.service.UserService;
import com.cybertek.util.JWTUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

	private AuthenticationManager authenticationManager;
	private UserService userService;
	private MapperUtil mapperUtil;
	private JWTUtil jwtUtil;

	public LoginController(AuthenticationManager authenticationManager, UserService userService, MapperUtil mapperUtil, JWTUtil jwtUtil) {
		this.authenticationManager = authenticationManager;
		this.userService = userService;
		this.mapperUtil = mapperUtil;
		this.jwtUtil = jwtUtil;
	}

	@PostMapping("/authenticate")
	@DefaultExceptionMessage(defaultMessage = "Bad Credentials")
	public ResponseEntity<ResponseWrapper> doLogin(@RequestBody AuthenticationRequest authenticationRequest) throws TicketingProjectException {

		String password = authenticationRequest.getPassword();
		String username = authenticationRequest.getUsername();

		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
		authenticationManager.authenticate(authenticationToken);

		UserDTO foundUserDTO = userService.findByUserName(username);

		User convertedUser = mapperUtil.convert(foundUserDTO, new User());

		if (!foundUserDTO.isEnabled()){
			throw new TicketingProjectException("Please verify your user");
		}

		String jwtToken = jwtUtil.generateToken(convertedUser);

		return ResponseEntity.ok(new ResponseWrapper("Login successfull!",jwtToken));
	}





}
