package com.github.capncanuck.smsstatistics;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.PhoneLookup;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;

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
    protected static final CharSequence NL = System.getProperty("line.separator");

    /**
     * For when the display_name is unknown
     */
    protected static final String UNKNOWN = "Unknown";

    /**
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        final TextView content = (TextView) this.findViewById(R.id.content);
        content.setMovementMethod(new ScrollingMovementMethod());

        final Uri inbox = Uri.withAppendedPath(smsUri, "inbox");

        final Multiset<PhoneNumber> numbers = new Query<Multiset<PhoneNumber>>(this, inbox, "address") {
            @Override
            protected Multiset<PhoneNumber> ready(final Cursor cursor) {
                final ImmutableMultiset.Builder<PhoneNumber> builder = ImmutableMultiset.builder();

                for (int i = 0; i < cursor.getCount(); i++) {
                    builder.add(new PhoneNumber(cursor.getString(0)));
                    cursor.moveToNext();
                }

                return builder.build();
            }
        }.result();

        final Set<Entry<PhoneNumber>> entries = numbers.entrySet();
        final Map<PhoneNumber, Contact> contacts = new HashMap<PhoneNumber, Contact>(entries.size());

        for (final Entry<PhoneNumber> number : entries) {
            final PhoneNumber element = number.getElement();
            final int count = number.getCount();

            new Query<Void>(this, Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(element.getRaw())), Contacts.DISPLAY_NAME) {
                @Override
                protected Void ready(final Cursor cursor) {
                    final String display_name = cursor.getCount() == 0 ? UNKNOWN : cursor.getString(0);

                    contacts.put(element, new Contact(display_name, count));

                    return null;
                }

            }.result();
        }

        final Uri sent = Uri.withAppendedPath(smsUri, "sent");

        new Query<Void>(this, sent, "address") {
            @Override
            protected Void ready(final Cursor cursor) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    final PhoneNumber number = new PhoneNumber(cursor.getString(0));
                    final Contact contact = contacts.get(number);

                    if (contact != null) {
                        contact.succOutgoingCount();
                    } else {
                        contacts.put(number, new Contact(UNKNOWN, 0, 1));
                    }

                    cursor.moveToNext();
                }

                return null;
            }
        }.result();

        content.setText(TextUtils.join(NL, contacts.entrySet()));
    }
}