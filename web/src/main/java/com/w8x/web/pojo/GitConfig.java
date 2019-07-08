package com.w8x.web.pojo;

import lombok.Data;

import javax.persistence.*;

/**
 * git h2配置
 */
@Data
@Entity
@Table(name = "GIT_CONFIG")
public class GitConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column
    private String path;
    @Column
    private boolean use;


}
