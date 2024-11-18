package com.escass.meltube.mappers;

import com.escass.meltube.entities.EmailTokenEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmailTokenMapper {
    int insertEmailToken(EmailTokenEntity emailTokenEntity);
}
