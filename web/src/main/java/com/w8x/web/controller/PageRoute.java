package com.w8x.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/route")
public class PageRoute {
    /**
     *  路由处理
     * */
    @GetMapping("/{pageName}")
    public String route(@PathVariable String pageName){
        return "pages/"+pageName;
    }

}
