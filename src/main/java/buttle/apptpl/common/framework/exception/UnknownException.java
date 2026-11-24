package buttle.apptpl.common.framework.exception;

import micoai.corebase.exception.ExceptionMessage;
import micoai.corebase.exception.NetException;

public class UnknownException extends NetException {
	/**
	 *
	 */
	private static final long serialVersionUID = -8705035263144793138L;
	private ExceptionMessage exceptionMessage;

	public UnknownException() {
		super();
	}

	public UnknownException(String message) {
		super(message);
		this.exceptionMessage = ExceptionMessage.invalid(message);
	}

	public UnknownException(Integer status, String message) {
		super(message);
		this.exceptionMessage = ExceptionMessage.invalid(status, message);
	}

    public void setExceptionMessage(ExceptionMessage exceptionMessage) {
		this.exceptionMessage = exceptionMessage;
	}
}
