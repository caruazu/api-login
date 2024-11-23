package org.example.demologin.model;

public enum UserRole {
	ADMIN("Administrador"),
	USER("Usuário");

	private final String descricao;

	UserRole(String descricao) {
		this.descricao = descricao;
	}
	String getDescricao() {
		return descricao;
	}
}
