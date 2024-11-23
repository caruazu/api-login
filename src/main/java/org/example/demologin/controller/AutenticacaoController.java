package org.example.demologin.controller;

import org.example.demologin.model.User;
import org.example.demologin.service.TokenService;
import org.example.demologin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
public class AutenticacaoController {

	@Autowired
	private UserService userService;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private TokenService tokenService;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody User user) {
		try {
			var tokenAuth = new UsernamePasswordAuthenticationToken(user.getUsername(),user.getPassword());
			var autenticacao = authenticationManager.authenticate(tokenAuth);
			var token = tokenService.gerarToken((User) autenticacao.getPrincipal());
			return ResponseEntity.ok(token);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
		}
	}

	@PostMapping("register")
	public ResponseEntity<?> register(@RequestBody User user)
	{
		try {
			String password = passwordEncoder.encode(user.getPassword());

			UserDetails userDB = userService.cadastrar(
				user.getUsername(),
				user.getEmail(),
				password,
				user.getRole()
			);
			return ResponseEntity.ok(userDB);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
		}
	}
}
