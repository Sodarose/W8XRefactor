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
@Data
public class TreeNode {
    private String realPath;
    @JSONField(name = "name")
    private String fileName;
    @JSONField(name = "children")
    private List<TreeNode> children;
    private boolean isFile;
    private boolean isHasIssue;
}
