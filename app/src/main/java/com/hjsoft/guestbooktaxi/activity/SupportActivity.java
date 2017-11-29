package com.hjsoft.guestbooktaxi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.hjsoft.guestbooktaxi.adapter.RecyclerAdapter;
import com.hjsoft.guestbooktaxi.fragments.MyRidesFragment;
import com.hjsoft.guestbooktaxi.fragments.SpecificRideFragment;
import com.hjsoft.guestbooktaxi.fragments.SpecificRideOngoingFragment;
import com.hjsoft.guestbooktaxi.fragments.SpecificRideOngoingOtherFragment;
import com.hjsoft.guestbooktaxi.fragments.SpecificSupportFragment;
import com.hjsoft.guestbooktaxi.fragments.SupportFragment;
import com.hjsoft.guestbooktaxi.model.FormattedAllRidesData;
import com.hjsoft.guestbooktaxi.model.NavigationData;

import java.util.ArrayList;

/**
 * Created by hjsoft on 24/4/17.
 */
public class SupportActivity extends AppCompatActivity implements RecyclerAdapter.AdapterCallback {

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
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

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

        Fragment fragment=new SupportFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.content_frame, fragment,"my_rides").commit();
        setTitle("Support");

        adapter.setSelectedItem(4);

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {


                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {


                    mDrawerToggle.setDrawerIndicatorEnabled(false);//showing back button
                    setTitle("Tell us about it");

                    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // onBackPressed();
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.remove(getSupportFragmentManager().findFragmentByTag("support"));
                            fragmentManager.popBackStackImmediate();
                        }
                    });
                }
                else
                {

                    mDrawerToggle.setDrawerIndicatorEnabled(true);
                    setTitle("Support");

                    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            mDrawerLayout.openDrawer(mDrawerList);
                        }
                    });
                }
            }
        });
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
                Intent m=new Intent(this,MyRidesActivity.class);
                startActivity(m);
                finish();
                break;
            case 2:
                Intent k=new Intent(this,PaymentActivity.class);
                startActivity(k);
                finish();
                break;
            case 3:
                Intent j=new Intent(this, RateCardActivity.class);
                startActivity(j);
                finish();
                break;
            case 4:

                break;
            case 5:
                SessionManager s=new SessionManager(getApplicationContext());
                s.logoutUser();
                Intent l=new Intent(this,MainActivity.class);
                l.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                l.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(l);
                finish();
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

            /*
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(mNavigationDrawerItemTitles[position]);
            */
            mDrawerLayout.closeDrawer(mDrawerList);
            finish();
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
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }


    @Override
    public void onMethodCallback(final int position, ArrayList<FormattedAllRidesData> data) {

        Bundle args = new Bundle();
        args.putInt("position",position);
        args.putSerializable("list",data);

        ArrayList<FormattedAllRidesData> list=data;
        FormattedAllRidesData item;

        item=list.get(position);

        String status=item.getRideStatus();

        if(status.equals("ONGOING"))
        {
            Fragment frag = new SpecificSupportFragment();
            frag.setArguments(args);

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.content_frame, frag, "support");
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        }
        else {

            Fragment frag = new SpecificSupportFragment();
                frag.setArguments(args);

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.content_frame, frag, "support");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

        }
    }
}
