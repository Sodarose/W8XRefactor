package com.w8x.web.dao;

import com.w8x.web.pojo.CodeStyleConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CodeStyleRepository extends JpaRepository<CodeStyleConfig,Long> {
    CodeStyleConfig findCodeStyleConfigByCodeNameAndUse(String codeName,boolean use);
    long deleteCodeStyleConfigByCodeName(String codeName);
}
