package com.w8x.web.model;

import lombok.Data;

@Data
public class Overview {
    private String projectName;
    private String realPath;
    private int javaCount;
    private int issueCount;
    private int badFileCount;
    private int rule;
}
