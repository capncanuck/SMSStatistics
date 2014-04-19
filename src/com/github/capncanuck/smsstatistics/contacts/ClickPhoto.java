package com.github.capncanuck.smsstatistics.contacts;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * What happens when the photo is clicked.
 */
public class ClickPhoto implements OnClickListener {

    /**
     * The activity where the click happened.
     */
    private final Context ctx;

    /**
     * Information about the activity to be launched.
     */
    private final Intent intent;

    /**
     * @param ctx the activity where the click happened
     * @param intent information about the activity to be launched
     */
    public ClickPhoto(final Context ctx, final Intent intent) {
        this.ctx = ctx;
        this.intent = intent;
    }

    /**
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(final View v) {
        this.ctx.startActivity(this.intent);
    }

}
