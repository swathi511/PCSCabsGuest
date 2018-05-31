package com.hjsoft.guestbooktaxi.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.hjsoft.guestbooktaxi.database.DatabaseHandler;
import com.hjsoft.guestbooktaxi.model.LocationData;
import com.hjsoft.guestbooktaxi.model.UserRidesData;

import java.util.ArrayList;

/**
 * Created by hjsoft on 16/11/16.
 */
public class DBAdapter {

    static final String DATABASE_NAME = "cabs.db";
    static final int DATABASE_VERSION = 55;
    public static final int NAME_COLUMN = 1;

    public static final String TABLE_USER_STATUS="create table if not exists "+"USER_STATUS"+
            "( "+"REQUEST_ID text,RIDE_STATUS text);";

    public static final String TABLE_USER_LOCATIONS="create table if not exists "+"USER_LOCATIONS"+
            "( "+"PLACE_ID text,PLACE_NAME text,LAT double,LNG double);";

    public static final String TABLE_WALLET="create table if not exists "+"USER_WALLET"+
            "(BALANCE text,TIME_STAMP text);";

    public static final String TABLE_OS_CITIES="create table if not exists "+"OS_CITIES"+
            "(CITY text);";

    public static final String DB_CANCEL_OPTIONS="create table if not exists CANCEL_OPTIONS(CANCEL_TEXT text,CANCEL_ID text);";


    public SQLiteDatabase db;
    // Context of the application using the database.
    private final Context context;
    // Database open/upgrade helper
    private DatabaseHandler dbHelper;

