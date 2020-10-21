package de.abas.abex.erp;

public class OperatingRecordException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OperatingRecordException(String message) {
		super(message);
	}

	public OperatingRecordException() {
		super("NO_OPERATING_RECORD_FOUND");
	}

}
