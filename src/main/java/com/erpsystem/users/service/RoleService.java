package com.erpsystem.users.service;

import com.erpsystem.users.entity.Role;

import java.util.List;

public interface RoleService {

    public Role create(Role role);

    public List<Role> getRoles();

    public Role getRole(int id);
}
