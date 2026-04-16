package com.effinghamministorage.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class PhoneNumberUtilsTest {

    @Test
    public void stripPhone_removesFormatting() {
        assertEquals("9125551234", PhoneNumberUtils.stripPhone("(912) 555-1234"));
    }

    @Test
    public void stripPhone_handlesRawDigits() {
        assertEquals("9125551234", PhoneNumberUtils.stripPhone("9125551234"));
    }

    @Test
    public void stripPhone_handlesEmptyString() {
        assertEquals("", PhoneNumberUtils.stripPhone(""));
    }

    @Test
    public void stripPhone_removesAllNonDigits() {
        assertEquals("123", PhoneNumberUtils.stripPhone("+1 (2) 3"));
    }

    @Test
    public void isValidPhone_acceptsTenDigits() {
        assertTrue(PhoneNumberUtils.isValidPhone("9125551234"));
    }

    @Test
    public void isValidPhone_rejectsTooShort() {
        assertFalse(PhoneNumberUtils.isValidPhone("912555"));
    }

    @Test
    public void isValidPhone_rejectsTooLong() {
        assertFalse(PhoneNumberUtils.isValidPhone("91255512345"));
    }

    @Test
    public void isValidPhone_rejectsEmpty() {
        assertFalse(PhoneNumberUtils.isValidPhone(""));
    }

    @Test
    public void toE164_prependsCountryCode() {
        assertEquals("+19125551234", PhoneNumberUtils.toE164("9125551234"));
    }

    @Test
    public void formatUsPhone_emptyInput() {
        assertEquals("", PhoneNumberUtils.formatUsPhone(""));
    }

    @Test
    public void formatUsPhone_oneDigit() {
        assertEquals("(9", PhoneNumberUtils.formatUsPhone("9"));
    }

    @Test
    public void formatUsPhone_threeDigits() {
        assertEquals("(912", PhoneNumberUtils.formatUsPhone("912"));
    }

    @Test
    public void formatUsPhone_fourDigits() {
        assertEquals("(912) 5", PhoneNumberUtils.formatUsPhone("9125"));
    }

    @Test
    public void formatUsPhone_sixDigits() {
        assertEquals("(912) 555", PhoneNumberUtils.formatUsPhone("912555"));
    }

    @Test
    public void formatUsPhone_sevenDigits() {
        assertEquals("(912) 555-1", PhoneNumberUtils.formatUsPhone("9125551"));
    }

    @Test
    public void formatUsPhone_tenDigits() {
        assertEquals("(912) 555-1234", PhoneNumberUtils.formatUsPhone("9125551234"));
    }

    @Test
    public void formatUsPhone_truncatesAtTenDigits() {
        assertEquals("(912) 555-1234", PhoneNumberUtils.formatUsPhone("91255512345"));
    }
}
