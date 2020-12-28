package com.brausov.social_network.services.impl;

import com.brausov.social_network.models.Role;
import com.brausov.social_network.repositories.RoleRepository;
import com.brausov.social_network.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    @Transactional
    public void add(Role role) {
        roleRepository.save(role);
    }

    @Override
    @Transactional
    public void delete(Role role) {
        roleRepository.delete(role);
    }

    @Override
    @Transactional
    public Role getById(long id) {
        Optional<Role> role = roleRepository.findById(id);
        if (role.isPresent()) {
            return role.get();
        }
        return null;
    }

    @Override
    @Transactional
    public Role getByName(String roleName) {
        Optional<Role> role = roleRepository.findByRoleName(roleName);
        if (role.isPresent()) {
            return role.get();
        }
        return null;
    }

    @Override
    @Transactional
    public void edit(Role role) {
        roleRepository.save(role);
    }

    @Override
    @Transactional
    public List<Role> getAll() {
        return (List<Role>) roleRepository.findAll();
    }
}
