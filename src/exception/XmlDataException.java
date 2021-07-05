package exception;


import main.ErrType;

/**
 * 
 * Exception for general XML Data inconsistency and missing attribute
 */
public class XmlDataException extends Exception {

	//to locate where the Exception has occurred.
	private String location;
	//asin where exception has occurred.
	private String item_id;
	//to record attribute name for inconsistency and missing.
	private String attrName = "";
	//to record detailed message.
	private String message = "";
	//to record error type.
	private ErrType errType;
	
	public String getItem_id() {
		return item_id;
	}

	public void setItem_id(String item_id) {
		this.item_id = item_id;
	}
	
	public ErrType getErrType() {
		return errType;
	}

	protected void setErrType(ErrType errType) {
		this.errType = errType;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getAttrName() {
		return attrName;
	}

	public void setAttrName(String attrName) {
		this.attrName = attrName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	private static final long serialVersionUID = 1006124100678587748L;

	//Error Type will be automatically set.
	public XmlDataException() {
		this.setErrType(ErrType.XML);
	}

}
