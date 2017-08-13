package com.bluebulls.apps.whatsapputility.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bluebulls.apps.whatsapputility.entity.actors.Event;
import com.bluebulls.apps.whatsapputility.entity.actors.Poll;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by ogil on 03/08/17.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "WA_USER.db";

    public static final String POLL_TABLE_NAME = "polls";
    public static final String POLL_ID_COLUMN = "poll_id";
    public static final String POLL_TITLE_COLUMN = "title";
    public static final String POLL_USER_COLUMN = "user";
    public static final String POLL_OPTION_COLUMN = "options";
    public static final String POLL_ANSWER_COL = "answer";

    public static final String EVENT_TABLE_NAME = "events";
    public static final String EVENT_ID_COLUMN = "event_id";
    public static final String EVENT_TOPIC_COLUMN = "topic";
    public static final String EVENT_DESCRIPTION_COLUMN = "description";
    public static final String EVENT_USER_COLUMN = "user";
    public static final String EVENT_PARTICIPANTS_COLUMN = "participants";
    public static final String EVENT_TIME_COLUMN = "time";
    public static final String EVENT_JOINED_COLUMN = "joined";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table " + POLL_TABLE_NAME +
                        "("+ POLL_ID_COLUMN +" text primary key, "+ POLL_TITLE_COLUMN + " text," + POLL_USER_COLUMN +" text,"+ POLL_OPTION_COLUMN + " text,"+ POLL_ANSWER_COL + " text)"
        );

        db.execSQL(
                "create table " + EVENT_TABLE_NAME +
                        "("+ EVENT_ID_COLUMN +" text primary key, "+ EVENT_TOPIC_COLUMN + " text," + EVENT_DESCRIPTION_COLUMN +" text,"+ EVENT_PARTICIPANTS_COLUMN + " text,"+ EVENT_USER_COLUMN + " text,"+ EVENT_JOINED_COLUMN + " text,"+ EVENT_TIME_COLUMN + " text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + POLL_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + EVENT_TABLE_NAME);
        onCreate(db);
    }

    public boolean insertPoll (String poll_id, String title, String user, String options, String ans) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(POLL_ID_COLUMN, poll_id);
        contentValues.put(POLL_TITLE_COLUMN, title);
        contentValues.put(POLL_USER_COLUMN, user);
        contentValues.put(POLL_OPTION_COLUMN, options);
        contentValues.put(POLL_ANSWER_COL, ans);
        db.insert(POLL_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean insertEvent (String event_id, String topic, String description, String participants, String time, String user, String joined) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(EVENT_ID_COLUMN, event_id);
        contentValues.put(EVENT_TOPIC_COLUMN, topic);
        contentValues.put(EVENT_PARTICIPANTS_COLUMN, participants);
        contentValues.put(EVENT_USER_COLUMN, user);
        contentValues.put(EVENT_DESCRIPTION_COLUMN, description);
        contentValues.put(EVENT_TIME_COLUMN, time);
        contentValues.put(EVENT_JOINED_COLUMN, joined);
        db.insert(EVENT_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean insertEvent (Event event, String joined) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(EVENT_ID_COLUMN, event.getEvent_id());
        contentValues.put(EVENT_TOPIC_COLUMN, event.getTopic());
        contentValues.put(EVENT_PARTICIPANTS_COLUMN, event.getParticipants());
        contentValues.put(EVENT_USER_COLUMN, event.getUser());
        contentValues.put(EVENT_DESCRIPTION_COLUMN, event.getDescriptioin());
        contentValues.put(EVENT_TIME_COLUMN, event.getTime());
        contentValues.put(EVENT_JOINED_COLUMN, joined);
        db.insert(EVENT_TABLE_NAME, null, contentValues);
        return true;
    }

    public Poll getPoll(String  poll_id) {
        Poll poll;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+POLL_TABLE_NAME, null );
        res.moveToFirst();

        while(res.isAfterLast() == false) {
            String pollid = res.getString(res.getColumnIndex(POLL_ID_COLUMN));
            if(poll_id.equals(pollid)){
                String user = res.getString(res.getColumnIndex(POLL_USER_COLUMN));
                String options = res.getString(res.getColumnIndex(POLL_OPTION_COLUMN));
                String title = res.getString(res.getColumnIndex(POLL_TITLE_COLUMN));
                String ans = res.getString(res.getColumnIndex(POLL_ANSWER_COL));
                poll = new Poll(poll_id,title,options,user,ans);
                return poll;
            }
            else res.moveToNext();
        }
        return null;
    }

    public Event getEvent(String event_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+EVENT_TABLE_NAME, null );
        res.moveToFirst();

        while(res.isAfterLast() == false) {
            String eventid = res.getString(res.getColumnIndex(EVENT_ID_COLUMN));
            if(event_id.equals(eventid)){
                String user = res.getString(res.getColumnIndex(POLL_USER_COLUMN));
                String description = res.getString(res.getColumnIndex(EVENT_DESCRIPTION_COLUMN));
                String topic = res.getString(res.getColumnIndex(EVENT_TOPIC_COLUMN));
                String time = res.getString(res.getColumnIndex(EVENT_TIME_COLUMN));
                String participants = res.getString(res.getColumnIndex(EVENT_PARTICIPANTS_COLUMN));
                Event event = new Event(event_id,topic,description,participants,user,time);
                return event;
            }
            else res.moveToNext();
        }
        return null;
    }

    public boolean pollExists(String poll_id) throws SQLException {
        int count = -1;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = null;
        try {
            String query = "SELECT COUNT(*) FROM "
                    + POLL_TABLE_NAME + " WHERE " + POLL_ID_COLUMN + " = ?";
            c = db.rawQuery(query, new String[] {poll_id});
            if (c.moveToFirst()) {
                count = c.getInt(0);
            }
            return count > 0;
        }
        finally {
            if (c != null) {
                c.close();
            }
        }
    }

    public boolean eventExists(String event_id) throws SQLException {
        int count = -1;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = null;
        try {
            String query = "SELECT COUNT(*) FROM "
                    + EVENT_TABLE_NAME + " WHERE " + EVENT_ID_COLUMN + " = ?";
            c = db.rawQuery(query, new String[] {event_id});
            if (c.moveToFirst()) {
                count = c.getInt(0);
            }
            return count > 0;
        }
        finally {
            if (c != null) {
                c.close();
            }
        }
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, POLL_TABLE_NAME);
        return numRows;
    }

    public boolean updatePoll (String poll_id, String title, String options) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(POLL_TITLE_COLUMN, title);
        contentValues.put(POLL_OPTION_COLUMN, options);
        db.update(POLL_TABLE_NAME, contentValues, POLL_ID_COLUMN + " = ? ", new String[] { poll_id } );
        return true;
    }

    public boolean updatePollAns(String poll_id, String ans){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(POLL_ANSWER_COL, ans);
        db.update(POLL_TABLE_NAME, contentValues, POLL_ID_COLUMN + " = ? ", new String[] { poll_id } );
        return true;
    }

    public boolean updatePollOptions(String poll_id, String options){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(POLL_OPTION_COLUMN, options);
        db.update(POLL_TABLE_NAME, contentValues, POLL_ID_COLUMN + " = ? ", new String[] { poll_id } );
        return true;
    }

    public boolean updateEventParticipants(String event_id, String participants){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(EVENT_PARTICIPANTS_COLUMN, participants);
        db.update(EVENT_TABLE_NAME, contentValues, EVENT_ID_COLUMN + " = ? ", new String[] { event_id } );
        return true;
    }

    public Integer deletePoll (String poll_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(POLL_TABLE_NAME,
                POLL_ID_COLUMN+" = ? ",
                new String[] { poll_id });
    }

    public Integer deleteEvent (String event_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(EVENT_TABLE_NAME,
                EVENT_ID_COLUMN+" = ? ",
                new String[] { event_id });
    }

    public ArrayList<Poll> getAllPolls() {
        ArrayList<Poll> polls = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+POLL_TABLE_NAME, null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            String poll_id = res.getString(res.getColumnIndex(POLL_ID_COLUMN));
            String user = res.getString(res.getColumnIndex(POLL_USER_COLUMN));
            String options = res.getString(res.getColumnIndex(POLL_OPTION_COLUMN));
            String title = res.getString(res.getColumnIndex(POLL_TITLE_COLUMN));
            String ans = res.getString(res.getColumnIndex(POLL_ANSWER_COL));
            Poll poll = new Poll(poll_id,title,options,user,ans);
            polls.add(poll);
            res.moveToNext();
        }
        Collections.reverse(polls);
        return polls;
    }

    public ArrayList<Event> getAllEvents() {
        ArrayList<Event> events = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+EVENT_TABLE_NAME, null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            String event_id = res.getString(res.getColumnIndex(EVENT_ID_COLUMN));
            String user = res.getString(res.getColumnIndex(POLL_USER_COLUMN));
            String description = res.getString(res.getColumnIndex(EVENT_DESCRIPTION_COLUMN));
            String topic = res.getString(res.getColumnIndex(EVENT_TOPIC_COLUMN));
            String time = res.getString(res.getColumnIndex(EVENT_TIME_COLUMN));
            String participants = res.getString(res.getColumnIndex(EVENT_PARTICIPANTS_COLUMN));
            Event event = new Event(event_id,topic,description,participants,user,time);
            events.add(event);
            res.moveToNext();
        }

        Collections.reverse(events);
        return events;
    }

}