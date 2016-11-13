package pt.ua.tomasr.imhere.rabitt;

import android.util.Log;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageBroker {
    private final static String QUEUE_NAME = "hello";
    private Channel channel;
    private Connection connection;


    public void connect(){
        try {
            ConnectionFactory factory = new ConnectionFactory();
            //factory.setHost("x.x.x.x.servidordo.jorge");
            factory.setHost("192.168.8.217");
            factory.setPort(5012);
            factory.setUsername("es");
            factory.setPassword("imhere");
            connection = factory.newConnection();
            channel = connection.createChannel();  
            
        } catch (Exception ex) {
            Logger.getLogger(MessageBroker.class.getName()).log(Level.SEVERE, null, ex);
            Log.e("Erro","erro no connect - rabbit");
        }
    }
    
    public void createQueue(String queue_name) throws IOException{
        channel.queueDeclare(queue_name, false, false, false, null);        
    }
    
    public void publish(String queue_name, String message) throws IOException{
        channel.basicPublish("", queue_name, null, message.getBytes());
        Log.i("Rabbit publish",""+message);
    }
    
    public void consume(String queue_name) throws IOException{
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {

                try {
                    int error = -1;

                    String message = new String(body, "UTF-8");
                    JSONObject obj = new JSONObject(message);
                    if(obj.getInt("op_id")==0) imprime_data(obj);
                    else if(obj.getInt("op_id")==1){
                        error = obj.getInt("response_id");
                        if(error==0) imprime_data(obj);
                        else imprime_error(obj);
                    }
                    else if(obj.getInt("op_id")==2) imprime_data(obj);
                    else if(obj.getInt("op_id")==4) imprime_data(obj);
                    else if(obj.getInt("op_id")==8) imprime_data(obj);
                    else imprime_data(obj);

                }catch(JSONException e){
                    e.printStackTrace();
                    Log.e("erro","Rabbit - JSON Exception");

                }
            }
        };
        channel.basicConsume(queue_name, true, consumer);
    }
    
    public void closeChannel() throws IOException, TimeoutException{
        channel.close();
    }
    
    public void closeConnection() throws IOException{
        connection.close();
    }

    public void imprime_data(JSONObject obj) throws JSONException{
        Log.i("Rabbbit data",""+obj.getString("response_id"));
    }

    public void imprime_error(JSONObject obj) throws JSONException{
        Log.i("Rabbbit data",""+obj.getString("response_id"));
    }
    
}
