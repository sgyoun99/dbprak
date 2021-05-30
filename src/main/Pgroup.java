package main;

public enum Pgroup {

	//'Book', 'Music_CD', 'DVD'
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
	

}
