package org.example.demologin.model;

public record UserDadosDetalhes(
	String username,
	String email,
	Boolean enable,
	UserRole role
) {
	public UserDadosDetalhes(User user) {
		this(
			user.getUsername(),
			user.getEmail(),
			user.getEnabled(),
			user.getRole()
		);
	}
}
