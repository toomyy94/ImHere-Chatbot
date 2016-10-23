package pt.ua.tomasr.imhere;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import pt.ua.tomasr.imhere.modules.ApplicationController;
import pt.ua.tomasr.imhere.modules.LocationCoord;

/**
 * @author Tomás Rodrigues (tomasrodrigues@ua.pt)
 *  Setember 2016
 */

@SuppressLint("ValidFragment")
public class CreateChatFragment extends Fragment {

    private LocationCoord gps;

    public CreateChatFragment(LocationCoord gps) {
        this.gps=gps;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_createchat,
                container, false);

        //Public checkbox
//        CompoundButton.OnCheckedChangeListener myCheckboxListener = new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                switch (buttonView.getId()) {
//                    case R.id.checkBox_public:
//                        if (isChecked == false) {
//                            TextView chatpassword = (TextView) view.findViewById(R.id.chat_password);
//
//                            buttonView.setText("Private");
//                            chatpassword.setVisibility(View.VISIBLE);
//                        }
//                }
//            }
//        };

        //Submit Button
        Button btn = (Button) view.findViewById(R.id.submit_chat);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CreateChatFragment CreateChatFragment = (CreateChatFragment) getFragmentManager().findFragmentById(R.id.fragment_container);
                View frag = CreateChatFragment.getView();

                //Other ID's
                EditText chat_name = (EditText) frag.findViewById(R.id.chat_name);
                EditText chat_description = (EditText) frag.findViewById(R.id.chat_description);
                EditText chat_radius = (EditText) frag.findViewById(R.id.chat_radius);
                EditText chat_time = (EditText) frag.findViewById(R.id.chat_time);
                CheckBox checkBox_public = (CheckBox) frag.findViewById(R.id.checkBox_public);
                EditText chat_password = (EditText) frag.findViewById(R.id.chat_password);
                Spinner evento = (Spinner) frag.findViewById(R.id.evento);
                //Get Tetx's
                String str_chat_name = chat_name.getText().toString();
                String str_chat_description = chat_description.getText().toString();
                Double str_chat_radius = 100.0;
                Double str_chat_time = 5.0;
                Boolean str_checkBox_public = checkBox_public.isChecked();
                String str_chat_password = chat_password.getText().toString();
                String str_evento = evento.getSelectedItem().toString();

                String url = "http://192.168.8.217:5011/location/point";
                str_chat_radius = Double.parseDouble(chat_radius.getText().toString());

                final String URL = "http://192.168.8.217:5011/location/point";
                // Post params to be sent to the server
                HashMap<String, Double> params = new HashMap<String, Double>();
                params.put("latitude", gps.getLatitude());
                params.put("longitude", gps.getLongitude());
                params.put("radius", str_chat_radius);

                JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    VolleyLog.v("Response:%n %s", response.toString(4));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.e("Error: ", error.getMessage());
                    }
                });

                // add the request object to the queue to be executed
                ApplicationController.getInstance().addToRequestQueue(req);

//                try {
//                    str_chat_radius = Double.parseDouble(chat_radius.getText().toString());
//
////                    post("http://192.168.8.217:5011/location/point",
////                            "{\"latitude\":"+gps.getLatitude()+", \"longitude\":"+gps.getLatitude()+", \"radius\":"+str_chat_radius+"}");
//                    new PostJson().execute("http://192.168.8.217:5011/location/point",str_chat_radius.toString());
//
//                } catch (Exception e) {
//                    Log.e("erro","Radius n é um Double ou Erro no Post");
//
//                    Fragment MapFragment = new MapFragment(gps);
//                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
//                    transaction.replace(R.id.fragment_container, MapFragment); // give your fragment container id in first parameter
//                    transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
//                    transaction.commit();
//               }

                //Voltar ao mapa
                //Fragment MapFragment = new MapFragment(gps, str_chat_name, str_chat_description, str_chat_radius, str_chat_time, str_checkBox_public, str_chat_password, str_evento);
                Fragment MapFragment = new MapFragment(gps);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, MapFragment); // give your fragment container id in first parameter
                transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
                transaction.commit();
            }
        });


        return view;
    }

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    private class PostJson extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                Double radius = Double.parseDouble(urls[1].toString());
                URL url;
                URLConnection urlConn;
                DataOutputStream printout;
                DataInputStream input;
                url = new URL(urls[0]);
                urlConn = url.openConnection();
                urlConn.setDoInput(true);
                urlConn.setDoOutput(true);
                urlConn.setUseCaches(false);
                urlConn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                //urlConn.setRequestProperty("Host", "android.schoolportal.gr");
                urlConn.connect();
                //Create JSONObject here
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("latitude", gps.getLatitude());
                jsonParam.put("longitude", gps.getLongitude());
                jsonParam.put("radius", radius);

                // Send POST output.
                printout = new DataOutputStream(urlConn.getOutputStream());
                String str = jsonParam.toString();
                byte[] data=str.getBytes("UTF-8");
                printout.write(data);
                printout.flush();
                printout.close();

                return jsonParam.toString();
            }
            catch (Exception e){
                Log.e("Erro","Erro no POST :'(");
                e.printStackTrace();
            }
            return "POST";
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.i("post",""+result);
        }
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    public static void post(String url, String param ) throws MalformedURLException, IOException, UnsupportedEncodingException{
        String charset = "UTF-8";
        URLConnection connection = new URL(url).openConnection();
        connection.setDoOutput(true); // Triggers POST.
        connection.setRequestProperty("Accept-Charset", charset);
        connection.setRequestProperty("Content-Type", "application/json;charset=" + charset);

        try (OutputStream output = connection.getOutputStream()) {
            output.write(param.getBytes(charset));

            Log.i("post","POST feito com sucesso");
        }

        InputStream response = connection.getInputStream();
    }



}
