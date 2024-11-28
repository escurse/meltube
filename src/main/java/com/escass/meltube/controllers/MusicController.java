package com.escass.meltube.controllers;

import com.escass.meltube.entities.MusicEntity;
import com.escass.meltube.entities.UserEntity;
import com.escass.meltube.results.CommonResult;
import com.escass.meltube.results.Result;
import com.escass.meltube.services.MusicService;
import com.escass.meltube.vos.ResultVo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @RequestMapping(value = "/cover", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<byte[]> getCover(@RequestParam(value = "index", required = false) Integer index) {
        MusicEntity music = this.musicService.getMusicByIndex(index, true);
        if (music == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(music.getCoverContentType()))
                .contentLength(music.getCoverData().length)
                .body(music.getCoverData());
    }

    @RequestMapping(value = "/", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String deleteIndex(@SessionAttribute(value = "user", required = false) UserEntity user,
                              @RequestParam(value = "indexes", required = false) int[] indexes) {
        JSONObject response = new JSONObject();
        Result result = this.musicService.withdrawInquiries(user, indexes);
        response.put(Result.NAME, result.nameToLower());
        return response.toString();
    }

    @RequestMapping(value = "/inquiries", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getInquiries(@SessionAttribute(value = "user", required = false) UserEntity user) throws JsonProcessingException {
        JSONObject response = new JSONObject();
        ResultVo<Result, MusicEntity[]> result = this.musicService.getMusicInquiriesByUser(user);
        response.put(Result.NAME, result.getResult().nameToLower());
        if (result.getResult() == CommonResult.SUCCESS) {
            JSONArray musics = new JSONArray();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            // ObjectMapper는 기본적으로 Java에 내장된 LocalDateTime 및 LocalDate를 변환하는 기능을 내포하지 않음.
            // Mapping된 메서드에서 사용하는 ObjectMapper는 스프링의 설정에 의해서 JavaTimeModule을 기본적으로 등록함.
            for (MusicEntity music : result.getPayload()) {
//                String s = objectMapper.writeValueAsString(music); // "{index: 7, name: \"해야\", artist: ~}"
                JSONObject musicObject = new JSONObject(objectMapper.writeValueAsString(music));
                musics.put(musicObject);
            }
            response.put("musics", musics);
        }
        return response.toString();
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

    @RequestMapping(value = "/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postIndex(@SessionAttribute(value = "user", required = false) UserEntity user,
                            @RequestParam(value = "_cover", required = false) MultipartFile _cover,
                            MusicEntity music) throws IOException, InterruptedException {
        JSONObject response = new JSONObject();
        Result result = this.musicService.addMusic(user, music, _cover);
        response.put(Result.NAME, result.nameToLower());
        return response.toString();
    }
}
