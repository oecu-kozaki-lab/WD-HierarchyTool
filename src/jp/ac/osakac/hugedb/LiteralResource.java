package jp.ac.osakac.hugedb;

public class LiteralResource {
	public String lang;
	public String value;
	public String str;
	public LiteralResource(String str) {
		this.str = str;
		String[] split = str.split("\"@");
		if (split.length == 2) {
			lang = split[1];
			if(split[0].length()>0) {
				value = split[0].substring(1);
			}
			else {
				value ="";
			}
		} else {
			lang = "_";
			value = str.substring(1, str.length()-1);
		}
	}
}
