package com.w8x.web.pojo;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@ToString
@Data
@Entity
@Table(name = "CODE_STYLE")
public class CodeStyleConfig {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    @Column
    private String codeName;
    @Column
    private String path;
    @Column
    private boolean use;
}

