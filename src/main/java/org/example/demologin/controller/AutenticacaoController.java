package org.example.demologin.controller;

import jakarta.validation.Valid;
import org.example.demologin.config.exception.ResponseTemplate;
import org.example.demologin.model.User;
import org.example.demologin.model.UserDadosReset;
import org.example.demologin.service.TokenService;
import org.example.demologin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
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
		var tokenAuth = new UsernamePasswordAuthenticationToken(user.getUsername(),user.getPassword());
		var autenticacao = authenticationManager.authenticate(tokenAuth);
		String token = tokenService.gerar((User) autenticacao.getPrincipal());
		ResponseTemplate responseTemplate = new ResponseTemplate("login efetuado",token);

		return ResponseEntity.ok(responseTemplate);
	}

	@PostMapping("register")
	public ResponseEntity<?> register(@RequestBody User user){
		String passwordEncoded = passwordEncoder.encode(user.getPassword());
		UserDetails userDB = userService.cadastrar(
			user.getUsername(),
			user.getEmail(),
			passwordEncoded,
			user.getRole()
		);

		return ResponseEntity.ok().build();
	}

	@GetMapping("/validate-email")
	public ResponseEntity<?> confirmarEmail(@RequestParam("token") String token){
		String username = tokenService.decodificar(token);
		userService.ativar(username);

		return ResponseEntity.ok().build();
	}

	@PostMapping("/forgot-password")
	public ResponseEntity<?> esqueceuSenha(@RequestParam("username") String username	){
		userService.senhaPedirNova(username);

		return ResponseEntity.ok().build();
	}

	@Transactional
	@PostMapping("/reset-password")
	public ResponseEntity<?> resetarSenha(@Valid @RequestBody UserDadosReset userDadosReset){
		String username = tokenService.decodificar(userDadosReset.token());
		String passwordEncoded = passwordEncoder.encode(userDadosReset.password());
		userService.senhaMudar(username,passwordEncoded);

		return ResponseEntity.ok().build();
	}
}
