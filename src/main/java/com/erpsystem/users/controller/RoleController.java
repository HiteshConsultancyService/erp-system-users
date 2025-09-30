package com.erpsystem.users.controller;

import com.erpsystem.users.entity.Role;
import com.erpsystem.users.service.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RoleController {

    RoleService roleService;

    RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/role")
    public ResponseEntity<Role> create(@RequestBody Role role) {
        Role savedRole = roleService.create(role);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedRole);
    }
}
