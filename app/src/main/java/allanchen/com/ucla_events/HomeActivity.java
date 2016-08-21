package allanchen.com.ucla_events;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "HomeActivity";

    private static final String TAG_EVENT_LIST_FRAGMENT = "home";
    private static final String TAG_CATEGORY_FRAGMENT = "categories";

    private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.home_frame_layout, EventListFragment.newInstance(EventListFragment.PARAM_VIEW_RECENTS,null), TAG_EVENT_LIST_FRAGMENT)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home,menu);

        mSearchView = (SearchView) menu.findItem(R.id.menu_item_search).getActionView();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {


            @Override
            public boolean onQueryTextSubmit(String query) {
                EventListFragment elf = EventListFragment.newInstance(EventListFragment.PARAM_SEARCH_QUERY,query);
                getSupportFragmentManager().beginTransaction().replace(R.id.home_frame_layout,elf).commit();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                EventListFragment elf = EventListFragment.newInstance(EventListFragment.PARAM_VIEW_RECENTS,null);
                getSupportFragmentManager().beginTransaction().replace(R.id.home_frame_layout,elf).commit();
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_item_refresh_list:
                EventListFragment elf = EventListFragment.newInstance(EventListFragment.PARAM_VIEW_RECENTS,null);
                getSupportFragmentManager().beginTransaction().replace(R.id.home_frame_layout,elf).commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        String existingFragmentTag = getSupportFragmentManager().findFragmentById(R.id.home_frame_layout).getTag();

            switch (id) {
                case R.id.nav_home:
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.home_frame_layout, EventListFragment.newInstance(EventListFragment.PARAM_VIEW_RECENTS,null), TAG_EVENT_LIST_FRAGMENT)
                            .commit();
                    break;
                case R.id.nav_categories:
                    if(!existingFragmentTag.equals(TAG_CATEGORY_FRAGMENT)) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.home_frame_layout,CategoryFragment.newInstance(),TAG_CATEGORY_FRAGMENT)
                                .commit();
                    }
                    break;
                default:

            }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
