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
        File homeDir = home.getSource();
        // jar包存放的位置
        String path = homeDir.getPath() + "/" + gitConfig.getPath();
        File repository = new File(path);
        if (!repository.exists()) {
            repository.mkdirs();
        }
        gitConfig.setRepository(repository);
    }

    /**
     * 克隆仓库
     */
    public String gitCloneRepository(String remoteUrl, String branch) throws IOException {
        init();
        Git git = null;
        String name = remoteUrl.substring(remoteUrl.lastIndexOf("/") + 1, remoteUrl.lastIndexOf(".git"));
        File file = new File(gitConfig.getRepository().getPath() + File.pathSeparatorChar + name);
        if (file.exists()) {
            Files.deleteIfExists(file.toPath());
        } else {
            file.mkdirs();
        }
        String projectPath = null;
        try {
            LOGGER.info("开始拉取仓库");
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

    public static void main(String args[]) throws IOException {
        GithubDataGrabber githubDataGrabber = new GithubDataGrabber();
        githubDataGrabber.gitCloneRepository("https://github.com/Sodarose/W8XRefactor.git", "dev");
    }
}