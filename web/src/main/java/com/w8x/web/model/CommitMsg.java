package com.w8x.web.model;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@ToString
@Data
public class CommitMsg {
  private String ref;
  private String before;
  private String after;
  private List<Commits> commits;
  private Repository repository;

  public CommitMsg(){

  }
  public CommitMsg(String ref, String before, String after, List<Commits> commits, Repository repository){
      this.ref = ref;
      this.before = before;
      this.after = after;
      this.commits = commits;
      this.repository = repository;
  }
}
