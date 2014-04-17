package com.github.capncanuck.smsstatistics;

import android.text.TextUtils;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;

/**
 * the immutable contact list including furthur statistics.
 */
public class ContactList {

    /**
     * newline
     */
    private static final CharSequence NL = System.getProperty("line.separator");

    /**
     * The immutable contact list.
     */
    private final ImmutableSet<Contact> frozenContacts;

    /**
     * The total number of messages
     */
    private final int total;

    /**
     * @param frozenContacts the immutable contact list
     */
    public ContactList(final ImmutableSet<Contact> frozenContacts) {
        this.frozenContacts = frozenContacts;
        int total = 0;

        for (final Contact contact : frozenContacts) {
            total += contact.getTotal();
        }

        this.total = total;

        for (final Contact contact : frozenContacts) {
            contact.setPercentage(total);
        }
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("total", this.total)
                .add("list", TextUtils.join(NL, this.frozenContacts))
                .toString();
    }
}