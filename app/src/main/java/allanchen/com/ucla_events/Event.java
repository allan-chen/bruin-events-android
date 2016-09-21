package allanchen.com.ucla_events;

import android.util.Log;

import java.awt.font.TextAttribute;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

/**
 * Created by Allan on 6/26/2016.
 */
public class Event implements Serializable,Comparator{

    public static final int MAX_SHORT_DESCRIPT_LENGTH = 40;
    public static final String TAG = "Event Object";

    private String mID;

    private ArrayList<String> mTags;
    private String mTitle;
    private String mLocation;
    private String mShortDescription;
    private String mLongDescription;
    private String mHost;
    private String mEventLink;
    private Date mDate;
    private boolean mIsFavorite;
    private int mRelevanceIndex;
    private int mNumParticipants;

    public String getHost() {
        return mHost==null?"No host specified":mHost;
    }

    public void setHost(String host) {
        mHost = host;
    }

    public String getEventLink() {
        return mEventLink;
    }

    public void setEventLink(String eventLink) {
        mEventLink = eventLink;
    }

    public Event(String ID) {
        mID = ID;
        mTags = new ArrayList<>();
        mIsFavorite=false;
    }

    public String getID() {
        return mID;
    }

    public String getTitle() {
        return mTitle!=null?mTitle:"Undefined Title";
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public int getNumTags() {
        return mTags.size();
    }

    public ArrayList<String> getTagsArray(){
        return mTags;
    }

    public String getTagString(){
        String tags = "";

        if(getNumTags()==0) return "No tags specified";
        for(int i=0;i<mTags.size() && i<3;i++){
            tags+= mTags.get(i)+", ";
        }
        tags=tags.substring(0,tags.length()-2);
        return tags;
    }
    public void setTags(ArrayList<String> tags) {
        mTags = tags;
    }

    public String getLocation() {
        return mLocation==null?"No location specified":mLocation;
    }

    public void setLocation(String location) {
        mLocation = location;
    }

    public String getShortDescription() {
        return mShortDescription==null?"No description": mShortDescription;
    }

    public void setShortDescription(String shortDescription) {
        mShortDescription = shortDescription;
    }

    public String getLongDescription() {
        return mLongDescription==null?"No description": mLongDescription;
    }

    public void setLongDescription(String longDescription) {
        mLongDescription = longDescription;
    }

    public String getDateTime() {
        SimpleDateFormat df = new SimpleDateFormat();
        df.applyPattern("EEE, MMM d 'at' hh:mm aaa");
        return mDate==null?"No date or time specified":df.format(mDate);
    }
    public String getDate(){
        SimpleDateFormat df = new SimpleDateFormat();
        df.applyPattern("EEE, MMM d");
        return mDate==null?"No date specified":df.format(mDate);
    }
    public String getTime(){
        SimpleDateFormat df = new SimpleDateFormat();
        df.applyPattern("hh:mm aaa");
        return mDate==null?"No time specified":df.format(mDate);
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public void setDate(String dateStr){
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
            setDate(dateFormat.parse(dateStr));
        }
        catch (ParseException p){
            Log.e(TAG,p.getMessage());
            mDate=null;
        }
    }

    public boolean isFavorite() {
        return mIsFavorite;
    }

    public void setFavorite(boolean favorite) {
        mIsFavorite = favorite;
        EventManager.getInstance().setFavEvent(this);
    }

    @Override
    public int compare(Object lhs, Object rhs) {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        return o.getClass()==Event.class && ((Event)o).getID().equals(getID());
    }
}
