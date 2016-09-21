package allanchen.com.ucla_events;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.facebook.Profile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Allan on 6/27/2016.
 */
public class EventManager {
    public static final String TAG = "EventManager";
    private static final String API_ROOT = "http://www.uclalist.com/api";
    private static final Uri DEF_ENDPOINT = Uri.parse(API_ROOT+"/events");
    private static final Uri QUERY_ENDPOINT = Uri.parse(API_ROOT+"/search");
    private static final Uri FAV_MANAGE_ENDPOINT = Uri.parse(API_ROOT+"/user");

    private static final int CONNECTION_TIMEOUT = 20000;

    private class EventSchema{
        public static final String ID="_id";
        public static final String EVENT_LINK="url";
        public static final String DETAILS = "details";
        public static final String LOCATION = "location";
        public static final String DATE = "date";
        public static final String HOST = "host";
        public static final String TITLE = "title";
        public static final String TAGS = "tags";
    }

    private List<Event> mAllEvents;

    public List<Event> getCachedRecentEvents() {
        return mAllEvents;
    }

    private EventManager(){

    }

    private static EventManager sEventManager;

    public static EventManager getInstance(){
        if (sEventManager==null){
            sEventManager = new EventManager();
        }
        return sEventManager;
    }

    public List<Event> getRecentEvents(){
        try {
            return mAllEvents = fetchEvents(new String(getUrlBytes(DEF_ENDPOINT.toString())));
        }
        catch (IOException i){
            Log.e(TAG,i.getMessage());
            return null;
        }
    }


    public List<Event> searchEvents(String tagQuery){
        try {
            return fetchEvents(new String(getUrlBytes(QUERY_ENDPOINT.buildUpon().appendPath(tagQuery)
                    .build().toString())));
        }
        catch (IOException i){
            Log.e(TAG,i.getMessage());
            return null;
        }
    }

    public List<Event> getFavEvents(Context context){
        try {
            List<Event> eventList;
            if (Profile.getCurrentProfile() != null) {
                ArrayList<String> favEventIDs = new ArrayList<>();
                String UID = Profile.getCurrentProfile().getId();
                eventList = fetchEvents(new String(getUrlBytes(FAV_MANAGE_ENDPOINT.buildUpon().appendPath(UID).build().toString())));
                for(Event e : eventList) e.setFavorite(true);
            }
            else {
                List<String> favEventIDs = OfflineEventCache.getInstance(context).fetchGuestFavEventIDs();
                //this method only called when internet connectivity is verified, therefore safe to label online
                //events as favorites by ID
                List<Event> tempEventList = mAllEvents!=null?mAllEvents:getRecentEvents();
                eventList = new ArrayList<>();
                for(Event event:tempEventList){
                    if(favEventIDs.contains(event.getID())){
                        eventList.add(event);
                        event.setFavorite(true);
                    }
                }
            }
            return eventList;
        }
        catch (IOException i){
            Log.e(TAG,i.getMessage());
            return null;
        }
    }

    public void setFavEvent(Event event){
        if(Profile.getCurrentProfile()==null){
            if(event.isFavorite())
                OfflineEventCache.getInstance(null).setOfflineFavorite(event);
            else OfflineEventCache.getInstance(null).removeOfflineFavorite(event);
        }
        else new EventFavSyncTask().execute(event);
    }

    private List<Event> fetchEvents(String jsonString){
        List<Event> events = new ArrayList<>();
     //   HashMap<String,Event> eventHashMap = new HashMap<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonString);


            for(int i=0;i<jsonArray.length();i++){
                JSONObject currEvent = jsonArray.getJSONObject(i);
                Event event = new Event(currEvent.getString(EventSchema.ID));
                event.setTitle(
                        currEvent.has(EventSchema.TITLE)?
                            currEvent.getString(EventSchema.TITLE):"Untitled");
                event.setLocation(
                        currEvent.has(EventSchema.TITLE)?
                                currEvent.getString(EventSchema.LOCATION):"Unspecified location");

                if(currEvent.has(EventSchema.DETAILS)) {
                    String longDescript = currEvent.getString(EventSchema.DETAILS);
                    String shortDescript = longDescript.length() > Event.MAX_SHORT_DESCRIPT_LENGTH
                            ? longDescript.substring(0, Event.MAX_SHORT_DESCRIPT_LENGTH) + "..." : longDescript;
                    event.setLongDescription(longDescript);
                    event.setShortDescription(shortDescript);
                }

                event.setEventLink(
                        currEvent.has(EventSchema.EVENT_LINK)?
                            currEvent.getString(EventSchema.EVENT_LINK):"Event link not available");
                event.setHost(currEvent.has(EventSchema.HOST)?
                        currEvent.getString(EventSchema.HOST):"No host specified");

                ArrayList<String> tagList = new ArrayList<>();
                JSONArray array = currEvent.getJSONArray(EventSchema.TAGS);
                for (int j=0;j<array.length();j++){
                    tagList.add((String)array.get(j));
                }

                event.setTags(tagList);

                if(currEvent.has(EventSchema.DATE)) {
                    event.setDate(currEvent.getString(EventSchema.DATE));
                }

                events.add(event);
              //  eventHashMap.put(event.getID(),event);
            }
        }
        catch (JSONException j){
            Log.e(TAG,"Error parsing JSON",j);
        }
        return events;
    }

    private byte[] getUrlBytes(String urlSpec) throws IOException{

        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setConnectTimeout(CONNECTION_TIMEOUT);

        try {
            if(connection.getResponseCode()!=HttpURLConnection.HTTP_OK) {
                throw new IOException("Error connecting to server: " + url.toString());
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            byte[] buffer = new byte[1024];
            int bytesRead = 0;
            while((bytesRead = in.read(buffer))>0){
                out.write(buffer,0,bytesRead);
            }
            return out.toByteArray();
        }
        finally{
            connection.disconnect();
        }
    }
    private class EventFavSyncTask extends AsyncTask<Event,Void,Void>{
        @Override
        protected Void doInBackground(Event... params) {
            try {
                String userID = Profile.getCurrentProfile().getId();
                URL url = new URL(FAV_MANAGE_ENDPOINT.buildUpon().appendPath(userID).appendPath(params[0].getID()).build().toString());
                Log.d(TAG,"Syncing favorite event with server at "+url.toString());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                int rep = connection.getResponseCode();
                Log.d(TAG,"Response code:"+ rep);
                connection.disconnect();
            }
            catch (MalformedURLException m){
                Log.e(TAG,m.getMessage());
            }
            catch (IOException i){
                Log.e(TAG,i.getMessage());
            }
            return null;
        }
    }
}