    public  DBAdapter(Context _context)
    {
        context = _context;
        dbHelper = new DatabaseHandler(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public  DBAdapter open() throws SQLException
    {
        db = dbHelper.getWritableDatabase();
        return this;
    }
    public void close()
    {
        db.close();
    }

    public  SQLiteDatabase getDatabaseInstance()
    {
        return db;
    }

    public void insertUserRide(String tripId,String pickup,String drop,String ride_date,String ride_status){

        db=dbHelper.getWritableDatabase();
        ContentValues newValues = new ContentValues();

        newValues.put("TRIP_ID",tripId);
        newValues.put("PICKUP",pickup);
        newValues.put("DROPLOC",drop);
        newValues.put("RIDE_DATE",ride_date);
        newValues.put("RIDE_STATUS",ride_status);

        db.insert("USER_RIDES", null, newValues);

    }

    public ArrayList<UserRidesData> getAllRideEntries(){

        db=dbHelper.getReadableDatabase();
        Cursor c=db.rawQuery("SELECT * FROM USER_RIDES",null);
        ArrayList<UserRidesData> dataList=new ArrayList<UserRidesData>();
        UserRidesData data;

        if(c.getCount()>0)
        {
            for(int i=0;i<c.getCount();i++)
            {
                c.moveToNext();
                data=new UserRidesData(c.getString(0),c.getString(1),c.getString(2),c.getString(3),c.getString(4));
                dataList.add(data);
            }
        }
        c.close();
        // close();
        return dataList;

    }

    public void updateUserRideEntry(String tripId,String rideStatus)
    {
        db=dbHelper.getWritableDatabase();

        ContentValues newValues = new ContentValues();
        newValues.put("RIDE_STATUS",rideStatus);

        int i=db.update("USER_RIDES",newValues,"TRIP_ID="+" '"+tripId+"' ", null);
    }


    public void insertUserRideStatus(String request_id,String ride_status){

        db=dbHelper.getWritableDatabase();
        ContentValues newValues = new ContentValues();
        // Assign values for each row.
        newValues.put("REQUEST_ID",request_id);
        newValues.put("RIDE_STATUS",ride_status);
        // Insert the row into your table
        db.insert("USER_STATUS", null, newValues);

    }

    public String getUserStatus(String request_id){

        db=dbHelper.getReadableDatabase();
        Cursor c=db.rawQuery("SELECT * FROM USER_STATUS WHERE REQUEST_ID ="+" '"+request_id+"' ",null);
        String status="";

        if(c.getCount()>0) {

            c.moveToNext();
            status=c.getString(1);
        }
        c.close();
        // close();
        return status;

    }



    public void updateUserRideStatus(String request_id,String ride_status)
    {
        db=dbHelper.getWritableDatabase();

        ContentValues newValues = new ContentValues();
        newValues.put("RIDE_STATUS",ride_status);

        int i=db.update("USER_STATUS",newValues,"REQUEST_ID="+" '"+request_id+"' ", null);
    }

    public void insertUserLocation(String place_id,String place_name,double latitude,double longitude){

        db=dbHelper.getWritableDatabase();
        ContentValues newValues = new ContentValues();
        // Assign values for each row.
        newValues.put("PLACE_ID",place_id);
        newValues.put("PLACE_NAME",place_name);
        newValues.put("LAT",latitude);
        newValues.put("LNG",longitude);
        // Insert the row into your table
        db.insert("USER_LOCATIONS", null, newValues);
    }

    public ArrayList<String> getUserLocations(){

        ArrayList<String> place=new ArrayList<>();

        db=dbHelper.getReadableDatabase();
        Cursor c=db.rawQuery("SELECT * FROM USER_LOCATIONS",null);

        if(c.getCount()>0) {
            for(int i=0;i<c.getCount();i++) {
                c.moveToNext();
                place.add(c.getString(1));
            }
        }
        c.close();
        // close();
        return place;

    }

    public String getUserLocationFromPosition(int position){

        String place="";

        db=dbHelper.getReadableDatabase();
        Cursor c=db.rawQuery("SELECT * FROM USER_LOCATIONS",null);

        if(c.getCount()>0) {
            for(int i=0;i<c.getCount();i++) {
                c.moveToNext();
                if(i==position)
                {
                    place=c.getString(1);
                }

            }
        }
        c.close();
        // close();
        return place;

    }

    public double getUserLocationLatFromPosition(int position){

        double place_lat=0;

        db=dbHelper.getReadableDatabase();
        Cursor c=db.rawQuery("SELECT * FROM USER_LOCATIONS",null);

        if(c.getCount()>0) {
            for(int i=0;i<c.getCount();i++) {
                c.moveToNext();
                if(i==position)
                {
                    place_lat=Double.parseDouble(c.getString(2));
                }

            }
        }
        c.close();
        // close();
        return place_lat;

    }

    public double getUserLocationLngFromPosition(int position){

        double place_lng=0;

        db=dbHelper.getReadableDatabase();
        Cursor c=db.rawQuery("SELECT * FROM USER_LOCATIONS",null);

        if(c.getCount()>0) {
            for(int i=0;i<c.getCount();i++) {
                c.moveToNext();
                if(i==position)
                {
                    place_lng=Double.parseDouble(c.getString(3));
                }

            }
        }
        c.close();
        // close();
        return place_lng;

    }

    public boolean checkIfPlaceNameExists(String placeId)
    {
        boolean flag=false;

        db=dbHelper.getReadableDatabase();
        Cursor c=db.rawQuery("SELECT * FROM USER_LOCATIONS",null);

        if(c.getCount()>0) {
            for(int i=0;i<c.getCount();i++) {
                c.moveToNext();

                if(c.getString(1).equals(placeId))
                {
                    flag=true;
                    break;
                }


            }
        }
        c.close();

        return flag;
    }

    public void insertCabEntry(String veh_id,String driver_name,String driver_number)
    {
        db=dbHelper.getWritableDatabase();
        ContentValues newValues = new ContentValues();
        // Assign values for each row.
        newValues.put("VEH_ID",veh_id);
        newValues.put("DRIVER_NAME",driver_name);
        newValues.put("DRIVER_NUMBER",driver_number);
        // Insert the row into your table
        db.insert("CAB_DETAILS", null, newValues);
        //  close();
    }


    public void insertLocEntry(String veh_id,double lat,double lng,String place,String time_updated,String marker,String driver_name,String driver_number)
    {
        db=dbHelper.getWritableDatabase();
        ContentValues newValues = new ContentValues();
        // Assign values for each row.
        newValues.put("VEH_ID",veh_id);
        newValues.put("LATITUDE",lat);
        newValues.put("LONGITUDE",lng);
        newValues.put("PLACE",place);
        newValues.put("TIME_UPDATED",time_updated);
        newValues.put("MARKER",marker);
        newValues.put("DRIVER_NAME",driver_name);
        newValues.put("DRIVER_NUMBER",driver_number);

        // Insert the row into your table
        db.insert("LOC_DETAILS", null, newValues);
    }

    public void updateLocEntry(String veh_id,double lat,double lng,String place,String time_updated,String marker)
    {
        db=dbHelper.getWritableDatabase();

        ContentValues newValues = new ContentValues();
        newValues.put("LATITUDE",lat);
        newValues.put("LONGITUDE",lng);
        newValues.put("PLACE",place);
        newValues.put("TIME_UPDATED",time_updated);

        int i=db.update("LOC_DETAILS",newValues,"VEH_ID="+" '"+veh_id+"' ", null);

        System.out.println("Row  updated..."+i);
    }

    public int getCountLocEntry(String Reg)
    {
        db=dbHelper.getReadableDatabase();
        Cursor c=db.rawQuery("SELECT * FROM LOC_DETAILS WHERE VEH_ID="+" '"+Reg+"' ",null);

        return c.getCount();
    }

    public ArrayList<LocationData> getAllLocEntries(){

        db=dbHelper.getReadableDatabase();
        Cursor c=db.rawQuery("SELECT * FROM LOC_DETAILS",null);
        ArrayList<LocationData> dataList=new ArrayList<LocationData>();
        LocationData data;

        if(c.getCount()>0)
        {
            for(int i=0;i<c.getCount();i++)
            {
                c.moveToNext();
                data=new LocationData(c.getString(0),Double.parseDouble(c.getString(1)),Double.parseDouble(c.getString(2)),c.getString(3),c.getString(4),c.getString(5),c.getString(6),c.getString(7));
                dataList.add(data);
            }
        }
        c.close();
        // close();
        return dataList;

    }

    public void insertCabCatEntry(String veh_cat_id,String veh_cat)
    {
        db=dbHelper.getWritableDatabase();
        ContentValues newValues = new ContentValues();
        // Assign values for each row.
        newValues.put("VEH_CAT_ID",veh_cat_id);
        newValues.put("VEH_CAT",veh_cat);

        // Insert the row into your table
        db.insert("CAB_CATEGORIES", null, newValues);

    }

    public ArrayList<String> getCabCatEntries()
    {
        db=dbHelper.getWritableDatabase();
        Cursor c=db.rawQuery("SELECT * FROM CAB_CATEGORIES",null);
        ArrayList<String> data=new ArrayList<String>();

        if(c.getCount()>0)
        {
            for(int i=0;i<c.getCount();i++)
            {
                c.moveToNext();
                data.add(c.getString(1));
            }
        }
        c.close();


        return data;
    }

    public String getCabCategoryId(String category)
    {
        db=dbHelper.getWritableDatabase();
        System.out.println("category is "+category);
        String catID="";
        String sql="SELECT VEH_CAT_ID FROM CAB_CATEGORIES "+"WHERE VEH_CAT= '"+category+"' ";
        Cursor cursor=db.rawQuery(sql,null);

        while(cursor.moveToNext())
        {
            catID=cursor.getString(0);
        }

        return catID;
    }

    public void insertWalletAmount(String balance,String updatedDate)
    {
        db=dbHelper.getWritableDatabase();
        ContentValues newValues = new ContentValues();
        // Assign values for each row.
        newValues.put("BALANCE",balance);
        newValues.put("TIME_STAMP",updatedDate);
        // newValues.put("UPDATED_TIME",updatedTime);

        // Insert the row into your table
        db.insert("USER_WALLET", null, newValues);
    }

    public void updateWalletAmount(String balance,String updatedDate)
    {
        db=dbHelper.getWritableDatabase();
        ContentValues newValues = new ContentValues();
        // Assign values for each row.
        newValues.put("BALANCE",balance);
        newValues.put("TIME_STAMP",updatedDate);
        // newValues.put("UPDATED_TIME",updatedTime);

        // Insert the row into your table
        db.update("USER_WALLET",newValues,null,null);
    }

    public String getWalletAmount()
    {
        db=dbHelper.getWritableDatabase();
        String amount="0";
        String sql="SELECT BALANCE FROM USER_WALLET";
        Cursor cursor=db.rawQuery(sql,null);

        while(cursor.moveToNext())
        {
            amount=cursor.getString(0);
            if(amount.equals(""))
            {
                amount="0";
            }
        }

        return amount;
    }

    public int isWalletPresent()
    {
        db=dbHelper.getWritableDatabase();
        String amount="";
        String sql="SELECT BALANCE FROM USER_WALLET";
        Cursor cursor=db.rawQuery(sql,null);

        return cursor.getCount();
    }

    public void insertOsCity(String city)
    {
        db=dbHelper.getWritableDatabase();
        ContentValues newValues = new ContentValues();
        // Assign values for each row.
        newValues.put("CITY",city);

        // newValues.put("UPDATED_TIME",updatedTime);

        // Insert the row into your table
        db.insert("OS_CITIES", null, newValues);
    }

    public ArrayList<String> getOsCities()
    {
        ArrayList<String> city=new ArrayList<>();
        db=dbHelper.getWritableDatabase();
        String amount="0";
        String sql="SELECT * FROM OS_CITIES";
        Cursor cursor=db.rawQuery(sql,null);

        while(cursor.moveToNext())
        {
            city.add(cursor.getString(0));

        }

        return city;
    }

    public void deleteOsCities()
    {
        db=dbHelper.getReadableDatabase();
        db.execSQL("delete from OS_CITIES" );
        db.close();
    }

    public void insertCancelOptions(String cancelText,String cancelId)
    {
        db=dbHelper.getWritableDatabase();
        ContentValues newValues = new ContentValues();
        newValues.put("CANCEL_TEXT",cancelText);
        newValues.put("CANCEL_ID",cancelId);

        // Assign values for each row.

        // Insert the row into your table
        db.insert("CANCEL_OPTIONS", null, newValues);
        //  close();
        //  System.out.println("Value inserted");
    }

    public ArrayList<String> getCancelOptions()
    {
        ArrayList<String> cancelData=new ArrayList<>();

        db=dbHelper.getReadableDatabase();
        String sql="SELECT * FROM CANCEL_OPTIONS";
        Cursor c=db.rawQuery(sql,null);

        if(c.getCount()>0)
        {
            for(int i=0;i<c.getCount();i++)
            {
                c.moveToNext();

                cancelData.add(c.getString(0));
            }
        }

        c.close();


        return cancelData;
    }

    public String getCancelId(String cancelText)
    {
        String cancelId="1";
        db=dbHelper.getReadableDatabase();
        String sql="SELECT * FROM CANCEL_OPTIONS WHERE CANCEL_TEXT ="+" '"+cancelText+"'" ;
        Cursor c=db.rawQuery(sql,null);

        if(c.getCount()>0)
        {
            c.moveToNext();
            cancelId=c.getString(1);
        }

        c.close();


        return cancelId;
    }

    public void deleteCancelData()
    {
        db=dbHelper.getReadableDatabase();
        db.execSQL("delete from CANCEL_OPTIONS");
        db.close();
    }
}
