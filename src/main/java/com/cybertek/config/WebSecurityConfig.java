package com.cybertek.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


//we created this class to enable security(it has 4 steps)
//
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
//
//    @Autowired
//    private SecurityFilter securityFilter;


//we created below bean for doing authentication without a form
//(by using below bean i will create a method to handle authentication)
//we were doing this authentication with form login for UI
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    private static final String[] permittedUrls = {
            "/authenticate",
            "/create-user",
            "/confirmation",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/configuration/security",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/webjars/**"
    };

    @Override
    protected void configure(HttpSecurity http) throws Exception{

        http
                .csrf()//in the middle somebody can attack if this is enabled
                .disable()
                .authorizeRequests()
                //to get a token we put below endpoint for everybody(permitall)
                .antMatchers(permittedUrls)
                .permitAll()
                .anyRequest()
                .authenticated();

//        http
//                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);

    }
}
