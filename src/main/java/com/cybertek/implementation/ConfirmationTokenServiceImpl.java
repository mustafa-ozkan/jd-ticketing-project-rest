package com.cybertek.implementation;

import com.cybertek.annotation.DefaultExceptionMessage;
import com.cybertek.dto.UserDTO;
import com.cybertek.entity.ConfirmationToken;
import com.cybertek.entity.ResponseWrapper;
import com.cybertek.exception.TicketingProjectException;
import com.cybertek.repository.ConfirmationTokenRepository;
import com.cybertek.service.ConfirmationTokenService;
import com.cybertek.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.catalina.Store;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Service
public class ConfirmationTokenServiceImpl implements ConfirmationTokenService {

    private ConfirmationTokenRepository confirmationTokenRepository;
    private JavaMailSender javaMailSender;
    private UserService userService;

    public ConfirmationTokenServiceImpl(ConfirmationTokenRepository confirmationTokenRepository, JavaMailSender javaMailSender) {
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.javaMailSender = javaMailSender;
    }

    @Override
    public ConfirmationToken save(ConfirmationToken confirmationToken) {
        return confirmationTokenRepository.save(confirmationToken);
    }

    @Override
    @Async
    public void sendEmail(SimpleMailMessage email) {
        javaMailSender.send(email);
    }

    @Override
    public ConfirmationToken readByToken(String token) throws TicketingProjectException {

        ConfirmationToken confirmationToken = confirmationTokenRepository.findByToken(token).orElse(null);

        if(confirmationToken==null){
            throw new TicketingProjectException("This token does not exists");
        }

        if(!confirmationToken.isTokenValid(confirmationToken.getExpireDate())){
            throw new TicketingProjectException("This token has been expired");
        }

        return confirmationToken;
    }

    @Override
    public void delete(ConfirmationToken confirmationToken) {

        confirmationToken.setIsDeleted(true);
        confirmationTokenRepository.save(confirmationToken);

    }


}
