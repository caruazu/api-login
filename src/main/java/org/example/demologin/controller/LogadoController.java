package org.example.demologin.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("logado")
public class LogadoController {

	@GetMapping
	public String userAccess() {
		return "vc deve está logado.";
	}
}