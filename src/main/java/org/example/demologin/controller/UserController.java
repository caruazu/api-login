package org.example.demologin.controller;

import org.example.demologin.model.User;
import org.example.demologin.model.UserDadosDetalhes;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
public class UserController {

	@GetMapping
	public ResponseEntity<UserDadosDetalhes> lerUsuarioAtual(@AuthenticationPrincipal User user){

		return ResponseEntity.ok(new UserDadosDetalhes(user));
	};
}
