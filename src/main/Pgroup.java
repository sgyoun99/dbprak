package main;

public enum Pgroup {

	//'Book', 'Music_CD', 'DVD'
	Book,
	Music_CD,
	DVD;

	/*
	BOOK("Book"),
	MUSIC_CD("Music_CD"),
	DVD("DVD");

	String value;
	Pgroup(String string) {
		value = string;
	}
	
	public String value() {
		return this.value;
	}
	 */
	
	public static boolean isValueOfPgroup(String pgroup) {
	    for (Pgroup e : values()) {
	        if (e.toString().equals(pgroup)) {
	            return true;
	        }
	    }
	    return false;
	}
}
