package com.github.capncanuck.smsstatistics;

import java.util.Locale;
import java.util.Set;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.github.capncanuck.smsstatistics.contacts.Contact;
import com.github.capncanuck.smsstatistics.contacts.ContactList;
import com.github.capncanuck.smsstatistics.contacts.ContactsData;
import com.google.common.collect.Lists;

/**
 * The Class MainActivity.
 */
public class MainActivity extends Activity {

    /**
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        new AsyncGetContacts(this) {
            @Override
            protected void onPostExecute(final Set<Contact> results) {
                super.onPostExecute(results);

                final ContactsData data = new ContactsData(Lists.newArrayList(results));

                // set-up total infobar
                final TextView total = (TextView) this.aty.findViewById(R.id.total);
                final String format = this.aty.getResources().getString(R.string.total);
                total.setText(String.format(Locale.CANADA, format, data.getTotalIncoming(), data.getTotalOutgoing()));

                // set-up list view
                final ListView contactsView = (ListView) this.aty.findViewById(R.id.contacts);
                contactsView.setAdapter(new ContactList(this.aty, data.getList()));
            }
        }.execute();
    }
}