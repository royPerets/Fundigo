package com.example.events;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    ListView list_view;
    public static List<EventInfo> events_data = new ArrayList<EventInfo> ();
    private Button Event, SavedEvent, RealTime;
    boolean didInit = false;
    LoginButton login_button;
    CallbackManager callbackManager;
    private TextView mLatitudeText, mLongitudeText;
    private Location mLastLocation;
    private static boolean turnGps = true;
    private AlertDialog.Builder alertDialog;
    private AlertDialog alert;
    boolean gps_enabled = false;
    boolean network_enabled = false;
    private LocationManager LocationServices;
    public static Location loc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        if (!didInit) {
            uploadUserData ();
            didInit = true;
        }
        setContentView (R.layout.activity_main);
        list_view = (ListView) findViewById (R.id.listView);
        Adapters adapts = new Adapters (this);
        list_view.setAdapter (adapts);
        list_view.setSelector (new ColorDrawable (Color.TRANSPARENT));
        list_view.setOnItemClickListener (this);
        Event = (Button) findViewById (R.id.BarEvent_button);
        SavedEvent = (Button) findViewById (R.id.BarSavedEvent_button);
        RealTime = (Button) findViewById (R.id.BarRealTime_button);
        Event.setOnClickListener (this);
        SavedEvent.setOnClickListener (this);
        RealTime.setOnClickListener (this);

        login_button = (LoginButton) findViewById (R.id.login_button);
        AccessToken accessToken = AccessToken.getCurrentAccessToken ();
        if (accessToken != null) {
            login_button.setVisibility (View.INVISIBLE);
        }

        callbackManager = CallbackManager.Factory.create ();
        login_button.registerCallback (callbackManager, new FacebookCallback<LoginResult> () {
            @Override
            public void onSuccess(LoginResult loginResult) {

            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText (getApplicationContext (), "error", Toast.LENGTH_SHORT).show ();
            }
        });
        loc = getLocation ();
        if (loc == null) turnOnGps ();
        if (loc != null)
            Toast.makeText (getApplicationContext (), "" + loc.getLongitude () + " ," + loc.getLatitude (), Toast.LENGTH_LONG).show ();
    }

    @Override
    public void onClick(View v) {
        Intent newIntent = null;
        if (v.getId () == SavedEvent.getId ()) {
            newIntent = new Intent (this, SavedEvent.class);
            startActivity (newIntent);
        } else if(v.getId () == RealTime.getId ()) {
            newIntent = new Intent (this, RealTime.class);
            startActivity (newIntent);
        }
    }
    private void turnOnGps() {
        turnGps = false;
        try {
            gps_enabled = LocationServices.isProviderEnabled (LocationManager.GPS_PROVIDER);
            network_enabled = LocationServices.isProviderEnabled (LocationManager.NETWORK_PROVIDER);
        } catch (Exception x) {

        }
        if (!gps_enabled && !network_enabled) {
            alertDialog = new AlertDialog.Builder (this);
            alertDialog.setMessage ("turn on your GPS").setPositiveButton ("OK", new DialogInterface.OnClickListener () {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent (Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity (intent);
                    dialog.dismiss ();
                }
            }).setNegativeButton ("Cancel", new DialogInterface.OnClickListener () {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss ();
                }
            });
            alertDialog.create ();
            alertDialog.show ();
        }
    }


    /**
     * the function return the lastKnowenLocation
     *
     * @return lastKnowenLocation
     */
    public Location getLocation() {
        LocationManager locationManager = (LocationManager) this.getSystemService (Context.LOCATION_SERVICE);
        if (locationManager != null) {
            Location lastKnownLocationGPS = locationManager.getLastKnownLocation (LocationManager.GPS_PROVIDER);
            if (lastKnownLocationGPS != null) {
                return lastKnownLocationGPS;
            } else {
                if (ActivityCompat.checkSelfPermission (this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return null;
                }
                Location loc = locationManager.getLastKnownLocation (LocationManager.PASSIVE_PROVIDER);
                return loc;
            }
        } else {
            return null;
        }
    }
//    public void city(MenuItem item) {
//        ArrayList<String> list = new ArrayList<String> ();
//
//        String[] locales = Locale.getISOCountries ();
//
//        for (String countryCode : locales) {
//
//            Locale obj = new Locale ("", countryCode);
//
//            System.out.println ("Country Name = " + obj.getDisplayCountry ());
//            list.add (obj.getDisplayCountry ());
//
//        }
//    }
//
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId ()) {
//            case R.id.Filter:
//                openFilterPage (item);
//                return true;
//            default:
//                return super.onOptionsItemSelected (item);
//
//        }
//    }

    public void openFilterPage(View v) {
        Intent filterPageIntent = new Intent (this, FilterPage.class);
        startActivity (filterPageIntent);
    }

    public void uploadUserData() {

        Resources res = this.getResources ();
        String[] eventDate_list;
        String[] eventName_list;
        String[] eventTag_list;
        String[] eventPrice_list;
        String[] eventInfo_list;
        String[] eventPlace_list;

        eventName_list = res.getStringArray (R.array.eventNames);
        eventDate_list = res.getStringArray (R.array.eventDates);
        eventTag_list = res.getStringArray (R.array.eventTags);
        eventPrice_list = res.getStringArray (R.array.eventPrice);
        eventPlace_list = res.getStringArray (R.array.eventPlace);
        eventInfo_list = res.getStringArray (R.array.eventInfo);

        for (int j = 0; j < 3; j++) {
            for (int i = 0; i < 14; i++) {
                events_data.add (new EventInfo (
                                                       R.mipmap.pic0 + i,
                                                       eventDate_list[i],
                                                       eventName_list[i],
                                                       eventTag_list[i],
                                                       eventPrice_list[i],
                                                       eventInfo_list[i],
                                                       eventPlace_list[i])
                );
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> av, View view, int i, long l) {
        Bundle b = new Bundle ();
        Intent intent = new Intent (this, EventPage.class);
        Holder holder = (Holder) view.getTag ();
        EventInfo event = (EventInfo) holder.image.getTag ();
        intent.putExtra ("eventImage", events_data.get (i).getImageId ());
        intent.putExtra ("eventDate", events_data.get (i).getDate ());
        intent.putExtra ("eventName", events_data.get (i).getName ());
        intent.putExtra ("eventTags", events_data.get (i).getTags ());
        intent.putExtra ("eventPrice", events_data.get (i).getPrice ());
        intent.putExtra ("eventInfo", events_data.get (i).getInfo ());
        intent.putExtra ("eventPlace", events_data.get (i).getPlace ());
        b.putInt ("userIndex", i);
        intent.putExtras (b);
        startActivity (intent);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult (requestCode, resultCode, data);
        login_button.setVisibility (View.INVISIBLE);
        callbackManager.onActivityResult (requestCode, resultCode, data);
    }

}
