package com.github.capncanuck.smsstatistics.contact;

import java.util.List;

import android.text.TextUtils;

import com.google.common.base.Objects;

/**
 * the immutable contact list including furthur statistics.
 */
public class ContactsData {

    /**
     * newline
     */
    private static final CharSequence NL = System.getProperty("line.separator");

    /**
     * The contact list.
     */
    private final List<Contact> contacts;

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
     * @param contacts the contact list
     */
    public ContactsData(final List<Contact> contacts) {
        this.contacts = contacts;
        int total = 0, totalIncoming = 0, totalOutgoing = 0;

        for (final Contact contact : contacts) {
            total += contact.getTotal();
            totalIncoming += contact.getIncoming();
            totalOutgoing += contact.getOutgoing();
        }

        this.total = total;
        this.totalIncoming = totalIncoming;
        this.totalOutgoing = totalOutgoing;

        for (final Contact contact : contacts) {
            contact.setPercentage(total);
        }

        if (contacts.size() > 0) {
            final int greatest = contacts.get(0).getTotal();

            for (final Contact contact : contacts) {
                contact.setBarScale(greatest);
            }
        }
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("total", this.total)
                .add("list", TextUtils.join(NL, this.contacts))
                .toString();
    }

    /**
     * @return the contact list
     */
    public List<Contact> getList() {
        return this.contacts;
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