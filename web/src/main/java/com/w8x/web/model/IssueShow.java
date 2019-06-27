package com.w8x.web.model;

import lombok.Data;

@Data
public class IssueShow {
    private int beginLine;
    private int endLine;
    private String issueMessage;
    private String ruleName;
}
