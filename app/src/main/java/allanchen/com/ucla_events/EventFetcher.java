package allanchen.com.ucla_events;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Allan on 6/27/2016.
 */
public class EventFetcher {
    public static final String TAG = "EventFetcher";
    public static final Uri DEF_ENDPOINT = Uri.parse("http://ucla-events.herokuapp.com/api/events");
    public static final Uri QUERY_ENDPOINT = Uri.parse("http://ucla-events.herokuapp.com/api/TAG");

    public class EventSchema{
        public static final String ID="_id";
        public static final String EVENT_LINK="url";
        public static final String DETAILS = "details";
        public static final String LOCATION = "location";
        public static final String DATE = "date";
        public static final String HOST = "host";
        public static final String TITLE = "title";
        public static final String TAGS = "tags";
        public static final String ISFAV = "__v";
    }

    public List<Event> fetchEvents(@Nullable String tagQuery){
        List<Event> events = new ArrayList<>();
        try {
            String jsonString = new String(getUrlBytes(tagQuery));
            JSONArray jsonArray;
            jsonArray= new JSONArray(jsonString);

            for(int i=0;i<jsonArray.length();i++){
                JSONObject currEvent = jsonArray.getJSONObject(i);
                Event event = new Event(currEvent.getString(EventSchema.ID));
                event.setTitle(currEvent.getString(EventSchema.TITLE));
                event.setLocation(currEvent.getString(EventSchema.LOCATION));

                String longDescript = currEvent.getString(EventSchema.DETAILS);
                String shortDescript = longDescript.length()>Event.MAX_SHORT_DESCRIPT_LENGTH
                        ?longDescript.substring(0,Event.MAX_SHORT_DESCRIPT_LENGTH)+"...":longDescript;
                event.setLongDescription(longDescript);
                event.setShortDescription(shortDescript);

                event.setEventLink(currEvent.getString(EventSchema.EVENT_LINK));
                event.setHost(currEvent.getString(EventSchema.HOST));
                event.setFavorite(currEvent.getInt(EventSchema.ISFAV)==1);

                String[] tagArray = new String[3];
                for(int j=0;j<3;j++){
                    tagArray[j]= (String)currEvent.getJSONArray(EventSchema.TAGS).get(j);
                }

                event.setTags(tagArray);

                String dateString = currEvent.getString(EventSchema.DATE);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);

                event.setDate(dateFormat.parse(dateString));
                events.add(event);
            }
        }
        catch (JSONException j){
            Log.e(TAG,"Error parsing JSON",j);
        }
        catch (IOException i){
            Log.e(TAG,i.getMessage(),i);

        }
        catch (ParseException p){
            Log.e(TAG,"Error parsing date",p);
        }
        return events;
    }

    private byte[] getUrlBytes(@Nullable String tagQuery) throws IOException{
        String urlSpec = (tagQuery==null)
                ?DEF_ENDPOINT.toString()
                :QUERY_ENDPOINT.buildUpon().appendPath(tagQuery).build().toString();

        URL url = new URL(urlSpec);

        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

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
}
