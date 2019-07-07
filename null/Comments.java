package demanadtwo.packagetest.entity;

/**
 * Created by sang on 17-3-10.
 */
public class Comments {

  private long id;

  private long articleId;

  private String comMIp;

  private String comment;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getArticleId() {
    return articleId;
  }

  public void setArticleId(long articleId) {
    this.articleId = articleId;
  }

  public String getComMIp() {
    return comMIp;
  }

  public void setComMIp(String comMIp) {
    this.comMIp = comMIp;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }
}
