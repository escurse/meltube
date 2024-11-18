package com.escass.meltube.mappers;

import com.escass.meltube.entities.EmailTokenEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface EmailTokenMapper {
    int insertEmailToken(EmailTokenEntity emailTokenEntity);

    EmailTokenEntity selectEmailTokenByUserEmailAndKey(@Param("userEmail") String userEmail,
                                                       @Param("key") String key);

    int updateEmailToken(EmailTokenEntity emailTokenEntity);
}
