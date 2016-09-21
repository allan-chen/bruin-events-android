package allanchen.com.ucla_events;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

/**
 * Created by Allan on 9/15/2016.
 */
public class EventListActivity extends SingleFragmentActivity {
    public static final String TAG = "EventListActivity";

    public static final String PARAM_CATEGORY_QUERY = "category_query";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String category = getIntent().getStringExtra(PARAM_CATEGORY_QUERY);
        getSupportActionBar().setTitle(category);
    }

    @Override
    protected Fragment createFragment() {
        String targetCategory = getIntent().getStringExtra(PARAM_CATEGORY_QUERY);
        if(targetCategory==null) return null;
        EventListFragment elf = EventListFragment.newInstance(EventListFragment.PARAM_SEARCH_QUERY,targetCategory);
        return elf;
    }
}
