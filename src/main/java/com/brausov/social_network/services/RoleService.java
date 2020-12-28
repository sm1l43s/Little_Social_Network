package com.brausov.social_network.services;

import com.brausov.social_network.models.Role;

import java.util.List;

public interface RoleService {

    void add(Role role);
    void delete(Role role);
    Role getById(long id);
    Role getByName(String roleName);
    void edit(Role role);
    List<Role> getAll();

}
