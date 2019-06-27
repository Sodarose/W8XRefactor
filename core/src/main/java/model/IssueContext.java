package model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 存储分析后的信息
 * */
@Data
public class IssueContext {
    private List<Issue> issues = new ArrayList<>();
}
