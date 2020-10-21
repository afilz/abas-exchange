package de.abas.abex.exchange;

import com.microsoft.graph.authentication.IAuthenticationProvider;
import com.microsoft.graph.http.IHttpRequest;

public class AuthenticationProvider implements IAuthenticationProvider {

	private String accessToken = null;

	public AuthenticationProvider(String accessToken) {
		this.accessToken = accessToken;
	}

	@Override
	public void authenticateRequest(IHttpRequest request) {
		// Add the access token in the Authorization header
		request.addHeader("Authorization", "Bearer " + accessToken);
	}

}
