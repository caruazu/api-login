package org.example.demologin.controller;

import org.example.demologin.model.UserDadosCadastro;
import org.example.demologin.model.UserDadosLogin;
import org.example.demologin.model.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
class AutenticacaoControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private JacksonTester<UserDadosCadastro> userDadosCadastroJson;


	@Nested
	@DisplayName("Tests do endpoint /auth/register")
	class RegisterTests {
		@Test
		@DisplayName("Deve registrar usuário com sucesso")
		@Transactional
		@Rollback(true)
		void cadastrar_cenario2() throws Exception {
			var role = UserRole.USER;
			var corpo = userDadosCadastroJson.write(new UserDadosCadastro("Teste","teste@teste.com","{b6S.J6L78ye",role)).getJson();

			var response = mockMvc.perform(
					post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(corpo)
			).andReturn().getResponse();

			assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		}

		@Test
		@DisplayName("Deveria devolver código 400 e informar que o corpo da requisição é obrigatório")
		void cadastrar_cenario1() throws Exception {
			var response = mockMvc.perform(post("/auth/register")).andReturn().getResponse();

			assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		}
	}

}