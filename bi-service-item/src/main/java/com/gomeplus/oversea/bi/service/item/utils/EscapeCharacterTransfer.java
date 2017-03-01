package com.gomeplus.oversea.bi.service.item.utils;

/**
 * @author shanjie
 *
 */
public class EscapeCharacterTransfer {

	public static final String String_AMP = "&amp;";
	public static final String String_AMP_TRANS = "&";
	public static final String STRING_QUOT = "&quot;";
	public static final String STRING_QUOT_TRANS = "\"";
	public static final String STRING_SPRIT = "&#39;";
	public static final String STRING_SPRIT_TRANS = "\\";
	public static final String STRING_LT = "&lt;";
	public static final String STRING_LT_TRANS = "<";
	public static final String STRING_GT = "&gt;";
	public static final String STRING_GT_TRANS = ">";

	/**
	 * @param beReplaceName
	 * @return
	 */
	public static String forward(String str) {
		String toward = str;
		if (String.valueOf(str).contains(String_AMP)
				|| String.valueOf(str).contains(STRING_QUOT)
				|| String.valueOf(str).contains(STRING_SPRIT)
				|| String.valueOf(str).contains(STRING_LT)
				|| String.valueOf(str).contains(STRING_GT)) {
			toward = str.replace(String_AMP, String_AMP_TRANS)
					.replace(STRING_QUOT, STRING_QUOT_TRANS)
					.replace(STRING_SPRIT, STRING_SPRIT_TRANS)
					.replace(STRING_LT, STRING_LT_TRANS)
					.replace(STRING_GT, STRING_GT_TRANS);
		}
		return toward;
	}
}
