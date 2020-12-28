package com.brausov.social_network.repositories;

import com.brausov.social_network.models.Role;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RoleRepository extends CrudRepository<Role, Long> {

    Optional<Role> findByRoleName(String roleName);

}
