package cn.internetware.utils;

public class DecodeUtils {

	/**
	 * 解析str = "&#23458;&#36816;&#31449;&#38382;&#39064;";
	 * 
	 * @param str
	 * @return
	 */
	public static String decodeUnicode(String str) {
		StringBuilder sb = new StringBuilder();
		try {
			if (str != null && str.startsWith("&#") && str.endsWith(";")) {

				str = str.replace("&#", "");
				if (str.endsWith(";")) {
					str = str.substring(0, str.length() - 1);
					String[] temp = str.split(";");
					if (temp != null && temp.length > 0) {
						for (String t : temp) {
							if (t.startsWith(" ")) {
								sb.append(" ");
							}
							char chr = (char) Integer.parseInt(t.trim());
							sb.append(chr);
						}
					}
				}

				return sb.toString();
			} else {
				return str;
			}
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}

		return str;
	}

	/**
	 * 解析str = "\u817E\u8BAF\u79FB\u52A8\u4E92\u8054\u7F51";
	 * 
	 * @param str
	 * @return
	 */
	public String decodeUnicode1(String theString) {
		char aChar;
		int len = theString.length();
		StringBuffer outBuffer = new StringBuffer(len);
		for (int x = 0; x < len;) {
			aChar = theString.charAt(x++);
			if (aChar == '\\') {
				aChar = theString.charAt(x++);
				if (aChar == 'u') {
					// Read the xxxx
					int value = 0;
					for (int i = 0; i < 4; i++) {
						aChar = theString.charAt(x++);
						switch (aChar) {
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
							value = (value << 4) + aChar - '0';
							break;
						case 'a':
						case 'b':
						case 'c':
						case 'd':
						case 'e':
						case 'f':
							value = (value << 4) + 10 + aChar - 'a';
							break;
						case 'A':
						case 'B':
						case 'C':
						case 'D':
						case 'E':
						case 'F':
							value = (value << 4) + 10 + aChar - 'A';
							break;
						default:
							throw new IllegalArgumentException(
									"Malformed   \\uxxxx   encoding.");
						}

					}
					outBuffer.append((char) value);
				} else {
					if (aChar == 't')
						aChar = '\t';
					else if (aChar == 'r')
						aChar = '\r';
					else if (aChar == 'n')
						aChar = '\n';
					else if (aChar == 'f')
						aChar = '\f';
					outBuffer.append(aChar);
				}
			} else
				outBuffer.append(aChar);
		}

		return outBuffer.toString();
	}

}
