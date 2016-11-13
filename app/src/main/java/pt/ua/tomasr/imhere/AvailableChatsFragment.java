package pt.ua.tomasr.imhere;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import pt.ua.tomasr.imhere.chat.ChatActivity;
import pt.ua.tomasr.imhere.modules.Chat;
import pt.ua.tomasr.imhere.rabitt.MessageBroker;

/**
 * @author Tomás Rodrigues (tomasrodrigues@ua.pt)
 *  Setember 2016
 */

@SuppressLint("ValidFragment")
public class AvailableChatsFragment extends Fragment {

    List<Chat> insideCircle = new ArrayList<Chat>();
    ArrayList<Double> ids = new ArrayList();

    //Rabbit parameters
    String hash = "";
    String user_id = "";

    public AvailableChatsFragment() {}
    public AvailableChatsFragment(List<Chat> insideCircle) {
        this.insideCircle = insideCircle;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_availablechats,
                container, false);

        Bundle bundle = this.getArguments();
        hash = bundle.getString("hash");

        new RabbitGetChatInfo().execute();

        //Customized List
        ArrayAdapter<Chat> adapter = new MyListAdapter();
        ListView list = (ListView) view.findViewById(R.id.available_chats);
        list.setAdapter(adapter);

        for (Chat tmp:insideCircle) {
            ids.add(tmp.getID());
        }

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked,
                                    int position, long id) {

                Chat chat = insideCircle.get(position);

                String message = "Entering Chat... ";
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();


                String extraFromName = getActivity().getIntent().getExtras().getString("EXTRA_SESSION_Name");
                Intent intent = new Intent(getActivity().getBaseContext(), ChatActivity.class);

                intent.putExtra("EXTRA_SESSION_Name", extraFromName);
                intent.putExtra("chat_title", "era giro ter o nome do chat");
                intent.putExtra("chat_subtitle", "e a descrição...");
                intent.putExtra("chat_id", chat.getID().toString());
                startActivityForResult(intent, 1);
            }
        });


        // Inflate the layout for this fragment
        return view;
    }

    private class MyListAdapter extends ArrayAdapter<Chat> {
        public MyListAdapter() {
            super(getActivity(),R.layout.chat_items, insideCircle);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Make sure we have a view to work with (may have been given null)
            View itemView = convertView;
            if (itemView == null) {
                itemView = getActivity().getLayoutInflater().inflate(R.layout.chat_items, parent, false);
            }

            // Find the car to work with.
            Chat chat = insideCircle.get(position);

            // Fill the view
            ImageView imageView = (ImageView)itemView.findViewById(R.id.item_icon);
            //set...

            // Direita:
            TextView radiusText = (TextView) itemView.findViewById(R.id.item_txtRadius);
            radiusText.setText("Radius: "+chat.getRadius().toString());

            // Esquerda/cima:
            TextView latText = (TextView) itemView.findViewById(R.id.item_txtLat);
            latText.setText("Lat: "+chat.getLat().toString());

            // Esquerda/Baixo:
            TextView lonText = (TextView) itemView.findViewById(R.id.item_txtLon);
            lonText.setText("Lon: "+chat.getLon().toString());

            return itemView;
        }
    }

    private class RabbitGetChatInfo extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... urls) {

            MessageBroker msg = new MessageBroker();
            msg.connect();

            try {
                JSONObject obj = new JSONObject();

                String mensagem = "{\"op_id\":8,\"hash\":\""+hash+"\",\"chat_id\":"+ids+"}";

                msg.publish("hello",mensagem);
                //SystemClock.sleep(2000);

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
