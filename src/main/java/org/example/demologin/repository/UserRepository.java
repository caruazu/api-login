package org.example.demologin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.demologin.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	UserDetails findByUsername(String username);
}
