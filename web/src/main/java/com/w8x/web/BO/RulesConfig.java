package com.w8x.web.BO;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RulesConfig implements Serializable {

    private static final long serialVersionUID = 1965489881877269L;


    private String ruleName;

    private int status;

}
