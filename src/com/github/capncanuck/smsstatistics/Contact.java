package com.github.capncanuck.smsstatistics;

import com.google.common.base.Objects;

/**
 * A SMS Contact.
 * 
 * Includes this name and the number of incoming and outgoing texts
 */
public class Contact {

    /**
     * The display_name.
     */
    private final String display_name;

    /**
     * the number of incoming messages
     */
    private final int incoming;

    /**
     * the number of outgoing messages
     */
    private int outgoing;

    /**
     * @param display_name the contact's display name
     * @param incoming the number of incoming messages
     */
    public Contact(final String display_name, final int incoming) {
        this(display_name, incoming, 0);
    }

    /**
     * @param display_name the contact's display name
     * @param incoming the number of incoming messages
     * @param outgoing the number of outgoing messages
     */
    public Contact(final String display_name, final int incoming, final int outgoing) {
        this.display_name = display_name;
        this.incoming = incoming;
        this.outgoing = outgoing;
    }

    /**
     * Increment the number of outgoings by one
     */
    public void succOutgoingCount() {
        this.outgoing++;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Objects.toStringHelper("")
                .add("display_name", this.display_name)
                .add("incoming", this.incoming)
                .add("outgoing", this.outgoing).toString();
    }
}