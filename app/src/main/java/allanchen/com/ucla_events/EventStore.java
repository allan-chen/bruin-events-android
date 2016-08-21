package allanchen.com.ucla_events;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import Database.EventDBHelper;
import Database.EventDBSchema;

/**
 * Created by Allan on 7/5/2016.
 */
public class EventStore {
    private static EventStore mEventStore;

    public static final String TAG = "EventStore";

    private SQLiteDatabase mEventBase;

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
            contentValues.put(EventDBSchema.EventTable.Cols.ISFAV,e.isFavorite());
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
            e.setFavorite(c.getInt(c.getColumnIndex(EventDBSchema.EventTable.Cols.ISFAV))==1);

            String tagStr = c.getString(c.getColumnIndex(EventDBSchema.EventTable.Cols.TAGS));
            String[] tagArr = tagStr.split(",");
            e.setTags(tagArr);

            String dateStr = c.getString(c.getColumnIndex(EventDBSchema.EventTable.Cols.DATE));
            dateStr = dateStr.replaceAll("at","");
            try {
                e.setDate(new SimpleDateFormat("EEE, MMM dd hh:mm aaa").parse(dateStr));
            }catch (ParseException p){
                Log.e(TAG,"Date parsing error:",p);
            }
            events.add(e);
            c.moveToNext();
        }
        return events;
    }

    private EventStore(Context context){
        mEventBase = new EventDBHelper(context).getWritableDatabase();
    }

    public static EventStore getInstance(Context context){
        if(mEventStore==null){
            mEventStore = new EventStore(context);
        }
        return mEventStore;
    }

    public void setFavorite(Event event){
        mEventBase.execSQL("UPDATE "+ EventDBSchema.EventTable.NAME +
                " SET " + EventDBSchema.EventTable.Cols.ISFAV+" =1" +
                " WHERE "+ EventDBSchema.EventTable.Cols.ID+" = '"+ event.getID()+"'");
    }

    public boolean isFavorite (String id){
        Cursor c = mEventBase.query(EventDBSchema.EventTable.NAME
                ,new String[]{EventDBSchema.EventTable.Cols.ISFAV},EventDBSchema.EventTable.Cols.ID+" = ?",new String[]{id},null,null,null);
        c.moveToFirst();
        return c.getInt(0)==1;
    }
    public void removeFavorite(Event event){
        mEventBase.execSQL("UPDATE "+ EventDBSchema.EventTable.NAME +
                " SET " + EventDBSchema.EventTable.Cols.ISFAV+" =0" +
                " WHERE "+ EventDBSchema.EventTable.Cols.ID+" = '"+ event.getID()+"'");
    }
}
