package com.escass.meltube.controllers;

import com.escass.meltube.entities.UserEntity;
import com.escass.meltube.results.Result;
import com.escass.meltube.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value ="/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @RequestMapping(value = "/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postIndex(HttpServletRequest request, UserEntity user) {
        JSONObject response = new JSONObject();
        Result result = this.userService.register(request, user);
        response.put(Result.NAME, result.nameToLower());
        return response.toString();
    }
}
