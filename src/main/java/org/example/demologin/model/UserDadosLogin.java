package org.example.demologin.model;

import jakarta.validation.constraints.NotBlank;
import org.example.demologin.model.validation.ValidSenha;

public record UserDadosLogin(
	@NotBlank
	String username,
	@ValidSenha
	String password,
	@NotBlank
	String captcha
) {
}
