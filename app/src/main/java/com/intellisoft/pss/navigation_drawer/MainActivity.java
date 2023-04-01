package com.intellisoft.pss.navigation_drawer;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.intellisoft.pss.Login;
import com.intellisoft.pss.helper_class.FormatterClass;
import com.intellisoft.pss.NavigationValues;
import com.intellisoft.pss.R;
import com.intellisoft.pss.navigation_drawer.drawer.DataModel;
import com.intellisoft.pss.navigation_drawer.drawer.DrawerItemCustomAdapter;
import com.intellisoft.pss.navigation_drawer.fragments.FragmentAbout;
import com.intellisoft.pss.navigation_drawer.fragments.FragmentDataEntry;
import com.intellisoft.pss.navigation_drawer.fragments.FragmentHelpDesk;
import com.intellisoft.pss.navigation_drawer.fragments.FragmentHome;
import com.intellisoft.pss.navigation_drawer.fragments.FragmentSetPin;
import com.intellisoft.pss.navigation_drawer.fragments.FragmentSettings;
import com.intellisoft.pss.navigation_drawer.fragments.FragmentSubmission;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private String[] mNavigationDrawerItemTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    Toolbar toolbar;
    private CharSequence mTitle;

    ActionBarDrawerToggle mDrawerToggle;
    private FormatterClass formatterClass = new FormatterClass();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTitle = "Connect";
        mNavigationDrawerItemTitles= getResources().getStringArray(R.array.navigation_drawer_items_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        setupToolbar();

        DataModel[] drawerItem = new DataModel[7];

        drawerItem[0] = new DataModel(R.drawable.home, "Home");
        drawerItem[1] = new DataModel(R.drawable.settings, "Settings");
        drawerItem[2] = new DataModel(R.drawable.set_pin, "Set Pin");
        drawerItem[3] = new DataModel(R.drawable.set_pin, "Log out");
        drawerItem[4] = new DataModel();
        drawerItem[5] = new DataModel(R.drawable.about, "About");
        drawerItem[6] = new DataModel(R.drawable.help_desk, "Help Desk");

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);

        DrawerItemCustomAdapter adapter = new DrawerItemCustomAdapter(this, R.layout.list_view_item_row, drawerItem);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        setupDrawerToggle();
    }
    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        String navigation = formatterClass.getSharedPref(NavigationValues.NAVIGATION.name(), this);
        if (navigation == null){
            selectItem(0);
        }else {
            if (navigation.equals(NavigationValues.SUBMISSION.name())){
                selectItem(7);
            }else if (navigation.equals(NavigationValues.DATA_ENTRY.name())) {
                selectItem(8);
            }else {
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
//                fragment = new FragmentAbout();
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
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
//            setTitle(mNavigationDrawerItemTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);

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

    void setupToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    void setupDrawerToggle(){
        mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,toolbar,R.string.app_name, R.string.app_name);
        //This is necessary to change the icon of the Drawer Toggle upon state change.
        mDrawerToggle.syncState();
    }
}