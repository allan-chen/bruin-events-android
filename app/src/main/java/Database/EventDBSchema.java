package Database;

/**
 * Created by Allan on 6/27/2016.
 */
public class EventDBSchema {
    public static final class EventTable{
        public static final String NAME = "eventDB";

        public static final class Cols {
            public static final String ID="_id";
            public static final String EVENT_LINK="url";
            public static final String SHORT_DESCRIPT = "short_descript";
            public static final String LONG_DESCRIPT = "long_descript";
            public static final String LOCATION = "location";
            public static final String DATE = "date";
            public static final String HOST = "host";
            public static final String TITLE = "title";
            public static final String TAGS = "tags";
        }
    }
    public static final class FavoritesTable{
        public static final String NAME = "favEventsDB";

        public static final class Cols{
            public static final String ID = "_id";
        }
    }
}
