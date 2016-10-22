package pt.ua.tomasr.imhere;

import android.annotation.SuppressLint;
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

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

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

                CreateChatFragment CreateChatFragment = (CreateChatFragment)getFragmentManager().findFragmentById(R.id.fragment_container);
                View frag = CreateChatFragment.getView();

                //Other ID's
                EditText chat_name =(EditText) frag.findViewById(R.id.chat_name);
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

                try {
                    str_chat_radius = Double.parseDouble(chat_radius.getText().toString());

                    post("http://192.168.8.217:5011/location/point",
                            "{\"latitude\":"+gps.getLatitude()+", \"longitude\":"+gps.getLatitude()+", \"radius\":"+str_chat_radius+"}");

                } catch (Exception e) {
                    Log.e("erro","Radius n é um Double ou Erro no Post");
                    Fragment MapFragment = new MapFragment(gps);
                    //Fragment MapFragment = new MapFragment(gps);
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, MapFragment); // give your fragment container id in first parameter
                    transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
                    transaction.commit();
                }


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

    public static void post(String url, String param ) throws Exception{
        String charset = "UTF-8";
        URLConnection connection = new URL(url).openConnection();
        connection.setDoOutput(true); // Triggers POST.
        connection.setRequestProperty("Accept-Charset", charset);
        connection.setRequestProperty("Content-Type", "application/json;charset=" + charset);

        try (OutputStream output = connection.getOutputStream()) {
            output.write(param.getBytes(charset));
        }

        InputStream response = connection.getInputStream();
    }



}
