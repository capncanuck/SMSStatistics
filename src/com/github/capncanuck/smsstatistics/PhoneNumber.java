package com.github.capncanuck.smsstatistics;

import java.util.Locale;

import android.telephony.PhoneNumberUtils;

/**
 * @author Khalil Fazal
 */
public class PhoneNumber {

    /**
     * Reverses a string
     * 
     * @param str the string
     * @return the string's reverse
     */
    private static final String reverse(final String str) {
        return new StringBuffer(str).reverse().toString();
    }

    /**
     * The raw.
     */
    private final String raw;

    /**
     * the formatted phone number
     */
    private final String formatted;

    /**
     * @param raw the raw number
     */
    public PhoneNumber(final String raw) {
        this.raw = raw;

        final String reversed = PhoneNumberUtils.getStrippedReversed(raw);
        final String sln = reverse(reversed.substring(0, 4));
        final String exchange = reverse(reversed.substring(4, 7));
        final String area_code = reverse(reversed.substring(7, 10));
        final String country_code = reverse(reversed.substring(10));
        final StringBuilder builder;

        if (country_code.equals("1") || country_code.equals("+1") || country_code.equals("")) {
            builder = new StringBuilder("");
        } else {
            builder = new StringBuilder((country_code.charAt(0) != '+' ? "+" : "") + country_code + " ");
        }

        builder.append(String.format(Locale.CANADA, "(%s) %s-%s", area_code, exchange, sln));
        this.formatted = builder.toString();
    }

    /**
     * @return the raw number
     */
    public String getRaw() {
        return this.raw;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.formatted;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return 31 + (this.formatted == null ? 0 : this.formatted.hashCode());
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }

        final PhoneNumber other = (PhoneNumber) obj;

        if (this.formatted == null) {
            if (other.formatted != null) {
                return false;
            }
        } else if (!this.formatted.equals(other.formatted)) {
            return false;
        }

        return true;
    }
}
