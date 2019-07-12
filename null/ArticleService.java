package demanadtwo.packagetest.service;

import demanadtwo.packagetest.dao.ArticleDao;
import demanadtwo.packagetest.entity.Article;
import demanadtwo.packagetest.entity.Category;
import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by sang on 17-3-10.
 */
public class ArticleService {

  @Resource
  private ArticleDao articleDao;

  private SimpleDateFormat sDf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

  public Article getArticleById(Long id) {
    Article article = articleDao.getArticleById(id);
    article.setCategory(articleDao.getCategoryById(article.getCategoryId()).getDisplayName());
    return article;
  }

  public List<Article> getFirst10Article() {
    return articleDao.getFirst10Article();
  }

  public List<Category> getCategories() {
    return articleDao.getCategories();
  }

  public void writeBlog(Article article) {
    Long categoryId = articleDao.getCategoryIdByName(article.getCategory());
    article.setCategoryId(categoryId);
    article.setDate(sDf.format(new Date()));
    if (article.getSummary() == null || "".equals(article.getSummary())) {
      if (article.getContent().length() > 20) {
        article.setSummary(article.getContent().substring(0, 20));
      } else {
        article.setSummary(article.getContent().substring(0, article.getContent().length()));
      }
    }
    articleDao.writeBlog(article);
  }

  public void deleteArticleById(Long id) {
    articleDao.deleteArticleById(id);
  }

  public void updateBlog(Article article) {
    article.setDate(sDf.format(new Date()));
    if (article.getSummary() == null || "".equals(article.getSummary())) {
      if (article.getContent().length() > 20) {
        article.setSummary(article.getContent().substring(0, 20));
      } else {
        article.setSummary(article.getContent().substring(0, article.getContent().length()));
      }
    }
    articleDao.updateArticleById(article);
  }

  public List<Article> getArticlesByCategoryName(String name) {
    Long categoryId = articleDao.getCategoryIdByName(name);
    List<Article> articles = articleDao.getArticlesByCategoryName(categoryId);
    return articles;
  }
}
