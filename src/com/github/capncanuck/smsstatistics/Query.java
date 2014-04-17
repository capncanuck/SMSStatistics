package com.github.capncanuck.smsstatistics;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

/**
 * Doesnt Work:
 *     * content://sms/all
 *
 * Works:
 *     * content://sms/inbox
 *         - _id
 *         - thread_id
 *         - address
 *         - person
 *         - date
 *         - date_sent
 *         - protocol
 *         - read
 *         - status
 *         - type
 *         - reply_path_present
 *         - subject
 *         - body
 *         - service_center
 *         - locked
 *         - error_code
 *         - seen
 *     * content://sms/sent
 *         - _id
 *         - thread_id
 *         - address
 *         - person
 *         - date
 *         - date_sent
 *         - protocol
 *         - read
 *         - status
 *         - type
 *         - reply_path_present
 *         - subject
 *         - body
 *         - service_center
 *         - locked
 *         - error_code
 *         - seen
 *    * content://sms/conversations
 *         - thread_id
 *         - msg_count
 *         - snippet
 *    * content://com.android.contacts/phone_lookup/#
 *         - times_contacted
 *         - custom_ringtone
 *         - has_phone_number
 *         - label
 *         - number
 *         - type
 *         - lookup               An opaque value that contains hints on how to find the contact if its row id changed as a result of a sync or aggregation.
 *         - last_time_contacted
 *         - display_name
 *         - in_visible_group
 *         - _id
 *         - starred
 *         - photo_uri
 *         - normalized_number
 *         - custom_vibration
 *         - photo_thumb_uri
 *         - photo_id
 *         - send_to_voicemail
 *
 * @param <Result> the type of the result of the query
 */
public abstract class Query<Result> {

    /**
     * This class's TAG.
     */
    private static final String TAG = "com.github.capncanuck.smsstatistics.Query";

    /**
     * The activity that must terminate on an invalid uri
     */
    private static Activity aty;

    /**
     * The content resolver
     */
    private static ContentResolver resolver;

    /**
     * @param aty the activity all queries will run in
     */
    public static void setAty(final Activity aty) {
        Query.aty = aty;
        Query.resolver = aty.getContentResolver();
    }

    /**
     * The result.
     */
    private Result result;

    /**
     * @param uri the uri to query from
     * @param projection A list of which columns to return. Passing null will
     *         return all columns, which is inefficient.
     */
    public Query(final Uri uri, final String... projection) {
        Cursor cursor = null;

        try {
            cursor = resolver.query(uri, projection, null, null, null);

            if (cursor == null) {
                Log.e(TAG, "Invalid URI: " + uri.toString());
                aty.finish();
            } else {
                this.result = this.ready(cursor);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * Callback.
     *
     * @param cursor the cursor
     * @return the result of the query
     */
    protected abstract Result ready(final Cursor cursor);

    /**
     * Result.
     *
     * @return the object
     */
    public Result result() {
        return this.result;
    }
}
