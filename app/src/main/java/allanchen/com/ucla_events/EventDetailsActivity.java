package allanchen.com.ucla_events;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;


/**
 * Created by Allan on 7/4/2016.
 */
public class EventDetailsActivity extends AppCompatActivity {

    private static final String TAG = "EventDetailsActivity";
    private static final String EXTRA_EVENT = "event_extra";

    private Event mEvent;

    private ScrollView mScrollView;

    private TextView mTitleTextView;
    private TextView mDateTimeTextView;
    private TextView mLocationTextView;
    private TextView[] mTags;
    private TextView mShortDescription;
    private TextView mLongDescription;
    private FloatingActionButton mFavoriteActionButton;

    public static Intent newInstance(Context context, Event event){
        Intent i = new Intent(context,EventDetailsActivity.class);

        i.putExtra(EXTRA_EVENT,event);
        return i;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getIntent()==null || (mEvent = (Event) getIntent().getSerializableExtra(EXTRA_EVENT))==null){
            Log.e(TAG,"Cannot start activity without event passed in");
            finish();
        }

        setContentView(R.layout.activity_event);

        final Toolbar t = (Toolbar) findViewById(R.id.toolbar_event);
        setSupportActionBar(t);
        getSupportActionBar().setTitle(R.string.event_details);
        t.setTitleTextColor(getResources().getColor(R.color.white));


        final ColorDrawable cd = new ColorDrawable(getResources().getColor(R.color.colorPrimary));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(cd);


        mScrollView = (ScrollView) findViewById(R.id.event_scroll_view);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            cd.setAlpha(0);
            final ColorDrawable cdStatusBar = new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark));

            mScrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    cd.setAlpha(getAlphaForActionBar(v.getScrollY()));
                    cdStatusBar.setAlpha(getAlphaForActionBar(v.getScrollY()));
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP)
                        getWindow().setStatusBarColor(cdStatusBar.getColor());
                }

                private int getAlphaForActionBar(int scrollY) {
                    int minDist = 0, maxDist = 650;
                    if (scrollY > maxDist) {
                        return 255;
                    } else if (scrollY < minDist) {
                        return 0;
                    } else {
                        int alpha = 0;
                        alpha = (int) ((255.0 / maxDist) * scrollY);
                        return alpha;
                    }
                }
            });
        }
        else{
            cd.setAlpha(1);
        }
        mFavoriteActionButton = (FloatingActionButton) findViewById(R.id.event_detail_fav_floating_action_button);

        if(mEvent.setFavorite(EventStore.getInstance(this).isFavorite(mEvent.getID())))
        {
            mFavoriteActionButton.setImageResource(R.drawable.star_on);
        }

        else{
            mFavoriteActionButton.setImageResource(R.drawable.star_off_white);
        }

        mFavoriteActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FloatingActionButton f = (FloatingActionButton) v;
                if(mEvent.isFavorite()){
                    mEvent.setFavorite(false);
                    EventStore.getInstance(EventDetailsActivity.this).removeFavorite(mEvent);
                    f.setImageResource(R.drawable.star_off_white);
                }
                else{
                    mEvent.setFavorite(true);
                    EventStore.getInstance(EventDetailsActivity.this).setFavorite(mEvent);
                    f.setImageResource(R.drawable.star_on);
                }
            }
        });



        mTitleTextView = (TextView) findViewById(R.id.event_title_text_view);
        mTitleTextView.setText(mEvent.getTitle());

        mLocationTextView = (TextView) findViewById(R.id.event_detail_location_text_view);
        mLocationTextView.setText(mEvent.getLocation());

        mDateTimeTextView = (TextView) findViewById(R.id.event_detail_date_time_text_view);
        mDateTimeTextView.setText(mEvent.getDate()+" | "+mEvent.getTime());

        mLocationTextView = (TextView) findViewById(R.id.event_detail_location_text_view);
        mLocationTextView.setText(mEvent.getLocation());

        mLongDescription = (TextView) findViewById(R.id.event_long_description_text_view);
        mLongDescription.setText(mEvent.getLongDescription() + "\n\n" + mEvent.getLongDescription() + "\n\n"
                + mEvent.getLongDescription()+ "\n\n" + mEvent.getLongDescription()+ "\n\n" + mEvent.getLongDescription()+
                "\n\n" + mEvent.getLongDescription()+ "\n\n" + mEvent.getLongDescription());

        mTags = new TextView[3];
        mTags[0]=(TextView)findViewById(R.id.tag_text_view_1);
        mTags[1]=(TextView)findViewById(R.id.tag_text_view_2);
        mTags[2]=(TextView) findViewById(R.id.tag_text_view_3);

        for(int i=0;i<3;i++){
            mTags[i].setText(mEvent.getTagsArray()[i]);
        }

    }


}
