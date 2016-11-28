package pt.ua.tomasr.imhere.chat;

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
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import pt.ua.tomasr.imhere.R;
import pt.ua.tomasr.imhere.rabitt.MessageBroker;

public class ChatActivity extends AppCompatActivity {

    private ListView mListView;
    private Button mButtonSend;
    private EditText mEditTextMessage;
    private ImageView mImageView;
    private String mensagem_do_bot="Bot: ";

    //messages
    public static ArrayList<String> chatmessages = new ArrayList<String>();
    String user_name = "";

    //Loading
    ProgressBar progress_bar;
    TextView progress_text;

    //Rabbit parameters
    public MessageBroker msg = new MessageBroker();
    String hash = "";
    String user_id = "";
    String extraTitle = "";
    String chat_id = "";


    private ChatMessageAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        //Extras
        chatmessages = getIntent().getStringArrayListExtra("chat_messages");

        extraTitle = getIntent().getStringExtra("chat_title");
        TextView Title = (TextView) findViewById(R.id.chat_title);
        Title.setText(extraTitle); //tittle

        String extrasubTitle = getIntent().getStringExtra("chat_subtitle");
        TextView subTitle = (TextView) findViewById(R.id.chat_subtitle);
        subTitle.setText(extrasubTitle);//subtittle

        hash = getIntent().getStringExtra("EXTRA_SESSION_Hash");
        user_id = getIntent().getStringExtra("EXTRA_SESSION_Email");
        String[] tmp = user_id.split("@");
        user_name = tmp[0];
        chat_id = getIntent().getStringExtra("chat_id");

        //Fazer algo com isto
        String extraId = getIntent().getStringExtra("chat_id");
        //Fazer algo com isto

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
                Log.i("nome", "" + extraFromName);

                String message = mEditTextMessage.getText().toString();
                if (TextUtils.isEmpty(message)) {
                    return;
                }
                sendMessage(extraFromName + ": " + message);
                mEditTextMessage.setText("");

                //Rabbit message
                new RabbitSendMessage().execute(message);

                //ChatBot
//                if(message.startsWith("@")) {
//
//                    //      HTTP GET CLOSESTS POINTS
//                    JSONObject json_obtido;
//                    message = message.substring(1); //tirar o @
//                    if(message.contains(" "))message = message.replace(" ","%20");
//                    Log.i("mensagem",message);
//                    String URL = "http://192.168.8.217:5010/runService?message="+message;
//                    new ChatActivity.GETChatBotMessage().execute(URL);
//                    if(mensagem_do_bot.equals("Bot: ")) SystemClock.sleep(1500);
//                    mimicOtherMessage(mensagem_do_bot);
//                }

            }
        });

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        //historico de mensagens
        //chatmessages.clear();

        Log.i("chat-messages", chatmessages.toString());
        if (chatmessages.size() == 0) {
            progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
            progress_text = (TextView) findViewById(R.id.progress_text);
            progress_text.setText("Loading old messages...");
            progress_bar.setVisibility(View.VISIBLE);
            progress_text.setVisibility(View.VISIBLE);
            SystemClock.sleep(2000);
            progress_bar.setVisibility(View.GONE);
            progress_text.setVisibility(View.GONE);
        }

        //enviar para chat
        for (int i = 0; i < chatmessages.size(); i++) {
            if (chatmessages.get(i).toString().contains(user_name)) {
                sendMessage(chatmessages.get(i));
            } else mimicOtherMessage(chatmessages.get(i));
        }
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

//    private class GETChatBotMessage extends AsyncTask<String, Void, String> {
//        private ProgressDialog pDialog;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//        }
//
//        @Override
//        protected String doInBackground(String... urls) {
//
//            try {
//                StringBuilder result = new StringBuilder();
//                URL url = new URL(urls[0]);
//                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                conn.setRequestMethod("GET");
//                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                String line;
//                while ((line = rd.readLine()) != null) {
//                    result.append(line);
//                }
//                rd.close();
//
//                String resultado = result.toString();
//                JSONArray jArray = new JSONArray(resultado);
//
//                ArrayList<String> list = new ArrayList<String>();
//
//                if (jArray != null) {
//                    int len = jArray.length();
//                    for (int i=0;i<len;i++){
//                        list.add(jArray.get(i).toString());
//                    }
//                }
//                //for(int i=0; i<list.size();i++) {
//                    mensagem_do_bot = "Bot: ";
//                    mensagem_do_bot += list.get(0).toString();
//                //}
//
//                return mensagem_do_bot;
//            } catch (Exception e) {
//                Log.e("erro","Erro no GET da Mensagem do bot!!!");
//                e.printStackTrace();
//            }
//            return mensagem_do_bot;
//        }
//
//        protected void onPostExecute(Boolean result) {
//
//        }
//
//    }

    private class RabbitSendMessage extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... urls) {

            MessageBroker msg = new MessageBroker();
            msg.connect();

            try {

                String mensagem = "{\"op_id\":2,\"hash\":\""+hash+"\",\"chat_id\":\""+chat_id+"\",\"msg\":\""+urls[0]+"\"}";

                msg.publish("hello",mensagem);

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
