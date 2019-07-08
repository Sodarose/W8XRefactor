package com.w8x.web.Exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class WebExceptionHandler {
    private static Logger LOGGER = LoggerFactory.getLogger(WebExceptionHandler.class);
    private static String ERROR_VIEW= "/error.html";
    @ExceptionHandler(value = RuntimeException.class)
    public ModelAndView defaultErrorHandler(HttpServletRequest request, Exception e){
        LOGGER.info("出现错误 错误为"+e.getMessage());
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(ERROR_VIEW);
        return modelAndView;
    }
}
