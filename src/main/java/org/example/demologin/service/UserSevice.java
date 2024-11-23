package org.example.demologin.service;

import lombok.RequiredArgsConstructor;
import org.example.demologin.model.User;
import org.example.demologin.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserSevice implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepository.findByUsername(username);
	}

	public UserDetails cadastrar(String username, String email, String password) {


		User user = new User();
		user.setUsername(username);
		user.setEmail(email);
		user.setPassword(password);
		user.setEnabled(true);

		User userDB = salvarNoBanco(user);
		return userDB;
	}

	private User salvarNoBanco(User user) {
		return userRepository.save(user);
	}
}
