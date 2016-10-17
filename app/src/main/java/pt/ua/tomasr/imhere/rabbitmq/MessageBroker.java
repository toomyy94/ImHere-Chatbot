/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.ua.tomasr.imhere.rabbitmq;

import android.util.Log;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageBroker {
    private final static String QUEUE_NAME = "login";
    private Channel channel;
    private Connection connection;


    public void connect(){
        try {
            ConnectionFactory factory = new ConnectionFactory();
            //factory.setHost("x.x.x.x.servidordo.jorge");
            factory.setHost("localhost");
            connection = factory.newConnection();
            channel = connection.createChannel();  
            
        } catch (IOException ex) {
            Logger.getLogger(MessageBroker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void createQueue(String queue_name) throws IOException{
        channel.queueDeclare(queue_name, false, false, false, null);        
    }
    
    public void publish(String queue_name, String message) throws IOException{
        channel.basicPublish("", queue_name, null, message.getBytes());
        Log.i("teste1: "," [x] Sent '" + message + "'");
    }
    
    public void consume(String queue_name) throws IOException{
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                throws IOException {
              String message = new String(body, "UTF-8");
              Log.i("teste2: "," [x] Received '" + message + "'");
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
    
}


