package demanadtwo.packagetest.dao;

import demanadtwo.packagetest.entity.Article;
import demanadtwo.packagetest.entity.Category;
import java.util.List;

/**
 * Created by sang on 17-3-10.
 */
public interface ArticleDao {

  public Article getArticleById(Long id);

  public List<Article> getFirst10Article();

  public List<Article> getArticlesByCategoryName(Long categoryId);

  public List<Category> getCategories();

  public void writeBlog(Article article);

  public Long getCategoryIdByName(String name);

  public void deleteArticleById(Long id);

  public void updateArticleById(Article article);

  public Category getCategoryById(Long id);
}
