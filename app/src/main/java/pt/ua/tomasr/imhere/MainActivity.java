package pt.ua.tomasr.imhere;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import pt.ua.tomasr.imhere.chat.ChatActivity;
import pt.ua.tomasr.imhere.modules.GeoChat;
import pt.ua.tomasr.imhere.modules.LocationCoord;
import pt.ua.tomasr.imhere.rabitt.MessageBroker;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private LocationCoord gps = null;
    private List<Circle> mCircle = new ArrayList<>();
    private List<Marker> mMarkers = new ArrayList<>();

    //0-id ; 1-lat ; 2-lon ; 3-radius
    ArrayList<GeoChat> InsideCircle = new ArrayList<GeoChat>();

    //Google Auth Info
    String g_extraFromName = "";
    String g_extraFromEmail = "";
    String g_extraFromId = "";

    //Messages
    String hash = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Google Auth Info
        String extraFromName = getIntent().getStringExtra("EXTRA_SESSION_Name");
        final String extraFromEmail = getIntent().getStringExtra("EXTRA_SESSION_Email");
        String extraFromId = getIntent().getStringExtra("EXTRA_SESSION_Id");
        Uri extraFromPhoto = getIntent().getData();

        //para o rabbit
        g_extraFromEmail = extraFromEmail;

        //Login Message
        new RabbitLoginMessage().execute();

        //------------ TA A FUNCIONAR
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("Device Token: ",refreshedToken);

        //Wifi Manage
        WifiManager wifi;
        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifi.setWifiEnabled(true);//Turn on Wifi

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putString("hash", hash );
                bundle.putString("user_id", extraFromEmail );

                CreateChatFragment fragment = new CreateChatFragment(gps);
                //args
                fragment.setArguments(bundle);
                //-----
                FragmentTransaction fragmentTransaction =
                        getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.commit();
            }
        });

        Log.i("Login:",extraFromName+" está logado!");

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(extraFromId.getBytes("UTF-16")); // Change this to "UTF-16" if needed
            byte[] digest = md.digest();
            final String hashedId = Base64.encodeToString(digest,Base64.DEFAULT);
            hash = hashedId;
            hash = hash.replace("\n","");

            Log.i("hash: ",""+hash);

            //Passar cenas aos fragmentos
        }catch (Exception e){
            Log.e("Erro:","Erro algoritmo de digest Inexistente!");
        }
        //Passar cenas ao Gabriel....
            //...

        //GPS Manage
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("Allow ImHere to access this device's location?");
            dialog.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton("Deny", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                }
            });
            dialog.show();
        }

        gps = new LocationCoord(this);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getBaseContext(), ChatActivity.class);
            startActivityForResult(intent, 1);
        }
        else if(id == R.id.action_signout) {

            Intent intent = new Intent(getBaseContext(), SignInActivity.class);
            startActivityForResult(intent, 1);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        //Google Auth Info
        String tmp_extraFromName = getIntent().getStringExtra("EXTRA_SESSION_Name");
        String tmp_extraFromEmail = getIntent().getStringExtra("EXTRA_SESSION_Email");
        String tmp_extraFromId = getIntent().getStringExtra("EXTRA_SESSION_Id");
        Uri tmp_extraFromPhoto = getIntent().getData();

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            String URL_insidecircle = "http://192.168.8.217:5011/location/insideCircle?latitude="+gps.getLatitude()+
                    "&longitude="+gps.getLongitude();

            new GETInsideCircle().execute(URL_insidecircle);

            if (InsideCircle.size()==0) SystemClock.sleep(1500);

            Bundle bundle = new Bundle();
            bundle.putString("hash", hash );
            bundle.putString("user_id", tmp_extraFromEmail );

            AvailableChatsFragment fragment = new AvailableChatsFragment(InsideCircle);
            //args
            fragment.setArguments(bundle);
            //-----
            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_map) {

            Bundle bundle = new Bundle();
            bundle.putString("hash", hash );
            bundle.putString("user_id", tmp_extraFromEmail );

            MapFragment fragment = new MapFragment(gps);
            //args
            fragment.setArguments(bundle);
            //-----
            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_user) {

            UserProfileFragment fragment = new UserProfileFragment(tmp_extraFromName, tmp_extraFromEmail, tmp_extraFromPhoto);
            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_share) {

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "My location sends via ImHere-ChatBot\nhttp://maps.google.com/maps?q=" + gps.getLatitude() + "," + gps.getLongitude());
            sendIntent.setType("text/plain");
            startActivity(sendIntent);

        } else if (id == R.id.nav_about) {
            AboutFragment fragment = new AboutFragment();
            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class GETInsideCircle extends AsyncTask<String, Void, ArrayList> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected ArrayList doInBackground(String... urls) {

            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
                rd.close();

                String resultado = result.toString();
                JSONArray jArray = new JSONArray(resultado);

                InsideCircle.clear();

                for (int i=0; i < jArray.length(); i++) {

                    JSONObject oneObject = jArray.getJSONObject(i);

                    // Pulling items from the Objects
                    double d_id = oneObject.getDouble("id");
                    double d_latitude = oneObject.getDouble("latitude");
                    double d_longitude = oneObject.getDouble("longitude");
                    double d_radius = oneObject.getDouble("radius");

                    //Add to the list
                    GeoChat geoChat = new GeoChat(d_id,d_latitude,d_longitude,d_radius);
                    InsideCircle.add(geoChat);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return InsideCircle;
        }

        protected void onPostExecute(Boolean result) {

        }

    }

    private class RabbitLoginMessage extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... urls) {

            MessageBroker msg = new MessageBroker();
            msg.connect();

            try {
                //msg.createQueue("hello");
                //msg.consume("hello");
                //msg.publish("hello", "Please work");
                String login = "{\"op_id\":0,\"user_id\":\""+g_extraFromEmail+"\",\"hash\":\""+hash+"\"}";

                msg.publish("hello",login);
                SystemClock.sleep(1500);
                msg.consume(hash);

            }catch (Exception e){
                e.printStackTrace();
                Log.e("error","ERRO RABBIT");
            }

            return "";
        }

        protected void onPostExecute(Boolean result) {

        }

    }
}