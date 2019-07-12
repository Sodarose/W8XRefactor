package demanadtwo.packagetest.dao;

import demanadtwo.packagetest.entity.User;

/**
 * Created by sang on 17-3-10.
 */
public interface UserDao {

  public User getUser(String username, String password);
}
