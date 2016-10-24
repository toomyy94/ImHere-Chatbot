package pt.ua.tomasr.imhere.chat;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import pt.ua.tomasr.imhere.R;

public class ChatActivity extends AppCompatActivity {

    private ListView mListView;
    private Button mButtonSend;
    private EditText mEditTextMessage;
    private ImageView mImageView;
    private String mensagem_do_bot="Bot: ";


    private ChatMessageAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        //Extras
        String extraTitle = getIntent().getStringExtra("chat_title");
        TextView Title = (TextView) findViewById(R.id.chat_title);
        Title.setText(extraTitle); //tittle

        String extrasubTitle = getIntent().getStringExtra("chat_subtitle");
        TextView subTitle = (TextView) findViewById(R.id.chat_subtitle);
        subTitle.setText(extrasubTitle);//subtittle


        mListView = (ListView) findViewById(R.id.listView);
        mButtonSend = (Button) findViewById(R.id.btn_send);
        mEditTextMessage = (EditText) findViewById(R.id.et_message);
        mImageView = (ImageView) findViewById(R.id.iv_image);

        mAdapter = new ChatMessageAdapter(this, new ArrayList<ChatMessage>());
        mListView.setAdapter(mAdapter);


        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Google Auth Info
                String extraFromName = getIntent().getStringExtra("EXTRA_SESSION_Name");
                Log.i("nome",""+extraFromName);

                String message = mEditTextMessage.getText().toString();
                if (TextUtils.isEmpty(message)) {
                    return;
                }
                sendMessage(extraFromName+": "+message);
                mEditTextMessage.setText("");

                //ChatBot
                if(message.startsWith("@")) {

                    //      HTTP GET CLOSESTS POINTS
                    JSONObject json_obtido;
                    message = message.substring(1); //tirar o @
                    if(message.contains(" "))message = message.replace(" ","%20");
                    Log.i("mensagem",message);
                    String URL = "http://192.168.8.217:5010/runService?message="+message;
                    new ChatActivity.GETChatBotMessage().execute(URL);
                    if(mensagem_do_bot.equals("Bot: ")) SystemClock.sleep(1500);
                    mimicOtherMessage(mensagem_do_bot);
                }

            }
        });

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

    }

    private void sendMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, true, false);
        mAdapter.add(chatMessage);

        //mimicOtherMessage(message);
    }

    private void mimicOtherMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, false, false);
        mAdapter.add(chatMessage);
    }

    private void sendMessage() {
        ChatMessage chatMessage = new ChatMessage(null, true, true);
        mAdapter.add(chatMessage);

        mimicOtherMessage();
    }

    private void mimicOtherMessage() {
        ChatMessage chatMessage = new ChatMessage(null, false, true);
        mAdapter.add(chatMessage);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class GETChatBotMessage extends AsyncTask<String, Void, String> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... urls) {

            try {
                StringBuilder result = new StringBuilder();
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

                ArrayList<String> list = new ArrayList<String>();

                if (jArray != null) {
                    int len = jArray.length();
                    for (int i=0;i<len;i++){
                        list.add(jArray.get(i).toString());
                    }
                }
                //for(int i=0; i<list.size();i++) {
                    mensagem_do_bot = "Bot: ";
                    mensagem_do_bot += list.get(0).toString();
                //}

                return mensagem_do_bot;
            } catch (Exception e) {
                Log.e("erro","Erro no GET da Mensagem do bot!!!");
                e.printStackTrace();
            }
            return mensagem_do_bot;
        }

        protected void onPostExecute(Boolean result) {

        }

    }
}
