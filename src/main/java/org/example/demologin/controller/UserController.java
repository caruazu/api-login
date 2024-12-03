package org.example.demologin.controller;

import org.example.demologin.model.User;
import org.example.demologin.model.UserDadosDetalhes;
import org.example.demologin.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping
	public ResponseEntity<UserDadosDetalhes> lerUsuarioAtual(@AuthenticationPrincipal User user){

		return ResponseEntity.ok(new UserDadosDetalhes(user));
	};

	@GetMapping("/{id}")
	public ResponseEntity<?> ler(@PathVariable Long id) {
		User usuario = userService.loadUserByIdAndRole(id);

		return ResponseEntity.ok(new UserDadosDetalhes(usuario));
	}

	@Transactional
	@PutMapping("/{id}")
	public ResponseEntity<Void> desativar(@PathVariable Long id) {
		userService.desativar(id);

		return ResponseEntity.noContent().build();
	}
}
