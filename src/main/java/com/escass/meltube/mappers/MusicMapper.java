package com.escass.meltube.mappers;

import com.escass.meltube.entities.MusicEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MusicMapper {
    int insertMusic(MusicEntity music);
    int updateMusic(@Param("music") MusicEntity music,
                    @Param("includeCover") boolean includeCover);

    MusicEntity selectMusicByIndex(@Param("index") int index,
                                   @Param("includeCover") boolean includeCover);

    MusicEntity selectMusicByYoutubeId(@Param("youtubeId") String youtubeId);

    MusicEntity[] selectMusicsByUserEmail(@Param("userEmail") String userEmail);
}
