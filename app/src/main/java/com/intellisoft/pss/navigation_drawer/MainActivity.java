package com.intellisoft.pss.navigation_drawer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.intellisoft.pss.Login;
import com.intellisoft.pss.helper_class.DbSaveDataEntry;
import com.intellisoft.pss.helper_class.FormatterClass;
import com.intellisoft.pss.helper_class.NavigationValues;
import com.intellisoft.pss.R;
import com.intellisoft.pss.helper_class.SubmissionsStatus;
import com.intellisoft.pss.navigation_drawer.drawer.DataModel;
import com.intellisoft.pss.navigation_drawer.drawer.DrawerItemCustomAdapter;
import com.intellisoft.pss.navigation_drawer.fragments.FragmentAbout;
import com.intellisoft.pss.navigation_drawer.fragments.FragmentDataEntry;
import com.intellisoft.pss.navigation_drawer.fragments.FragmentHelpDesk;
import com.intellisoft.pss.navigation_drawer.fragments.FragmentHome;
import com.intellisoft.pss.navigation_drawer.fragments.FragmentSetPin;
import com.intellisoft.pss.navigation_drawer.fragments.FragmentSettings;
import com.intellisoft.pss.navigation_drawer.fragments.FragmentSubmission;
import com.intellisoft.pss.network_request.RetrofitCalls;
import com.intellisoft.pss.room.PssViewModel;
import com.intellisoft.pss.room.Submissions;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private String[] mNavigationDrawerItemTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private Toolbar toolbar;
    private CharSequence mTitle;
    private RetrofitCalls retrofitCalls = new RetrofitCalls();
    ActionBarDrawerToggle mDrawerToggle;
    private FormatterClass formatterClass = new FormatterClass();
    private PssViewModel myViewModel;


    private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("dhis2")) {
                // Call your function here
                autoSyncSubmissions();
            }
        }
    };

    private void autoSyncSubmissions() {

        if (activeInternetConnection()) {
            List<Submissions> submissionList = myViewModel.getUnsyncedSubmissions(this, SubmissionsStatus.SUBMITTED.name());
            for (Submissions sm : submissionList) {
                DbSaveDataEntry dataEntry = myViewModel.getSubmitSync(this, sm);
                if (dataEntry != null) {
                    retrofitCalls.submitSyncData(this, dataEntry, sm, myViewModel);
                }
            }
        } else {
            Log.e(TAG, "Sync paused.... No active connection");
        }
    }

    private boolean activeInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(myBroadcastReceiver);
        } catch (Exception e) {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter intentFilter = new IntentFilter("dhis2");
        registerReceiver(myBroadcastReceiver, intentFilter);
        myViewModel = new PssViewModel(((Application) this.getApplicationContext()));
        mDrawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                toolbar, R.string.drawer_open, R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // Do something when the drawer is opened
            }
        };
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Set the listener for menu item clicks
        navigationView.setNavigationItemSelectedListener(this);
    }

//    protected void onCreateOld(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

//        mTitle = "Connect";
//        mNavigationDrawerItemTitles = getResources().getStringArray(R.array.navigation_drawer_items_array);
//        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//        mDrawerList = (ListView) findViewById(R.id.left_drawer);
//        View headerView = LayoutInflater.from(this).inflate(R.layout.drawer_header, null);
//
//        mDrawerList.addHeaderView(headerView);
//
//        setupToolbar();
//
//        DataModel[] drawerItem = new DataModel[7];
//
//        drawerItem[0] = new DataModel(R.drawable.home, "Home");
//        drawerItem[1] = new DataModel(R.drawable.settings, "Settings");
//        drawerItem[2] = new DataModel(R.drawable.set_pin, "Set Pin");
//        drawerItem[3] = new DataModel(R.drawable.set_pin, "Log out");
//        drawerItem[4] = new DataModel();
//        drawerItem[5] = new DataModel(R.drawable.about, "About");
//        drawerItem[6] = new DataModel(R.drawable.help_desk, "Help Desk");
//
//        IntentFilter intentFilter = new IntentFilter("dhis2");
//        registerReceiver(myBroadcastReceiver, intentFilter);
//        myViewModel = new PssViewModel(((Application) this.getApplicationContext()));
//        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
//        getSupportActionBar().setHomeButtonEnabled(true);
//
//        DrawerItemCustomAdapter adapter = new DrawerItemCustomAdapter(this, R.layout.list_view_item_row, drawerItem);
//        mDrawerList.setAdapter(adapter);
//        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
//        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//        mDrawerLayout.setDrawerListener(mDrawerToggle);
//
//        setupDrawerToggle();
//    }
//    private class DrawerItemClickListener implements ListView.OnItemClickListener {
//
//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            Log.e("Main", "Item:::::::" + position);
//            selectItem(position);
//        }
//
//    }

    @Override
    protected void onStart() {
        super.onStart();

        String navigation = formatterClass.getSharedPref(NavigationValues.NAVIGATION.name(), this);
        if (navigation == null) {
            selectItem(0);
        } else {
            if (navigation.equals(NavigationValues.SUBMISSION.name())) {
                selectItem(7);
            } else if (navigation.equals(NavigationValues.DATA_ENTRY.name())) {
                selectItem(8);
            } else {
                selectItem(0);
            }
        }


    }

    private void selectItem(int position) {

        formatterClass.deleteSharedPref(NavigationValues.NAVIGATION.name(), this);
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new FragmentHome();
                break;
            case 1:
                fragment = new FragmentSettings();
                break;
            case 2:
                fragment = new FragmentSetPin();
                break;
            case 3:
                //Log out
                formatterClass.deleteSharedPref("isLoggedIn", this);
                Intent intent = new Intent(this, Login.class);
                startActivity(intent);
                finish();
                break;
            case 4:
//                fragment = new OrganizationFragment();
                break;
            case 5:
                fragment = new FragmentAbout();
                break;
            case 6:
                fragment = new FragmentHelpDesk();
                break;
            case 7:
                fragment = new FragmentSubmission();
                break;
            case 8:
                fragment = new FragmentDataEntry();
                break;

            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment)
                    .addToBackStack(null)
                    .commit();
            mDrawerLayout.closeDrawer(GravityCompat.START);

        } else {
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle menu item clicks here
        int itemId = item.getItemId();
        if (itemId == R.id.nav_home) {
            // Handle Home click
            selectItem(0);
        } else if (itemId == R.id.nav_settings) {
            // Handle Gallery click
            selectItem(1);
        } else if (itemId == R.id.nav_set_pin) {
            // Handle Settings click
            selectItem(2);
        } else if (itemId == R.id.nav_logout) {
            // Handle Settings click
            selectItem(3);
        } else if (itemId == R.id.nav_about) {
            // Handle Settings click
            selectItem(5);
        } else if (itemId == R.id.nav_help) {
            // Handle Settings click
            selectItem(6);
        }

        // Close the navigation drawer
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }
}