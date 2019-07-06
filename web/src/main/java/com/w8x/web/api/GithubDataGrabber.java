package com.w8x.web.api;


import com.w8x.web.config.GitConfig;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;


/**
 * 通过pull来获取源代码
 */
@Component
public class GithubDataGrabber {

    @Autowired
    GitConfig gitConfig;

    private static Logger LOGGER = LoggerFactory.getLogger(GithubDataGrabber.class);

    /**
     * 初始化一些配置信息 设置仓库地址
     */
    private void init() {
        ApplicationHome home = new ApplicationHome(getClass());
        //目前用于测试 返回的是程序主目录
        File homeDir = home.getDir();
        // jar包存放的位置
        String path = homeDir.getPath() + File.separator + "repository";
        if(gitConfig.getPath()!=null&&!"".equals(gitConfig.getPath())){
            path = gitConfig.getPath();
        }
        gitConfig.setPath(path);
        File repository = new File(path);
        LOGGER.info("仓库地址："+repository.getPath());
        if (!repository.exists()) {
            repository.mkdirs();
        }
        //设置仓库地址
        gitConfig.setRepository(repository);
    }

    /**
     * 克隆仓库
     */
    public String gitCloneRepository(String remoteUrl, String branch) throws IOException {
        //初始化
        init();
        Git git = null;
        //项目名称
        String name = remoteUrl.substring(remoteUrl.lastIndexOf("/") + 1, remoteUrl.lastIndexOf(".git"));
        //得到
        File file = new File(gitConfig.getRepository().getPath() + File.separatorChar+ name);
        LOGGER.info("项目地址："+file.getPath());
        if (file.exists()) {
            Files.deleteIfExists(file.toPath());
        } else {
            file.mkdirs();
        }
        String projectPath = null;
        try {
            LOGGER.info("开始拉取仓库 仓库名："+name+"\t分支："+branch);
            git = Git.cloneRepository().setDirectory(file).setURI(remoteUrl).setBranch(branch).call();
            LOGGER.info("克隆本地成功");
            projectPath = file.getPath();
        } catch (InvalidRemoteException e) {
            e.printStackTrace();
        } catch (TransportException e) {
            e.printStackTrace();
        } catch (GitAPIException e) {
            e.printStackTrace();
        } finally {
            if (git != null) {
                git.close();
            }
        }
        return projectPath;
    }
}