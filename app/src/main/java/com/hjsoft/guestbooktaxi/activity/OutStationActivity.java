package com.hjsoft.guestbooktaxi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hjsoft.guestbooktaxi.R;
import com.hjsoft.guestbooktaxi.SessionManager;
import com.hjsoft.guestbooktaxi.adapter.DrawerItemCustomAdapter;
import com.hjsoft.guestbooktaxi.fragments.OutStationFragment;
import com.hjsoft.guestbooktaxi.model.NavigationData;

/**
 * Created by hjsoft on 22/2/17.
 */
public class OutStationActivity extends AppCompatActivity {

    private String[] mNavigationDrawerItemTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    Toolbar toolbar;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    android.support.v7.app.ActionBarDrawerToggle mDrawerToggle;
    DrawerItemCustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mTitle = mDrawerTitle = getTitle();
        mNavigationDrawerItemTitles= getResources().getStringArray(R.array.navigation_drawer_items_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_view_drawer);

        setupToolbar();

        NavigationData[] drawerItem = new NavigationData[6];

        drawerItem[0] = new NavigationData(R.drawable.car, "Book a Cab");
        drawerItem[1] = new NavigationData(R.drawable.history, "My Rides");
        drawerItem[2] = new NavigationData(R.drawable.wallet, "Payments");
        drawerItem[3] = new NavigationData(R.drawable.ratecard,"Rate Card");
        drawerItem[4] = new NavigationData(R.drawable.support,"Support");
        drawerItem[5] = new NavigationData(R.drawable.logout,"Logout");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        adapter = new DrawerItemCustomAdapter(this, R.layout.list_view_item_row, drawerItem);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        setupDrawerToggle();

        Fragment fragment=new OutStationFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.content_frame, fragment,"outstation").commit();
        setTitle("Outstation Booking");
        //adapter.setSelectedItem(1);
        adapter.setSelectedItem(-1);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {

        Fragment fragment = null;
        adapter.setSelectedItem(position);

        switch (position) {
            case 0:
                Intent i=new Intent(this,HomeActivity.class);
                startActivity(i);
                finish();
                break;
            case 1:
                Intent j=new Intent(this,MyRidesActivity.class);
                startActivity(j);
                finish();
                break;

            case 2:
                Intent k=new Intent(this,PaymentActivity.class);
                startActivity(k);
                mDrawerLayout.closeDrawer(mDrawerList);
               // finish();
                break;
            case 3:
                Intent l=new Intent(this,RateCardActivity.class);
                startActivity(l);
                finish();
                break;
            case 4:
                Intent n=new Intent(this,SupportActivity.class);
                startActivity(n);
                finish();
                break;
            case 5:
                SessionManager s=new SessionManager(getApplicationContext());
                s.logoutUser();
                Intent m=new Intent(this,MainActivity.class);
                m.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                m.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(m);
                break;
            default:
                break;
        }

        if (fragment != null) {

            openFragment(fragment,position);

        } else {
            // Log.e("MainActivity", "Error in creating fragment");
        }
    }

    private void openFragment(Fragment fragment,int position){

        Fragment containerFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);

        if (containerFragment.getClass().getName().equalsIgnoreCase(fragment.getClass().getName())) {
            mDrawerLayout.closeDrawer(mDrawerList);
            return;
        }

        else{

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(mNavigationDrawerItemTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
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
        //getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#000000\">" +mTitle + "</font>")));
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    void setupToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        /*
        int titleId = getResources().getIdentifier("toolbar", "id", "android");
        TextView abTitle = (TextView) findViewById(titleId);
        abTitle.setTextColor(Color.parseColor("#000000"));*/
    }

    void setupDrawerToggle(){
        mDrawerToggle = new android.support.v7.app.ActionBarDrawerToggle(this,mDrawerLayout,toolbar,R.string.app_name, R.string.app_name);
        //This is necessary to change the icon of the Drawer Toggle upon state change.
        mDrawerToggle.syncState();

        mDrawerToggle.setDrawerIndicatorEnabled(false);
        mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {

        //super.onBackPressed();
        finish();
    }
}
