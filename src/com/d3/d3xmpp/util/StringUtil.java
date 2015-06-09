package com.d3.d3xmpp.util;

import java.lang.Character.UnicodeBlock;
import java.util.HashMap;
import java.util.Map;

public class StringUtil {
	public static Map<String, String> patsemapfromstring(String baseString) {
		String[] arr = baseString.split(",");
		Map<String, String> result = new HashMap<String, String>();
		for (int i = 0; i < arr.length; i++) {
			String tempString[] = arr[i].split("=");
			String key = tempString[0].trim();
			String value = tempString[1].trim().substring(1,
					tempString[1].length() - 1);
			result.put(key, value);
		}

		return result;

	}
	
	public static String gbk2utf8(String gbk) {
        String l_temp = GBK2Unicode(gbk);
        l_temp = unicodeToUtf8(l_temp);
 
        return l_temp;
    }
 
    public static String utf82gbk(String utf) {
        String l_temp = utf8ToUnicode(utf);
        l_temp = Unicode2GBK(l_temp);
 
        return l_temp;
    }

	
	public static String GBK2Unicode(String str) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            char chr1 = (char) str.charAt(i);
 
            if (!isNeedConvert(chr1)) {
                result.append(chr1);
                continue;
            }
 
            result.append("\\u" + Integer.toHexString((int) chr1));
        }
 
        return result.toString();
    }
 
	
    /**
     *
     * @param dataStr
     * @return String
     */
 
    public static String Unicode2GBK(String dataStr) {
        int index = 0;
        StringBuffer buffer = new StringBuffer();
 
        int li_len = dataStr.length();
        while (index < li_len) {
            if (index >= li_len - 1
                    || !"\\u".equals(dataStr.substring(index, index + 2))) {
                buffer.append(dataStr.charAt(index));
 
                index++;
                continue;
            }
 
            String charStr = "";
            charStr = dataStr.substring(index + 2, index + 6);
 
            char letter = (char) Integer.parseInt(charStr, 16);
 
            buffer.append(letter);
            index += 6;
        }
 
        return buffer.toString();
    }
 
    public static boolean isNeedConvert(char para) {
        return (((para & (0x00FF)) != para) && !isChinese(para));
    }
 
    
    private static final boolean isChinese(char c) { 
    	Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
    	if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
    		    || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
    		    || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
    		    || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
    		    || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
    		    || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
    		return true;
    	}
    	return false;
    }
    
    /**
     * utf-8 תunicode
     *
     * @param inStr
     * @return String
     */
    public static String utf8ToUnicode(String inStr) {
        char[] myBuffer = inStr.toCharArray();
 
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < inStr.length(); i++) {
            UnicodeBlock ub = UnicodeBlock.of(myBuffer[i]);
            if (ub == UnicodeBlock.BASIC_LATIN) {
                sb.append(myBuffer[i]);
            } else if (ub == UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
                int j = (int) myBuffer[i] - 65248;
                sb.append((char) j);
            } else {
                short s = (short) myBuffer[i];
                String hexS = Integer.toHexString(s);
                String unicode = "\\u" + hexS;
                sb.append(unicode.toLowerCase());
            }
        }
        return sb.toString();
    }
 
    /**
     *
     * @param theString
     * @return String
     */
    public static String unicodeToUtf8(String theString) {
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
