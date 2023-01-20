package com.increff.pos.util;

public class StringUtil {

	private static final String EMAIL_PATTERN = "[a-z\\d]+@[a-z]+\\.[a-z]{2,3}";

	public static boolean isEmpty(String s) {
		return s == null || s.trim().length() == 0;
	}

	public static String toLowerCase(String s) {
		return s == null ? null : s.trim().toLowerCase();
	}

	public static boolean isValidEmail(String email) {
		return email.matches(EMAIL_PATTERN);
	}

}
