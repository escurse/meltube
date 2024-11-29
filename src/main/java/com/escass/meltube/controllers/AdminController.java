package com.escass.meltube.controllers;

import com.escass.meltube.entities.MusicEntity;
import com.escass.meltube.services.MusicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/admin")
@RequiredArgsConstructor
public class AdminController {
    private final MusicService musicService;

    @RequestMapping(value = "/musics", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public MusicEntity[] getMusics() {
        return this.musicService.getAllMusics(false);
    }


}
