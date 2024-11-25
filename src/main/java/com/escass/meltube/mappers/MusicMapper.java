package com.escass.meltube.mappers;

import com.escass.meltube.entities.MusicEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MusicMapper {
    int insertMusic(MusicEntity music);

    MusicEntity selectMusicByYoutubeId(@Param("youtubeId") String youtubeId);
}
