package com.github.capncanuck.smsstatistics.contacts;

import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.capncanuck.smsstatistics.R;

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
     * The activity in which the contact list is shown.
     */
    private final Context ctx;

    /**
     * @param ctx The current context.
     * @param contacts The objects to represent in the ListView.
     */
    public ContactList(final Context ctx, final List<Contact> contacts) {
        super(ctx, 0, new Contact[contacts.size()]);
        this.ctx = ctx;
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
        final ImageView photoView = (ImageView) rowView.findViewById(R.id.photo);
        final Uri photoUri = contact.getPhoto();

        // set the photo
        if (photoUri != null) {
            photoView.setImageURI(photoUri);

            // TODO: set all of the photo's dimensions to be equal to each other
            // PREREQ: Finish rest of layout
            /*photoView.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    Log.d("", "" + photoView.getDrawable().getBounds());
                    photoView.getDrawable().setBounds(new Rect(0, 0, 64, 64));
                    //photoView.buildLayer();
                    //photoView.forceLayout();
                    //rowView.se
                    rowView.
                    
                    return true;
                }
            });*/
        }

        // set the photo onclick to profile
        photoView.setOnClickListener(contact.getProfileLinkListener(this.ctx));

        // set the display name
        final TextView display_name = (TextView) rowView.findViewById(R.id.display_name);
        display_name.setText(contact.getDisplayName());

        // set the percentage
        final TextView percentage = (TextView) rowView.findViewById(R.id.percentage);
        percentage.setText(contact.getPercentage());

        // set the progress bar
        final ProgressBar percentage_bar = (ProgressBar) rowView.findViewById(R.id.percentage_bar);
        percentage_bar.setProgress(contact.getIncomingBar());
        percentage_bar.setSecondaryProgress(contact.getOutgoingBar());

        // set the phone number
        final TextView phone_number = (TextView) rowView.findViewById(R.id.phone_number);
        phone_number.setText(contact.getPhoneNumber());

        return rowView;
    }
}
