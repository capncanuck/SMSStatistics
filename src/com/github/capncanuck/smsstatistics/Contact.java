package com.github.capncanuck.smsstatistics;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;

/**
 * An SMS Contact.<br>
 * 
 * Includes this name, the phone number, and the number of incoming and outgoing texts
 */
public class Contact implements Comparable<Contact> {

    /**
     * For when the display_name is unknown
     */
    private static final String UNKNOWN = "Unknown";

    /**
     * Checks to see if a contact has the right number<br>
     * 
     * {@link PhoneNumber} -> {@link Contact} -> boolean
     * 
     * @param number the phone number to check
     * @return if the contact has the right number
     */
    public static Predicate<? super Contact> checkNumber(final PhoneNumber number) {
        return new Predicate<Contact>() {
            @Override
            public boolean apply(final Contact input) {
                return input.number.equals(number);
            }
        };
    }

    /**
     * The display_name.
     */
    private String display_name;

    /**
     * The phone number.
     */
    protected final PhoneNumber number;

    /**
     * The number of incoming messages.
     */
    private int incoming;

    /**
     * The number of outgoing messages.
     */
    private int outgoing;

    /**
     * The total number of incoming and outgoing messages.
     */
    private Integer total;

    /**
     * Instantiates a new contact.
     *
     * @param number the phone number
     * @param incoming the number of incoming messages
     * @param outgoing the number of outgoing messages
     */
    public Contact(final PhoneNumber number, final int incoming, final int outgoing) {
        this.number = number;
        this.incoming = incoming;
        this.outgoing = outgoing;
        this.total = this.incoming + this.outgoing;
    }

    /**
     * Increments the count of incomings by one.
     * @return the modified contact
     */
    public Contact incrIncomingCount() {
        this.incoming++;
        this.total++;
        return this;
    }

    /**
     * Increments the count of outgoings by one.
     * @return the modified contact
     */
    public Contact incrOutgoingCount() {
        this.outgoing++;
        this.total++;
        return this;
    }

    /**
     * Set the display name of the contact.
     * 
     * @param display_name the display name
     * @return the modified contact
     */
    public Contact setName(final String display_name) {
        this.display_name = display_name;
        return this;
    }

    /**
     * @return the raw format of the phone number
     */
    public String getRawNumber() {
        return this.number.toRaw();
    }

    /**
     * Sorting Contacts by:<br>
     * <ol>
     *     <li>total incoming and outgoing (greatest to least)</li>
     *     <li>lexical ordering of display name</li>
     *     <li>by phone number if display name is unknown</li>
     * </ol>
     *     
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final Contact other) {
        return ComparisonChain.start()
                .compare(other.total, this.total)
                .compare(this.display_name, other.display_name, Ordering.natural().nullsLast())
                .compare(this.number, other.number)
                .result();
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Objects.toStringHelper("")
                .add("display_name", this.display_name == null ? UNKNOWN : this.display_name)
                .add("phone_number", this.number)
                .add("incoming", this.incoming)
                .add("outgoing", this.outgoing).toString();
    }
}