package model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 文件树
 *
 * @author Administrator*/
@AllArgsConstructor
@NoArgsConstructor
public class TreeNode {
    /**
     *  磁盘路径
     * */
    private String stablePath;
    /**
     *  页面显示路径
     * */
    private String realPath;
    @JSONField(name = "name")
    private String fileName;
    @JSONField(name = "children")
    private List<TreeNode> children;
    private boolean isFile;
    private boolean isHasIssue = false;
    private boolean isChange = false;

    public TreeNode(String stablePath,String realPath,String fileName,List<TreeNode> children,boolean isFile){
        this.stablePath = stablePath;
        this.realPath = realPath;
        this.fileName = fileName;
        this.children = children;
        this.isFile = isFile;
    }

    public String getStablePath() {
        return stablePath;
    }

    public void setStablePath(String stablePath) {
        this.stablePath = stablePath;
    }

    public String getRealPath() {
        return realPath;
    }

    public void setRealPath(String realPath) {
        this.isChange = true;
        this.realPath = realPath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<TreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<TreeNode> children) {
        this.children = children;
    }

    public boolean isFile() {
        return isFile;
    }

    public void setFile(boolean file) {
        isFile = file;
    }

    public boolean isHasIssue() {
        return isHasIssue;
    }

    public void setHasIssue(boolean hasIssue) {
        isHasIssue = hasIssue;
    }

    public boolean isChange() {
        return isChange;
    }

    public void setChange(boolean change) {
        isChange = change;
    }
}
