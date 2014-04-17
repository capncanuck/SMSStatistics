package com.github.capncanuck.smsstatistics;

import java.util.Locale;

import android.telephony.PhoneNumberUtils;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;

/**
 * The phone number.
 */
public class PhoneNumber implements Comparable<PhoneNumber> {

    /**
     * Reverses a string.
     * 
     * @param str the string
     * @return the string's reverse
     */
    private static final String reverse(final String str) {
        return new StringBuffer(str).reverse().toString();
    }

    /**
     * The raw phone number.
     */
    private final String raw;

    /**
     * the formatted phone number
     */
    private final String formatted;

    /**
     * The country code.
     */
    private final String country_code;

    /**
     * The area code, exchange and sln (subscriber line number).
     */
    private final String body;

    /**
     * @param raw the raw number
     */
    public PhoneNumber(final String raw) {
        this.raw = PhoneNumberUtils.stripSeparators(raw);

        if (this.raw.length() >= 10) {
            final String reversed = reverse(this.raw);
            final String sln = reverse(reversed.substring(0, 4));
            final String exchange = reverse(reversed.substring(4, 7));
            final String area_code = reverse(reversed.substring(7, 10));
            String country_code = reverse(reversed.substring(10));

            final StringBuilder builder;

            if (country_code.equals("+1") || country_code.equals("1") || country_code.equals("")) {
                builder = new StringBuilder("");
                country_code = null;
            } else {
                builder = new StringBuilder(country_code + " ");
            }

            this.formatted = builder.append(String.format(Locale.CANADA, "(%s) %s-%s", area_code, exchange, sln)).toString();
            this.country_code = country_code;
            this.body = area_code + exchange + sln;
        } else {
            this.formatted = this.raw;
            this.country_code = null;
            this.body = null;
        }
    }

    /**
     * Sorting Phone numbers by:<br>
     * <ol>
     *     <li>non trivial country codes</li>
     *     <li>lexical ordering of area code, exchange and sln concatenated</li>
     * </ol>
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final PhoneNumber other) {
        return ComparisonChain.start()
                .compare(this.country_code, other.country_code, Ordering.natural().nullsLast())
                .compare(this.body, other.body)
                .result();
    }

    /**
    * @return the raw number
    */
    public String toRaw() {
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
        return this.raw.hashCode();
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

        if (this.formatted == null && other.formatted != null
                || !this.formatted.equals(other.formatted)) {
            return false;
        }

        return true;
    }
}