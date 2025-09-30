package com.erpsystem.users.service;

import com.erpsystem.users.entity.User;

public interface UserService {

    public User create(User user);

    public User getUserByUsername(String username);

    public User getUserByEmail(String email);
}
