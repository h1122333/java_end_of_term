package com.StudentEnrollment.service;


import com.StudentEnrollment.dao.UserDao;
import com.StudentEnrollment.entity.User;
import com.StudentEnrollment.utils.Md5;


public class UserService {
    private UserDao userDao = new UserDao();
    public boolean register(String username, String password, String role) {
        if (userDao.exists(username)) {
            return false; // 用户名已存在
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(Md5.md5(password));
        user.setRole(role);
        return userDao.insert(user);
    }

    public com.StudentEnrollment.entity.User login(String username, String password) {
        String encrypted = Md5.md5(password);
        return userDao.query(username, encrypted);
    }
}
