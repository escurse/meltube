package com.escass.meltube.services;

import com.escass.meltube.entities.MusicEntity;
import com.escass.meltube.exceptions.TransactionalException;
import com.escass.meltube.mappers.MusicMapper;
import com.escass.meltube.results.CommonResult;
import com.escass.meltube.results.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final MusicMapper musicMapper;

    //region Music
    @Transactional
    public Result deleteMusics(int[] indexes) {
        if (indexes == null || indexes.length == 0) {
            return CommonResult.FAILURE;
        }
        for (int index : indexes) {
            MusicEntity music = this.musicMapper.selectMusicByIndex(index, false);
            if (music == null || music.isDeleted()) {
                throw new TransactionalException();
            }
            music.setDeleted(true);
            if (this.musicMapper.updateMusic(music, false) == 0) {
                throw new TransactionalException();
            }
        }
        return CommonResult.SUCCESS;
    }

    public MusicEntity[] getMusics() {
        return this.musicMapper.selectMusics(false);
    }

    @Transactional
    public Result ModifyMusicStatuses(Boolean status, int[] indexes) {
        if (status == null || indexes == null || indexes.length == 0) {
            return CommonResult.FAILURE;
        }
        for (int index : indexes) {
            MusicEntity music = this.musicMapper.selectMusicByIndex(index, false);
            if (music == null || music.isDeleted() || !music.getStatus().equals(MusicEntity.Status.PENDING.name())) {
                throw new TransactionalException();
            }
            music.setStatus(status ? MusicEntity.Status.ALLOWED.name() : MusicEntity.Status.DENIED.name());
            if (this.musicMapper.updateMusic(music, false) == 0) {
                throw new TransactionalException();
            }
        }
        return CommonResult.SUCCESS;
    }
    //endregion
}
