package Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Allan on 6/27/2016.
 */
public class EventDBHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;

    public EventDBHelper(Context context) {
        super(context, EventDBSchema.EventTable.NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+ EventDBSchema.EventTable.NAME+"("+
         //   "_id integer primary key autoincrement, "+
                EventDBSchema.EventTable.Cols.ID+","+
                EventDBSchema.EventTable.Cols.TITLE+","+
                EventDBSchema.EventTable.Cols.DATE+","+
                EventDBSchema.EventTable.Cols.SHORT_DESCRIPT+","+
                EventDBSchema.EventTable.Cols.LONG_DESCRIPT+","+
                EventDBSchema.EventTable.Cols.EVENT_LINK+","+
                EventDBSchema.EventTable.Cols.HOST+","+
                EventDBSchema.EventTable.Cols.LOCATION+","+
                EventDBSchema.EventTable.Cols.TAGS+","+
                EventDBSchema.EventTable.Cols.ISFAV+
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
