package cn.internetware.utils;

public class Regex {
	public static final String attrName = "[^\\s>\"'<=]+";
	public static final String attrValue = "([^\\s\"']+|\"[^\"]*\"|'[^']*')";
	public static final String attr = attrName + "(\\s*=\\s*" + attrValue + ")?";
}
