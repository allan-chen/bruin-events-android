package allanchen.com.ucla_events;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.w3c.dom.Text;

/**
 * Created by Allan on 9/15/2016.
 */
public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";
    private LoginButton mLoginButton;
    private TextView mGuestAccessButton;
    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_login_screen);
        mCallbackManager = CallbackManager.Factory.create();
        mGuestAccessButton = (TextView) findViewById(R.id.text_view_guest_access);
        mGuestAccessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterApplication();
            }
        });
        mLoginButton = (LoginButton) findViewById(R.id.button_facebook_login);
        mLoginButton.setReadPermissions("public_profile");

        ProfileTracker profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                if(currentProfile!=null) enterApplication();
                stopTracking();
            }
        };

        if(Profile.getCurrentProfile()!=null) enterApplication();

        mLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {

            private ProfileTracker mProfileTracker;

            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();
                mProfileTracker = new ProfileTracker() {
                    @Override
                    protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                        Log.d(TAG,"New Profile: "+ Profile.getCurrentProfile());
                        enterApplication();
                        stopTracking();
                    }
                };
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
            }
        });

    }

    private void enterApplication() {
        Intent i = new Intent(LoginActivity.this,HomeActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode,resultCode,data);
    }
}
