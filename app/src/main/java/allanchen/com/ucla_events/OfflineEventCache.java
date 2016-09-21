package allanchen.com.ucla_events;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Database.EventDBHelper;
import Database.EventDBSchema;

/**
 * Created by Allan on 7/5/2016.
 */
public class OfflineEventCache {
    private static OfflineEventCache mOfflineEventCache;

    public static final String TAG = "OfflineEventCache";

    private SQLiteDatabase mEventBase;

    public List<String> fetchGuestFavEventIDs(){
        Cursor c = mEventBase.query(EventDBSchema.FavoritesTable.NAME, null, null, null, null, null, null);
        try {
            List<String> favEventIDs = new ArrayList<>();

            c.moveToFirst();
            while (!c.isAfterLast()) {
                favEventIDs.add(c.getString(0));
                c.moveToNext();
            }
            return favEventIDs;
        }
        finally {
            c.close();
        }
    }

    public void setEventCache(List<Event> eventCache) {
        for(Event e :eventCache){
            ContentValues contentValues = new ContentValues();
            contentValues.put(EventDBSchema.EventTable.Cols.ID,e.getID());
            contentValues.put(EventDBSchema.EventTable.Cols.TITLE,e.getTitle());
            contentValues.put(EventDBSchema.EventTable.Cols.DATE,e.getDateTime());
            contentValues.put(EventDBSchema.EventTable.Cols.SHORT_DESCRIPT,e.getShortDescription());
            contentValues.put(EventDBSchema.EventTable.Cols.LONG_DESCRIPT,e.getLongDescription());
            contentValues.put(EventDBSchema.EventTable.Cols.LOCATION,e.getLocation());
            contentValues.put(EventDBSchema.EventTable.Cols.EVENT_LINK,e.getEventLink());
            contentValues.put(EventDBSchema.EventTable.Cols.TAGS,e.getTagString());
            contentValues.put(EventDBSchema.EventTable.Cols.HOST,e.getHost());
            mEventBase.insert(EventDBSchema.EventTable.NAME,null,contentValues);
        }

    }

    public List<Event> getEventCache() {

        List<Event> events = new ArrayList<>();
        Cursor c = mEventBase.query(EventDBSchema.EventTable.NAME,null,null,null,null,null,null);
        c.moveToFirst();
        while(!c.isAfterLast()){
            Event e = new Event(c.getString(c.getColumnIndex(EventDBSchema.EventTable.Cols.ID)));
            e.setTitle(c.getString(c.getColumnIndex(EventDBSchema.EventTable.Cols.TITLE)));
            e.setHost(c.getString(c.getColumnIndex(EventDBSchema.EventTable.Cols.HOST)));
            e.setLocation(c.getString(c.getColumnIndex(EventDBSchema.EventTable.Cols.LOCATION)));
            e.setShortDescription(c.getString(c.getColumnIndex(EventDBSchema.EventTable.Cols.SHORT_DESCRIPT)));
            e.setLongDescription(c.getString(c.getColumnIndex(EventDBSchema.EventTable.Cols.LONG_DESCRIPT)));

            String tagStr = c.getString(c.getColumnIndex(EventDBSchema.EventTable.Cols.TAGS));
            String[] tagArr = tagStr.split(",");
            ArrayList<String> tagList = new ArrayList<>();
            Collections.addAll(tagList,tagArr);
            e.setTags(tagList);

            String dateStr = c.getString(c.getColumnIndex(EventDBSchema.EventTable.Cols.DATE));
            if (!dateStr.equals("No date or time specified")) {
                try {
                    e.setDate(new SimpleDateFormat("EEE, MMM dd 'at' hh:mm aaa").parse(dateStr));
                } catch (ParseException p) {
                    Log.e(TAG, "Date parsing error:", p);
                }
            }
            events.add(e);
            c.moveToNext();
        }

        return events;
    }

    private OfflineEventCache(Context context){
        mEventBase = new EventDBHelper(context).getWritableDatabase();
    }

    public static OfflineEventCache getInstance(Context context){
        if(mOfflineEventCache ==null){
            mOfflineEventCache = new OfflineEventCache(context);
        }
        return mOfflineEventCache;
    }

    public void setOfflineFavorite(Event event){
        ContentValues c = new ContentValues();
        c.put(EventDBSchema.FavoritesTable.Cols.ID,event.getID());
        mEventBase.insert(EventDBSchema.FavoritesTable.NAME,null,c);
    }

    public boolean isOfflineFavorite(String id){
        Cursor c = mEventBase.query(EventDBSchema.FavoritesTable.NAME
                , new String[]{EventDBSchema.FavoritesTable.Cols.ID}, EventDBSchema.FavoritesTable.Cols.ID + " = ?", new String[]{id}, null, null, null);
        try {
            return !c.isNull(0);
        }
        finally {
            c.close();
        }
    }
    public void removeOfflineFavorite(Event event){
        mEventBase.execSQL("DELETE FROM "+ EventDBSchema.EventTable.NAME +
                " WHERE "+ EventDBSchema.EventTable.Cols.ID+" = '"+ event.getID()+"'");
    }
}
