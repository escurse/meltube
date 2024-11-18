package com.escass.meltube.services;

import com.escass.meltube.entities.EmailTokenEntity;
import com.escass.meltube.entities.UserEntity;
import com.escass.meltube.exceptions.TransactionalException;
import com.escass.meltube.mappers.EmailTokenMapper;
import com.escass.meltube.mappers.UserMapper;
import com.escass.meltube.results.CommonResult;
import com.escass.meltube.results.Result;
import com.escass.meltube.results.user.RegisterResult;
import com.escass.meltube.utils.CryptoUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {
    private final EmailTokenMapper emailTokenMapper;
    private final UserMapper userMapper;
    private final JavaMailSender mailSender; // mail 보내는 역할
    private final SpringTemplateEngine templateEngine; // html 끌고와서 mailSender 로 보내고

    @Transactional // 하나라도 실패할 시 없던 일로 한다.
    public Result register(HttpServletRequest request, UserEntity user) {
        if (user == null ||
            user.getEmail() == null || user.getEmail().length() < 8 || user.getEmail().length() > 50 ||
            user.getPassword() == null || user.getPassword().length() < 6 || user.getPassword().length() > 50 ||
            user.getNickname() == null || user.getNickname().length() < 2 || user.getNickname().length() > 10 ||
            user.getContact() == null || user.getContact().length() < 10 || user.getContact().length() > 12) {
            return CommonResult.FAILURE;
        }
        if (this.userMapper.selectUserByEmail(user.getEmail()) != null) {
            return RegisterResult.FAILURE_DUPLICATE_EMAIL;
        }
        if (this.userMapper.selectUserByContact(user.getContact()) != null) {
            return RegisterResult.FAILURE_DUPLICATE_CONTACT;
        }
        if (this.userMapper.selectUserByNickname(user.getNickname()) != null) {
            return RegisterResult.FAILURE_DUPLICATE_NICKNAME;
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setDeletedAt(null);
        user.setAdmin(false);
        user.setSuspended(false);
        user.setVerified(false);
        if (this.userMapper.insertUser(user) == 0) {
            throw new TransactionalException();
        }
        EmailTokenEntity emailToken = new EmailTokenEntity();
        emailToken.setUserEmail(user.getEmail());
        emailToken.setKey(CryptoUtils.hashSha512(String.format("%s%s%f%f",
                user.getEmail(),
                user.getPassword(),
                Math.random(),
                Math.random())));
        emailToken.setCreatedAt(LocalDateTime.now());
        emailToken.setExpiresAt(LocalDateTime.now().plusHours(24));
        emailToken.setUsed(false);
        if (this.emailTokenMapper.insertEmailToken(emailToken) == 0) {
            throw new TransactionalException();
        }
        String validationLink = String.format("%s://%s:%d/user/validate-email-token?email=%s&key=%s",
                request.getScheme(),
                request.getServerName(),
                request.getServerPort(),
                emailToken.getUserEmail(),
                emailToken.getKey());
        Context context = new Context(); // org.thymeleaf.context
        context.setVariable("validationLink", validationLink);
        String mailText = this.templateEngine.process("email/register", context);
        return CommonResult.SUCCESS;
    }
}
