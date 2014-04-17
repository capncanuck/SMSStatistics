package com.github.capncanuck.smsstatistics;

import java.util.Set;
import java.util.TreeSet;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.PhoneLookup;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;

/**
 * The Class MainActivity.
 */
public class MainActivity extends Activity {

    /**
     * The url used to find SMS messages.
     */
    private static final Uri smsUri = Uri.parse("content://sms");

    /**
     * newline
     */
    private static final CharSequence NL = System.getProperty("line.separator");

    /**
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        final TextView content = (TextView) this.findViewById(R.id.content);
        content.setMovementMethod(new ScrollingMovementMethod());

        Query.setAty(this);

        // Initialze the set of contacts with the phone numbers of incoming texts
        final Set<Contact> contacts = new Query<Set<Contact>>(Uri.withAppendedPath(smsUri, "inbox"), "address") {
            @Override
            protected Set<Contact> ready(final Cursor cursor) {
                final Set<Contact> contacts = new TreeSet<Contact>();

                while (cursor.moveToNext()) {
                    final PhoneNumber number = new PhoneNumber(cursor.getString(0));
                    final Optional<Contact> maybeContact = Iterables.tryFind(contacts, Contact.checkNumber(number));

                    if (maybeContact.isPresent()) {
                        maybeContact.get().succIncomingCount();
                    } else {
                        contacts.add(new Contact(number, 1, 0));
                    }
                }

                return contacts;
            }
        }.result();

        // Update the contacts with the phone numbers of outgoing texts
        new Query<Void>(Uri.withAppendedPath(smsUri, "sent"), "address") {
            @Override
            protected Void ready(final Cursor cursor) {
                while (cursor.moveToNext()) {
                    final PhoneNumber number = new PhoneNumber(cursor.getString(0));
                    final Optional<Contact> maybeContact = Iterables.tryFind(contacts, Contact.checkNumber(number));

                    if (maybeContact.isPresent()) {
                        maybeContact.get().succOutgoingCount();
                    } else {
                        contacts.add(new Contact(number, 0, 1));
                    }
                }

                return null;
            }
        }.result();

        // Update the contacts with their display names
        for (final Contact contact : contacts) {
            new Query<Void>(Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(contact.getRawNumber())), Contacts.DISPLAY_NAME) {
                @Override
                protected Void ready(final Cursor cursor) {
                    contact.setName(cursor.moveToNext() ? cursor.getString(0) : null);
                    return null;
                }
            }.result();
        }

        content.setText(TextUtils.join(NL, new TreeSet<Contact>(contacts)));
    }
}