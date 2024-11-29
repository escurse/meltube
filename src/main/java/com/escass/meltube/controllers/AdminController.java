package com.escass.meltube.controllers;

import com.escass.meltube.entities.MusicEntity;
import com.escass.meltube.results.Result;
import com.escass.meltube.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @RequestMapping(value = "/music/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public MusicEntity[] getMusicIndex() {
        return this.adminService.getMusics();
    }

    //region Music
    @RequestMapping(value = "/music/", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String deleteMusicIndex(@RequestParam(value = "indexes", required = false) int[] indexes) {
        JSONObject response = new JSONObject();
        Result result = this.adminService.deleteMusics(indexes);
        response.put(Result.NAME, result.nameToLower());
        return response.toString();
    }

    @RequestMapping(value = "/music/status", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchMusicStatus(@RequestParam(value = "status", required = false) Boolean status,
                                   @RequestParam(value = "indexes", required = false) int[] indexes) {
        JSONObject response = new JSONObject();
        Result result = this.adminService.ModifyMusicStatuses(status, indexes);
        response.put(Result.NAME, result.nameToLower());
        return response.toString();
    }
    //endregion
}
