package com.w8x.web.model;

import lombok.Data;

import java.util.List;

@Data
public class CodeShown {
    private String originalCode;
    private String refactCode;
    private List<IssueShow> issueShows;
}
