package com.erpsystem.users.service.impl;

import com.erpsystem.users.entity.Role;
import com.erpsystem.users.repository.RoleRepository;
import com.erpsystem.users.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

    RoleRepository roleRepository;

    RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role create(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Role getRole(int id) {
        Optional<Role> result = roleRepository.findById(id);
        return result.orElse(null);
    }
}
