package org.example.demologin.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.demologin.model.validation.ValidSenha;

public record UserDadosCadastro(
	@NotBlank
	String username,
	@Email
	String email,
	@ValidSenha
	String password,
	@NotNull
	UserRole role
) {
}
