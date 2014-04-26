package com.github.capncanuck.smsstatistics;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.BaseColumns;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.PhoneLookup;

import com.github.capncanuck.smsstatistics.contacts.Contact;
import com.google.common.base.Optional;
import com.google.common.collect.Iterables;

/**
 * Retrieve contacts from a different thread
 */
public class AsyncGetContacts extends AsyncTask<Void, CharSequence, Set<Contact>> {

    /**
     * The url used to find SMS messages.
     */
    private static final Uri smsUri = Uri.parse("content://sms");

    /**
     * Shows the progress of retrieving the contacts
     */
    private final ProgressDialog spinner;

    /**
     * Where the progress bar is shown
     */
    protected final Activity aty;

    /**
     * @param aty where the progress bar is shown
     */
    public AsyncGetContacts(final Activity aty) {
        this.aty = aty;

        // create the progress bar
        this.spinner = new ProgressDialog(aty);
        this.spinner.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        this.spinner.setTitle(R.string.spinnerTitle);
        this.spinner.setIndeterminate(true);
        this.spinner.setCancelable(false);
        this.spinner.setCanceledOnTouchOutside(false);

        // set-up the querier
        Query.setAty(this.aty);
    }

    /**
     * Show the progress bar
     * 
     * @see android.os.AsyncTask#onPreExecute()
     */
    @Override
    protected void onPreExecute() {
        this.spinner.show();
    }

    /**
     * @param message message[0] is the new message to be displayed on the spinner
     */
    @Override
    protected void onProgressUpdate(final CharSequence... message) {
        this.spinner.setMessage(message[0]);
    }

    /**
     * Dismiss the progress bar
     * 
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     */
    @Override
    protected void onPostExecute(final Set<Contact> results) {
        this.spinner.dismiss();
    }

    /**
     * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
     */
    @Override
    protected Set<Contact> doInBackground(final Void... params) {
        // Getting information from the inbox
        this.publishProgress("Getting information from the inbox");

        final Set<Contact> contacts = new Query<Set<Contact>>(Uri.withAppendedPath(smsUri, "inbox"), "address") {
            @Override
            protected Set<Contact> ready(final Cursor cursor) {
                final Set<Contact> contacts = new ConcurrentSkipListSet<Contact>();
                final int address_index = cursor.getColumnIndex("address");

                while (cursor.moveToNext()) {
                    final PhoneNumber number = new PhoneNumber(cursor.getString(address_index));
                    final Optional<Contact> maybeContact = Iterables.tryFind(contacts, Contact.checkNumber(number));

                    if (maybeContact.isPresent()) {
                        final Contact contact = maybeContact.get();
                        contacts.remove(contact);
                        contact.incrIncomingCount();
                        contacts.add(contact);
                    } else {
                        contacts.add(new Contact(number, 1, 0));
                    }
                }

                return contacts;
            }
        }.result();

        // Getting information from the sent box
        this.publishProgress("Getting information from the sent box");

        new Query<Void>(Uri.withAppendedPath(smsUri, "sent"), "address") {
            @Override
            protected Void ready(final Cursor cursor) {
                final int address_index = cursor.getColumnIndex("address");

                while (cursor.moveToNext()) {
                    final PhoneNumber number = new PhoneNumber(cursor.getString(address_index));
                    final Optional<Contact> maybeContact = Iterables.tryFind(contacts, Contact.checkNumber(number));

                    if (maybeContact.isPresent()) {
                        final Contact contact = maybeContact.get();
                        contacts.remove(contact);
                        contact.incrOutgoingCount();
                        contacts.add(contact);
                    } else {
                        contacts.add(new Contact(number, 0, 1));
                    }
                }

                return null;
            }
        }.result();

        // Getting information from the phone lookup directory
        this.publishProgress("Getting information from the phone lookup directory");

        for (final Contact contact : contacts) {
            new Query<Void>(Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(contact.getRawPhoneNumber())),
                    BaseColumns._ID,
                    Contacts.LOOKUP_KEY,
                    Contacts.DISPLAY_NAME,
                    Contacts.PHOTO_URI) {
                @Override
                protected Void ready(final Cursor cursor) {
                    final int id_index = cursor.getColumnIndex(BaseColumns._ID);
                    final int lookup_key_index = cursor.getColumnIndex(Contacts.LOOKUP_KEY);
                    final int display_name_index = cursor.getColumnIndex(Contacts.DISPLAY_NAME);
                    final int photo_uri_index = cursor.getColumnIndex(Contacts.PHOTO_URI);

                    if (cursor.moveToNext()) {
                        contacts.remove(contact);

                        contact.setProfileLink(Contacts.getLookupUri(cursor.getLong(id_index), cursor.getString(lookup_key_index)));
                        contact.setName(cursor.getString(display_name_index));
                        contact.setPhoto(Optional.fromNullable(cursor.getString(photo_uri_index)));

                        contacts.add(contact);
                    }

                    return null;
                }
            }.result();
        }

        return contacts;
    }
}