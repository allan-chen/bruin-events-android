package allanchen.com.ucla_events;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.List;

/**
 * Created by Allan on 6/26/2016.
 */
public class EventListFragment extends Fragment {

    public static final String TAG = "EventListFragment";

    public static final int PARAM_VIEW_RECENTS = 0;
    public static final int PARAM_SEARCH_QUERY = 1;
    public static final int PARAM_VIEW_FAVS = 2;

    private static final String BUNDLE_KEY_VIEW_MODE = "viewmode";
    private static final String BUNDLE_KEY_QUERY_STRING = "query";

    private RecyclerView mRecyclerView;
    private ViewGroup mLoadingViewGroup;
    private ViewGroup mErrorViewGroup;

    public static EventListFragment newInstance(int param, @Nullable String query){
        EventListFragment e = new EventListFragment();
        Bundle b = new Bundle();
        b.putInt(BUNDLE_KEY_VIEW_MODE,param);
        if(query!=null) b.putCharSequence(BUNDLE_KEY_QUERY_STRING,query);
        e.setArguments(b);
        return e;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.fragment_home,container,false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.event_list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        mLoadingViewGroup = (ViewGroup) v.findViewById(R.id.loading_viewgroup);
        mLoadingViewGroup.setVisibility(View.VISIBLE);
        mErrorViewGroup = (ViewGroup) v.findViewById(R.id.error_viewgroup);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new EventFetcherTask().execute(getArguments().getString(BUNDLE_KEY_QUERY_STRING));
    }

    public void refreshEvents(){
        new EventFetcherTask().execute(getArguments().getString(BUNDLE_KEY_QUERY_STRING));
    }

    private class EventHolder extends RecyclerView.ViewHolder
    implements View.OnClickListener{
        private Event mEvent;

        private TextView mTitle;
        private TextView mShortDescript;
        private TextView mLocation;
        private TextView mTime;
        private TextView mTags;
        private ToggleButton mIsFavorite;

        public EventHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.event_title_text_view);
            mShortDescript = (TextView) itemView.findViewById(R.id.event_description_text_view);
            mLocation = (TextView) itemView.findViewById(R.id.event_location_text_view);
            mTime = (TextView) itemView.findViewById(R.id.event_time_text_view);
            mTags = (TextView) itemView.findViewById(R.id.event_tags_text_view);
            mIsFavorite = (ToggleButton) itemView.findViewById(R.id.event_isfavorite_toggle_button);
            mIsFavorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        EventStore.getInstance(getContext()).setFavorite(mEvent);
                    }
                    else EventStore.getInstance(getContext()).removeFavorite(mEvent);
                }
            });
            itemView.setOnClickListener(this);
        }

        public void bindEvent(Event event){
            mEvent = event;
            mTitle.setText(event.getTitle());
            mShortDescript.setText(event.getShortDescription());
            mLocation.setText(event.getLocation());
            mTime.setText(event.getDate());
            mTags.setText(event.getTagString());
            mIsFavorite.setChecked(EventStore.getInstance(getContext()).isFavorite(event.getID()));
        }

        @Override
        public void onClick(View v) {
            startActivity(EventDetailsActivity.newInstance(getActivity(),mEvent));
        }
    }

    private class EventAdapter extends RecyclerView.Adapter<EventHolder>{

        private List<Event> mEvents;

        public EventAdapter(List<Event> events) {
            mEvents = events;
        }

        @Override
        public void onBindViewHolder(EventHolder holder, int position) {
            holder.bindEvent(mEvents.get(position));
        }

        @Override
        public EventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new EventHolder(LayoutInflater.from(getActivity())
                    .inflate(R.layout.holder_event,parent,false));
        }

        @Override
        public int getItemCount() {
            return mEvents.size();
        }
    }

    private class EventFetcherTask extends AsyncTask<String,Void,List<Event>>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
                mLoadingViewGroup.setVisibility(View.VISIBLE);
                mErrorViewGroup.setVisibility(View.INVISIBLE);
                mRecyclerView.setAdapter(null);

            //check internet connectivity
            ConnectivityManager cm =
                    (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            if(cm.getActiveNetworkInfo()==null || !cm.getActiveNetworkInfo().isConnectedOrConnecting()){
                this.cancel(false);
                Toast.makeText(getContext(),R.string.network_error_toast,Toast.LENGTH_LONG).show();
                List<Event> cache = EventStore.getInstance(getContext()).getEventCache();
                if(cache.size()==0){
                    Toast.makeText(getContext(),R.string.no_offline_data_error_toast,Toast.LENGTH_LONG).show();
                    mErrorViewGroup.setVisibility(View.VISIBLE);
                }
                else {
                    Toast.makeText(getContext(), R.string.no_offline_data_error_toast, Toast.LENGTH_LONG).show();
                    mRecyclerView.setAdapter(new EventAdapter(cache));
                }
                mLoadingViewGroup.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        protected List<Event> doInBackground(String... params) {
            EventFetcher fetcher = new EventFetcher();
            EventStore eventStore = EventStore.getInstance(getActivity());
            switch(getArguments().getInt(BUNDLE_KEY_VIEW_MODE)) {
                case PARAM_VIEW_RECENTS:
                case PARAM_SEARCH_QUERY:
                    return eventStore.fetchEvents(params[0]);
                case PARAM_VIEW_FAVS:
                    return eventStore.fetchFavEvents();
                default:
                    return null;
            }
        }

        @Override
        protected void onPostExecute(List<Event> events) {
            super.onPostExecute(events);

            mLoadingViewGroup.setVisibility(View.INVISIBLE);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            if(events!=null && events.size()!=0) {
                mRecyclerView.setAdapter(new EventAdapter(events));
                EventStore.getInstance(getActivity()).setEventCache(events);
            }
            else {
                TextView t = (TextView) mErrorViewGroup.findViewById(R.id.error_text_view);
                if (getArguments().getInt(BUNDLE_KEY_VIEW_MODE) == PARAM_VIEW_RECENTS) {
                    t.setText(R.string.error_fetching_events);
                }
                else{
                    t.setText(R.string.error_results_empty);
                }
                mErrorViewGroup.setVisibility(View.VISIBLE);
            }
        }
    }

}
