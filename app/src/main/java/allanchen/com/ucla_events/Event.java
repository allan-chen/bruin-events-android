package allanchen.com.ucla_events;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by Allan on 6/26/2016.
 */
public class Event implements Serializable,Comparator{

    public static final int MAX_SHORT_DESCRIPT_LENGTH = 50;

    private String mID;


    private String[] mTags;
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
        mTags = new String[3];
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
        return mTags.length;
    }

    public String[] getTagsArray(){
        return mTags;
    }

    public String getTagString(){
        String tags = "";

        if(getNumTags()==0) return "No tags";
        return mTags[0]+", "+mTags[1]+", "+mTags[2];
    }
    public void setTags(String[] tags) {
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

    }

    public boolean isFavorite() {
        return mIsFavorite;
    }

    public boolean setFavorite(boolean favorite) {
        return mIsFavorite = favorite;
    }

    @Override
    public int compare(Object lhs, Object rhs) {
        return 0;
    }
}
