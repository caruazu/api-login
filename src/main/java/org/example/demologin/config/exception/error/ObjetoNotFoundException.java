package org.example.demologin.config.exception.error;

public class ObjetoNotFoundException extends RuntimeException {
	public ObjetoNotFoundException(Long id) {
		super("Nenhum objeto com o id " + id +" existe");
	}
}
