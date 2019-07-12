package com.w8x.web.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;


@Data
public class CodeStyle {
    @JSONField(name = "codename")
    private String codeName;
    @JSONField(name = "filename")
    private String filename;
    @JSONField(name = "status")
    private boolean status;

}
