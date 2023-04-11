package com.intellisoft.pss.navigation_drawer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.google.android.material.navigation.NavigationView;
import com.intellisoft.pss.Login;
import com.intellisoft.pss.helper_class.DbFileDataEntry;
import com.intellisoft.pss.helper_class.DbSaveDataEntry;
import com.intellisoft.pss.helper_class.FileUpload;
import com.intellisoft.pss.helper_class.FormatterClass;
import com.intellisoft.pss.helper_class.NavigationValues;
import com.intellisoft.pss.R;
import com.intellisoft.pss.helper_class.SubmissionsStatus;
import com.intellisoft.pss.navigation_drawer.fragments.FragmentAbout;
import com.intellisoft.pss.navigation_drawer.fragments.FragmentDataEntry;
import com.intellisoft.pss.navigation_drawer.fragments.FragmentHelpDesk;
import com.intellisoft.pss.navigation_drawer.fragments.FragmentHome;
import com.intellisoft.pss.navigation_drawer.fragments.FragmentSetPin;
import com.intellisoft.pss.navigation_drawer.fragments.FragmentSettings;
import com.intellisoft.pss.navigation_drawer.fragments.FragmentSubmission;
import com.intellisoft.pss.network_request.RetrofitCalls;
import com.intellisoft.pss.room.Image;
import com.intellisoft.pss.room.PssViewModel;
import com.intellisoft.pss.room.Submissions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_IMAGE_PICKER = 1001;
    private String[] mNavigationDrawerItemTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private Toolbar toolbar;
    private CharSequence mTitle;
    private RetrofitCalls retrofitCalls = new RetrofitCalls();
    ActionBarDrawerToggle mDrawerToggle;
    private FormatterClass formatterClass = new FormatterClass();
    private PssViewModel myViewModel;
    private List<Image> imageList;


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
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            if (hour == 0) {
                List<Submissions> submissionList = myViewModel.getUnsyncedSubmissions(this, SubmissionsStatus.SUBMITTED.name());
                for (Submissions sm : submissionList) {
                    DbSaveDataEntry dataEntry = myViewModel.getSubmitSync(this, sm);
                    if (dataEntry != null) {
                        retrofitCalls.submitSyncData(this, dataEntry, sm, myViewModel);
                    }
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
        imageList = new ArrayList<>();
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
        loadAllImages();
    }

    private void loadAllImages() {
        try {
            imageList = myViewModel.getAllImages(MainActivity.this);
            for (Image img : imageList) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    String encodedImage = Base64.getEncoder().encodeToString(img.getImage());
                    DbFileDataEntry dataEntry = new DbFileDataEntry(encodedImage, "");
                    retrofitCalls.submitFileData(MainActivity.this, dataEntry, myViewModel);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


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

    public void selectImage(String userId, String indicatorId, String submissionId) {

        formatterClass.saveSharedPref(FileUpload.USER.name(), userId, MainActivity.this);
        formatterClass.saveSharedPref(FileUpload.INDICATOR.name(), indicatorId, MainActivity.this);
        formatterClass.saveSharedPref(FileUpload.SUBMISSION.name(), submissionId, MainActivity.this);

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_PICKER);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_PICKER && resultCode == Activity.RESULT_OK && data != null) {
            // Get the selected image URI
            Uri selectedImageUri = data.getData();

            try {
                ContentResolver cr = getContentResolver();
                InputStream inputStream = cr.openInputStream(selectedImageUri);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 4; // specify the image compression ratio, higher the ratio smaller the image size
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream); // specify the compression quality
                byte[] imageBytes = byteArrayOutputStream.toByteArray();

                String userId = formatterClass.getSharedPref(FileUpload.USER.name(), this);
                String indicatorId = formatterClass.getSharedPref(FileUpload.INDICATOR.name(), this);
                String submissionId = formatterClass.getSharedPref(FileUpload.SUBMISSION.name(), this);
                Image image = new Image(
                        userId,
                        submissionId,
                        indicatorId,
                        imageBytes);
                myViewModel.uploadImage(MainActivity.this, image);
            } catch (IOException e) {
                e.printStackTrace();
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