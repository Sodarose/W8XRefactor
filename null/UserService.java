package demanadtwo.packagetest.service;

import demanadtwo.packagetest.dao.UserDao;
import demanadtwo.packagetest.entity.User;

public class UserService {

  @Resource
  private UserDao userDao;

  public boolean login(String username, String password) {
    User user = userDao.getUser(username, password);
    if (user == null) {
      return false;
    } else {
      return true;
    }
  }
}
