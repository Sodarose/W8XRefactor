package com.w8x.web.BO;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class RuleObj implements Serializable{

    private RulesConfig[] rules;

}
