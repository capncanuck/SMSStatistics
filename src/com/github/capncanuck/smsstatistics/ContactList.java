package com.github.capncanuck.smsstatistics;

import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

/**
 * Draws out the contact list
 */
public class ContactList extends ArrayAdapter<Contact> {

    /**
     * Where the contact list will be displayed
     */
    private static final int layout = R.layout.contact_row;

    /**
     * Used to inflate the layout.
     */
    private final LayoutInflater inflator;

    /**
     * The list of contacts.
     */
    private final List<Contact> contacts;

    /**
     * @param ctx The current context.
     * @param contacts The objects to represent in the ListView.
     */
    public ContactList(final Context ctx, final List<Contact> contacts) {
        super(ctx, layout, contacts);
        this.inflator = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.contacts = contacts;
    }

    /**
     * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        final View rowView = this.inflator.inflate(layout, parent, false);
        final Contact contact = this.contacts.get(position);
        final Uri photoUri = contact.getPhoto();

        if (photoUri != null) {
            final ImageView photoView = (ImageView) rowView.findViewById(R.id.photo);
            photoView.setImageURI(photoUri);
        }

        return rowView;
    }
}
