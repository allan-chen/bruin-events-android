package allanchen.com.ucla_events;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "HomeActivity";

    private static final String TAG_EVENT_LIST_FRAGMENT = "home";
    private static final String TAG_CATEGORY_FRAGMENT = "categories";

    private SearchView mSearchView;
    private LoginButton mLoginButton;
    private TextView mUserName;
    private TextView mUserEmail;
    private ImageView mUserProfilePic;

    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
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

        if(getSupportFragmentManager().getFragments() == null || getSupportFragmentManager().getFragments().size()==0) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.home_frame_layout, EventListFragment.newInstance(EventListFragment.PARAM_VIEW_RECENTS, null), TAG_EVENT_LIST_FRAGMENT)
                    .commit();
        }

        final View navHeaderView = navigationView.getHeaderView(0);
        mUserName = (TextView) navHeaderView.findViewById(R.id.nav_user_name);
        mUserEmail = (TextView) navHeaderView.findViewById(R.id.nav_user_email);
        mUserProfilePic = (ImageView) navHeaderView.findViewById(R.id.nav_user_profileImage);

    /*  THE FOLLOWING CODE MOVED TO LOGIN ACTIVITY
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        mCallbackManager = CallbackManager.Factory.create();
        mLoginButton = (LoginButton) navHeaderView.findViewById(R.id.fb_login_button);
        mLoginButton.setReadPermissions("public_profile");
        mLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {

            private ProfileTracker mProfileTracker;

            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();
                final Profile profile = Profile.getCurrentProfile();
                mProfileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                            ((EventListFragment)
                                    getSupportFragmentManager().findFragmentByTag(TAG_EVENT_LIST_FRAGMENT)).refreshEvents();
                            if (currentProfile != null) {
                                Log.d(TAG,"Logged in as "+currentProfile.getName()+", ID:"+currentProfile.getId());
                                mUserName.setText(currentProfile.getName());
                                new ProfilePicFetcherTask().execute(currentProfile.getProfilePictureUri(300, 300).toString());
                            }
                            else{
                                Log.d(TAG,"Not logged in");
                                mUserName.setText(null);
                                mUserProfilePic.setImageDrawable(null);
                            }
                        }
                    };
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
            }
        });*/
        Profile profile = Profile.getCurrentProfile();
        if(profile !=null) {
            Log.d(TAG, "Logged in as " + profile.getName() + ", ID:" + profile.getId());
            mUserName.setText(profile.getName());
            new ProfilePicFetcherTask().execute(profile.getProfilePictureUri(300, 300).toString());
        }
        else Log.d(TAG,"Using guest access");
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
  /*      mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                EventListFragment elf = EventListFragment.newInstance(EventListFragment.PARAM_VIEW_RECENTS,null);
                getSupportFragmentManager().beginTransaction().replace(R.id.home_frame_layout,elf).commit();
                return false;
            }
        });*/
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_item_refresh_list:
                EventListFragment e =
                        (EventListFragment) getSupportFragmentManager().findFragmentByTag(TAG_EVENT_LIST_FRAGMENT);
                e.refreshEvents();
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
                    getSupportActionBar().setTitle("Recents");
                    break;
                case R.id.nav_favorite:
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.home_frame_layout,EventListFragment.newInstance(EventListFragment.PARAM_VIEW_FAVS,null), TAG_EVENT_LIST_FRAGMENT)
                            .commit();
                    getSupportActionBar().setTitle("Favorites");
                    break;
                case R.id.nav_categories:
                    if(existingFragmentTag == null || !existingFragmentTag.equals(TAG_CATEGORY_FRAGMENT)) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.home_frame_layout,CategoryFragment.newInstance(),TAG_CATEGORY_FRAGMENT)
                                .commit();
                        getSupportActionBar().setTitle("Categories");
                    }
                    break;
                case R.id.nav_settings:
                    break;
                case R.id.nav_feedback:
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("message/rfc822");
                    i.putExtra(Intent.EXTRA_EMAIL,new String[]{"allan.chen.97@hotmail.com"});
                    i.putExtra(Intent.EXTRA_SUBJECT,"[BruinEvents] Feedback");
                    if(i.resolveActivity(getPackageManager())!=null)
                        startActivity(Intent.createChooser(i,"Email feedback:"));
                    break;
                default:

            }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private class ProfilePicFetcherTask extends AsyncTask<String,Void,Bitmap>{
        @Override
        protected Bitmap doInBackground(String... params) {
            HttpURLConnection urlConnection;
            try {
                urlConnection = (HttpURLConnection) new URL(params[0]).openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                Bitmap b = BitmapFactory.decodeStream(inputStream);
                urlConnection.disconnect();
                return b;
            }
            catch (MalformedURLException m){

            }
            catch (IOException i){

            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            mUserProfilePic.setImageBitmap(bitmap);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LoginManager.getInstance().logOut();
    }
}
