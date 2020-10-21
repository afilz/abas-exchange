package de.abas.abex.exchange;

public class AuthenticationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AuthenticationException(String message) {
		super(message);
	}

	public AuthenticationException() {
		super("NO_EXCHANGE_CONNECTION");
	}

}
