package com.github.capncanuck.smsstatistics.contacts;

import java.util.List;

import android.text.TextUtils;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;

/**
 * the immutable contact list including furthur statistics.
 */
public class ContactsData {

    /**
     * newline
     */
    private static final CharSequence NL = System.getProperty("line.separator");

    /**
     * The immutable contact list.
     */
    private final ImmutableSet<Contact> frozenContacts;

    /**
     * The total number of messages.
     */
    private final int total;

    /**
     * The total number of incoming messages.
     */
    private final int totalIncoming;

    /**
     * The total number of outgoing messages.
     */
    private final int totalOutgoing;

    /**
     * @param frozenContacts the immutable contact list
     */
    public ContactsData(final ImmutableSet<Contact> frozenContacts) {
        this.frozenContacts = frozenContacts;
        int total = 0, totalIncoming = 0, totalOutgoing = 0;

        for (final Contact contact : frozenContacts) {
            total += contact.getTotal();
            totalIncoming += contact.getIncoming();
            totalOutgoing += contact.getOutgoing();
        }

        this.total = total;
        this.totalIncoming = totalIncoming;
        this.totalOutgoing = totalOutgoing;

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

    /**
     * @return the contact list
     */
    public List<Contact> getList() {
        return this.frozenContacts.asList();
    }

    /**
     * @return the totalIncoming
     */
    public int getTotalIncoming() {
        return this.totalIncoming;
    }

    /**
     * @return the totalOutgoing
     */
    public int getTotalOutgoing() {
        return this.totalOutgoing;
    }
}