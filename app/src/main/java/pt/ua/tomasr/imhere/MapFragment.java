package pt.ua.tomasr.imhere;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import pt.ua.tomasr.imhere.chat.ChatActivity;
import pt.ua.tomasr.imhere.modules.LocationCoord;

import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_BLUE;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_GREEN;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_MAGENTA;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_ORANGE;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_ROSE;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_VIOLET;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_YELLOW;


/**
 * Tomás Rodrigues (tomasrodrigues@ua.pt)
 * @date Setember 2016
 */

@SuppressLint("ValidFragment")
public class MapFragment extends Fragment implements OnMapReadyCallback {


    //On Map View
    private GoogleMap mMap;
    private List<Circle> mCircle = new ArrayList<>();
    private List<Marker> mMarkers = new ArrayList<>();
    private LocationCoord gps;

    //Gets
    ArrayList<Double> ClosestPoints = new ArrayList<Double>();

    //Variaveis adicionais(create chat)
      private String chat_name, chat_description, chat_password, event_type;
//    private Double chat_radius, chat_time;
//    private Boolean isPublic;

    public MapFragment(LocationCoord gps) {
        this.gps = gps;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//      HTTP GET CLOSESTS POINTS
        JSONObject json_obtido;
        String URL = "http://192.168.8.217:5011/location/closestPoints?latitude="+gps.getLatitude()+
                "&longitude="+gps.getLongitude()+"&points=10&distance=150000";

        new GETClosestsPoints().execute(URL);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_map, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.mapa);
        mapFragment.getMapAsync(this);

        return v;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.i("json",""+ClosestPoints);

        googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        if(ClosestPoints==null) SystemClock.sleep(1500);
        Double[] a_closests = ClosestPoints.toArray(new Double[ClosestPoints.size()]);

        //Desenhar Closest Points
        for(int i=0; i< ClosestPoints.size(); i+=5) {
            addClosestCircle(a_closests[i+2],a_closests[i+3],a_closests[i+4]);
            //addMarker(chat_name, chat_description, event_type);
            addClosestMarker(a_closests[i+2],a_closests[i+3]);
        }


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
        {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Intent intent = new Intent(getActivity().getBaseContext(), ChatActivity.class);
                intent.putExtra("chat_title", marker.getTitle());
                intent.putExtra("chat_subtitle", marker.getSnippet());
                startActivityForResult(intent, 1);
                return true;
            }
        });


        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(gps.getLatitude(), gps.getLongitude()), 2));
    }

    public void addClosestCircle(Double chat_lat, Double chat_lon, Double chat_radius) {
        mCircle.add(mMap.addCircle(new CircleOptions().center(
                new LatLng(chat_lat, chat_lon)).radius(chat_radius).fillColor(Color.argb(120, 0, 0, 200)).strokeColor(Color.argb(90, 0, 0, 200)).strokeWidth(8)));
    }

    public void addMarker(String chat_name, String chat_description, String event_type) {
        float cor = HUE_YELLOW;
        switch (event_type){
            case "Music festival":
                cor = HUE_ROSE;
                break;
            case "Local show":
                cor = HUE_VIOLET;
                break;
            case "Street market":
                cor = HUE_ORANGE;
                break;
            case "Building Reunion":
                cor = HUE_BLUE;
                break;
            case "School/University":
                cor = HUE_GREEN;
                break;
            case "Sport related":
                cor = HUE_MAGENTA;
                break;
            case "Other":
                cor = HUE_YELLOW;
                break;
        }

        mMarkers.add(mMap.addMarker(new MarkerOptions().position(
                new LatLng((gps.getLatitude()), gps.getLongitude())).
                title(chat_name).snippet(event_type + ": "+ chat_description).icon(BitmapDescriptorFactory.
                defaultMarker(cor))));
    }

    public void addClosestMarker(Double chat_lat, Double chat_lon) {
        float cor = HUE_YELLOW;
//        switch (event_type){
//            case "Music festival":
//                cor = HUE_ROSE;
//                break;
//            case "Local show":
//                cor = HUE_VIOLET;
//                break;
//            case "Street market":
//                cor = HUE_ORANGE;
//                break;
//            case "Building Reunion":
//                cor = HUE_BLUE;
//                break;
//            case "School/University":
//                cor = HUE_GREEN;
//                break;
//            case "Sport related":
//                cor = HUE_MAGENTA;
//                break;
//            case "Other":
//                cor = HUE_YELLOW;
//                break;
//        }

        mMarkers.add(mMap.addMarker(new MarkerOptions().position(
                new LatLng(chat_lat, chat_lon)).
                title("falta informação outro serviço").snippet("falta informação outro serviço").icon(BitmapDescriptorFactory.
                defaultMarker(cor))));
    }

    public float distFrom(float lat1, float lng1, float lat2, float lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c);

        return dist;
    }


    private class GETClosestsPoints extends AsyncTask<String, Void, ArrayList> {
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

            for (int i=0; i < jArray.length(); i++) {

                JSONObject oneObject = jArray.getJSONObject(i);

                // Pulling items from the Objects
                double d_id = oneObject.getDouble("id");
                double d_distance = oneObject.getDouble("distance");
                double d_latitude = oneObject.getDouble("latitude");
                double d_longitude = oneObject.getDouble("longitude");
                double d_radius = oneObject.getDouble("radius");

                //Add to the list
                ClosestPoints.add(d_id);
                ClosestPoints.add(d_distance);
                ClosestPoints.add(d_latitude);
                ClosestPoints.add(d_longitude);
                ClosestPoints.add(d_radius);

            }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return ClosestPoints;
        }

        protected void onPostExecute(Boolean result) {

        }

    }



}
