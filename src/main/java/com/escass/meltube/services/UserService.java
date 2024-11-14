package com.escass.meltube.services;

import com.escass.meltube.entities.UserEntity;
import com.escass.meltube.results.CommonResult;
import com.escass.meltube.results.Result;
import com.escass.meltube.results.user.RegisterResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    public Result register(UserEntity user) {
        if (true) {
            return CommonResult.FAILURE;
        }
        if (true) {
            return RegisterResult.FAILURE_DUPLICATE_EMAIL;
        }
        return CommonResult.SUCCESS;
    }
}
