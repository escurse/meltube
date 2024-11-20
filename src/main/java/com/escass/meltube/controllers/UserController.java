package com.escass.meltube.controllers;

import com.escass.meltube.entities.EmailTokenEntity;
import com.escass.meltube.entities.UserEntity;
import com.escass.meltube.results.CommonResult;
import com.escass.meltube.results.Result;
import com.escass.meltube.services.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Generated;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value ="/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getIndex(HttpSession session, UserEntity user) {
        Result result = this.userService.login(user);
        if (result == CommonResult.SUCCESS) {
            session.setAttribute("user", user);
        }
        JSONObject response = new JSONObject();
        response.put(Result.NAME, result.nameToLower());
        return response.toString();
    }

    @RequestMapping(value = "/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postIndex(HttpServletRequest request, UserEntity user) throws MessagingException {
        JSONObject response = new JSONObject();
        Result result = this.userService.register(request, user);
        response.put(Result.NAME, result.nameToLower());
        return response.toString();
    }

    @RequestMapping(value = "/validate-email-token", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getValidateEmailToken(EmailTokenEntity emailToken) {
        Result result = this.userService.validateEmailToken(emailToken);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject(Result.NAME, result.nameToLower());
        modelAndView.setViewName("user/validateEmailToken");
        return modelAndView;
    }
}
