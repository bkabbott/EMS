package com.effinghamministorage.util;

public class PhoneNumberUtils {

    private PhoneNumberUtils() {}

    public static String stripPhone(String formatted) {
        return formatted.replaceAll("\\D", "");
    }

    public static boolean isValidPhone(String digits) {
        return digits.length() == 10;
    }

    public static String toE164(String digits) {
        return "+1" + digits;
    }

    public static String formatUsPhone(String digits) {
        if (digits.isEmpty()) {
            return "";
        } else if (digits.length() <= 3) {
            return "(" + digits;
        } else if (digits.length() <= 6) {
            return "(" + digits.substring(0, 3) + ") " + digits.substring(3);
        } else {
            String area = digits.substring(0, 3);
            String prefix = digits.substring(3, 6);
            String line = digits.substring(6, Math.min(digits.length(), 10));
            return "(" + area + ") " + prefix + "-" + line;
        }
    }
}
