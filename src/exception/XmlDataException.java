package exception;

import org.w3c.dom.Node;

import main.ErrType;

public class XmlDataException extends Exception {

	private String location;
	private String item_id;
	public String getItem_id() {
		return item_id;
	}



	public void setItem_id(String item_id) {
		this.item_id = item_id;
	}


	private String attrName = "";
	private String message = "";
	private ErrType errType;
	
	
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


	/**
	 * 
	 */
	private static final long serialVersionUID = 1006124100678587748L;
	


	public XmlDataException() {
		this.setErrType(ErrType.XML);
	}

}
