package com.w8x.web.dao;

import com.w8x.web.pojo.GitConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GitRepository extends JpaRepository<GitConfig, Long> {
    GitConfig findGitConfigById(Long id);
}
