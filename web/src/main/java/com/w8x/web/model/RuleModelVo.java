package com.w8x.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RuleModelVo {
    private String ruleName;
    private String description;
    private boolean ruleStatus;
    private String message;
    private String example;
}
