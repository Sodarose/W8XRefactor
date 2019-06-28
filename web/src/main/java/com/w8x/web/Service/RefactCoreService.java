package com.w8x.web.Service;

import com.w8x.web.model.Code;
import com.w8x.web.model.CodeShown;
import com.w8x.web.model.Overview;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

/**
 * 核心服务
 * */
public interface RefactCoreService {
    Code runAnalysis(String filePath) throws FileNotFoundException;
    String getJavaFileTree();
    CodeShown getJavaFileDetail(String filePath) throws UnsupportedEncodingException;
    Code<Overview> getOverview();
    Code<String> refactorAll();

    Code<String> analysisAgin() throws FileNotFoundException;
}
