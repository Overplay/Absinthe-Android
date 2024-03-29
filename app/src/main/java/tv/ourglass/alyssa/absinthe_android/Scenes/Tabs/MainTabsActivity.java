package tv.ourglass.alyssa.absinthe_android.Scenes.Tabs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import tv.ourglass.alyssa.absinthe_android.Networking.NetUtils;
import tv.ourglass.alyssa.absinthe_android.Networking.OGDPBroadcastReceiver;
import tv.ourglass.alyssa.absinthe_android.Networking.OGDPService;
import tv.ourglass.alyssa.absinthe_android.R;
import tv.ourglass.alyssa.absinthe_android.Scenes.Control.OGDevice;

public class MainTabsActivity extends AppCompatActivity {

    String TAG = "MainTabsActivity";

    private BroadcastReceiver mWifiBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "got network change!");
            restartOGDPService();
        }
    };

    private void restartOGDPService() {
        Intent intent = new Intent(this, OGDPService.class);
        stopService(intent);
        startService(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tabs);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        TabsPagerAdapter pagerAdapter = new TabsPagerAdapter(getSupportFragmentManager(),
                MainTabsActivity.this);

        ViewPager viewPager = (ViewPager) findViewById(R.id.container);
        if (viewPager != null) {
            viewPager.setAdapter(pagerAdapter);
        }

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        if (tabLayout != null) {
            tabLayout.setupWithViewPager(viewPager);
        }

        // Iterate over all tabs and set the custom view
        if (tabLayout != null) {
            for (int i = 0; i < tabLayout.getTabCount(); i++) {
                TabLayout.Tab tab = tabLayout.getTabAt(i);
                if (tab != null) {
                    tab.setCustomView(pagerAdapter.getTabView(i));
                }
            }
        }

        // Start OG device finding service
        Intent di = new Intent(this, OGDPService.class);
        startService(di);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        this.registerReceiver(mWifiBroadcastReceiver, intentFilter);
    }

    @Override
    public void onBackPressed() {
        // make back button do nothing
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }
}
