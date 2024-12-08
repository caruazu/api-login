package org.example.demologin.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.example.demologin.config.exception.ResponseTemplate;
import org.example.demologin.model.User;
import org.example.demologin.model.UserDadosCadastro;
import org.example.demologin.model.UserDadosLogin;
import org.example.demologin.model.UserDadosReset;
import org.example.demologin.model.validation.ValidSenha;
import org.example.demologin.service.TokenService;
import org.example.demologin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
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
	public ResponseEntity<?> login(@RequestBody UserDadosLogin dados) {
		var tokenAuth = new UsernamePasswordAuthenticationToken(dados.username(),dados.password());
		var autenticacao = authenticationManager.authenticate(tokenAuth);
		String token = tokenService.gerar((User) autenticacao.getPrincipal());
		ResponseTemplate responseTemplate = new ResponseTemplate("login efetuado",token);

		return ResponseEntity.ok(responseTemplate);
	}

	@Transactional
	@PostMapping("register")
	public ResponseEntity<?> register(@Valid @RequestBody UserDadosCadastro user){
		String passwordEncoded = passwordEncoder.encode(user.password());
		userService.cadastrar(
				user.username(),
				user.email(),
				passwordEncoded,
				user.role()
		);

		return ResponseEntity.ok().build();
	}

	@Transactional
	@GetMapping("/validate-email")
	public ResponseEntity<?> confirmarEmail(@RequestParam("token") String token){
		String username = tokenService.decodificar(token);
		userService.ativar(username);

		return ResponseEntity.ok().build();
	}

	@Validated
	@PostMapping("/forgot-password")
	public ResponseEntity<?> esqueceuSenha(@NotBlank @RequestParam("username") String username	){
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
