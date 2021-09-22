package entity;

import main.ErrType;

public class Error {
	private int error_id; 
	private String location;
	private String item_id;
	private String attribute;
	private ErrType errtype;
	private String exception;
	private String error_message;
	private String contents;
	
	public Error() {}
	public Error(int error_id, String location, String item_id, String attribute, ErrType errtype, String exception,
			String error_message, String contents) {
		super();
		this.error_id = error_id;
		this.location = location;
		this.item_id = item_id;
		this.attribute = attribute;
		this.errtype = errtype;
		this.exception = exception;
		this.error_message = error_message;
		this.contents = contents;
	}
	public int getError_id() {
		return error_id;
	}
	public void setError_id(int error_id) {
		this.error_id = error_id;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getItem_id() {
		return item_id;
	}
	public void setItem_id(String item_id) {
		this.item_id = item_id;
	}
	public String getAttribute() {
		return attribute;
	}
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	public ErrType getErrtype() {
		return errtype;
	}
	public void setErrtype(ErrType errtype) {
		this.errtype = errtype;
	}
	public String getException() {
		return exception;
	}
	public void setException(String exception) {
		this.exception = exception;
	}
	public String getError_message() {
		return error_message;
	}
	public void setError_message(String error_message) {
		this.error_message = error_message;
	}
	public String getContents() {
		return contents;
	}
	public void setContents(String contents) {
		this.contents = contents;
	}
	
	
	

	
	
}
