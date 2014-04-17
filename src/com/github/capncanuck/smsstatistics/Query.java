package com.github.capncanuck.smsstatistics;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

/**
 * <h1>Doesnt Work:</h1>
 * <ul>
 *     <li>content://sms/all</li>
 * </ul>
 *
 * <h1>Works:</h1>
 * <ul>
 *     <li>content://sms/inbox</li>
 *     <ul>
 *         <li>_id</li>
 *         <li>thread_id</li>
 *         <li>address</li>
 *         <li>person</li>
 *         <li>date</li>
 *         <li>date_sent</li>
 *         <li>protocol</li>
 *         <li>read</li>
 *         <li>status</li>
 *         <li>type</li>
 *         <li>reply_path_present</li>
 *         <li>subject</li>
 *         <li>body</li>
 *         <li>service_center</li>
 *         <li>locked</li>
 *         <li>error_code</li>
 *         <li>seen</li>
 *     </ul>
 *     <li>content://sms/sent</li>
 *     <ul>
 *         <li>_id</li>
 *         <li>thread_id</li>
 *         <li>address</li>
 *         <li>person</li>
 *         <li>date</li>
 *         <li>date_sent</li>
 *         <li>protocol</li>
 *         <li>read</li>
 *         <li>status</li>
 *         <li>type</li>
 *         <li>reply_path_present</li>
 *         <li>subject</li>
 *         <li>body</li>
 *         <li>service_center</li>
 *         <li>locked</li>
 *         <li>error_code</li>
 *         <li>seen</li>
 *    </ul>
 *    <li>content://sms/conversations</li>
 *    <ul>
 *         <li>thread_id</li>
 *         <li>msg_count</li>
 *         <li>snippet</li>
 *    </ul>
 *    <li>content://com.android.contacts/phone_lookup/#</li>
 *    <ul>
 *         <li>times_contacted</li>
 *         <li>custom_ringtone</li>
 *         <li>has_phone_number</li>
 *         <li>label</li>
 *         <li>number</li>
 *         <li>type</li>
 *         <li>lookup An opaque value that contains hints on how to find the contact if its row id changed as a result of a sync or aggregation.</li>
 *         <li>last_time_contacted</li>
 *         <li>display_name</li>
 *         <li>in_visible_group</li>
 *         <li>_id</li>
 *         <li>starred</li>
 *         <li>photo_uri</li>
 *         <li>normalized_number</li>
 *         <li>custom_vibration</li>
 *         <li>photo_thumb_uri</li>
 *         <li>photo_id</li>
 *         <li>send_to_voicemail</li>
 *     </ul>
 * </ul>
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
