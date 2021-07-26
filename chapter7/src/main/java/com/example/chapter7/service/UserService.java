package com.example.chapter7.service;

import com.example.chapter7.pojo.User;

import java.util.List;

/**
 * @author DX
 * @version 1.0
 * @date 2021/6/17 16:41
 */
public interface UserService {
    User getUser(Long id);

    User insertUser(User user);

    User updateUserName(Long id, String userName);

    List<User> findUsers(String userName, String note);

    int deleteUser(Long id);
}
