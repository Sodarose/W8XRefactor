package com.w8x.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/route")
public class PageRoute {
/**/
    @GetMapping("/{pageName}")
    public String route(@PathVariable String pageName){
        String temp = "pages";
        return temp+"/"+pageName;
    }
}
