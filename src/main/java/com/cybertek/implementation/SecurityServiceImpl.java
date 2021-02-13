package com.cybertek.implementation;

import com.cybertek.dto.UserDTO;
import com.cybertek.entity.User;
import com.cybertek.entity.common.UserPrincipal;
import com.cybertek.mapper.MapperUtil;
import com.cybertek.repository.UserRepository;
import com.cybertek.service.SecurityService;
import com.cybertek.service.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class SecurityServiceImpl implements SecurityService {

    private UserService userService;
    private MapperUtil mapperUtil;

    public SecurityServiceImpl(UserService userService, MapperUtil mapperUtil) {
        this.userService = userService;
        this.mapperUtil = mapperUtil;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        UserDTO userDTO= userService.findByUserName(s);

        if(userDTO==null){
            throw new UsernameNotFoundException("This user does not exists");
        }

        return new org.springframework.security.core.userdetails.User(userDTO.getId().toString(),
                userDTO.getPassWord(),listAuthorities(userDTO));
    }

    @Override
    public User loadUser(String value) {
        UserDTO userDTO = userService.findByUserName(value);
        return mapperUtil.convert(userDTO, new User());
    }

    private Collection<? extends GrantedAuthority> listAuthorities(UserDTO userDTO){
        List<GrantedAuthority> authorityList = new ArrayList<>();
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(userDTO.getRole().getDescription());
        authorityList.add(grantedAuthority);
        return authorityList;
    }
}
