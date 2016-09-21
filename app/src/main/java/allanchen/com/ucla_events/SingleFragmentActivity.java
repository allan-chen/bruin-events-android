package allanchen.com.ucla_events;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Allan on 9/15/2016.
 */
public abstract class SingleFragmentActivity extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        FragmentManager fm = getSupportFragmentManager();
        if(fm.findFragmentById(R.id.activity_fragment_frame_layout)==null){
            fm.beginTransaction().add(R.id.activity_fragment_frame_layout,createFragment()).commit();
        }
    }

    protected abstract Fragment createFragment();
}
