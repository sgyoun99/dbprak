package main;

/**
 * enum for product group
 *
 */
public enum Pgroup {

	Book,
	Music_CD,
	DVD;

	/**
	 * To determine if the pgroup is correct.
	 * @param pgroup pgroup
	 * @return correctness
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
