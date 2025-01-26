package org.example.demologin.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CaptchaResponse {
	private boolean success;

	@JsonProperty("challenge_ts")
	private String challengeTs;

	private String hostname;

	@JsonProperty("error-codes")
	private List<String> errorCodes;
}
