package com.escass.meltube.controllers;

import com.escass.meltube.entities.MusicEntity;
import com.escass.meltube.results.Result;
import com.escass.meltube.services.MusicService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Controller
@RequestMapping(value = "/music")
@RequiredArgsConstructor
public class MusicController {
    private final MusicService musicService;

    @RequestMapping(value = "/crawl-melon", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public MusicEntity getCrawlMelon(@RequestParam(value = "id", required = false) String id) throws IOException {
        return this.musicService.crawlMelon(id);
    }

    @RequestMapping(value = "/search-melon", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public MusicEntity[] getSearchMelon(@RequestParam(value = "keyword", required = false) String keyword) throws IOException, InterruptedException {
        return this.musicService.searchMelon(keyword);
    }

    @RequestMapping(value = "/verify-youtube-id", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getVerifyYoutubeId(@RequestParam(value = "id", required = false) String id) throws IOException, InterruptedException {
        boolean result = this.musicService.verifyYoutubeId(id);
        JSONObject response = new JSONObject();
        response.put(Result.NAME, result);
        return response.toString();
    }
}
