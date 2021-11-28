package com.mkuskowski.mapapp;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;


public class RabbitMQ_Client {

    private final static String QUEUE_NAME = "MKuskowskiChannel";
    private final static String EXCHANGE_NAME = "GoogleMaps";
    private final static String ROUTING_KEY = "GoogleMapsKey";
    private String url;

    private Thread subscribeThread;
    private Thread publishThread;

    private MapsActivity maps;

    // Constructor , getting needed data to get it work properly
    //-----------------------------------------------------------------------------------------------------
    public RabbitMQ_Client(MapsActivity maps , String url)
    {
        // Url get from our main activity which is giving us url from string.xml
        //---------------------------------------------------------------------
        this.url = url;
        this.maps = maps;
        setupConnectionFactory();
        // Methods to launch threads which will work in background
        //---------------------------------------------------------------------
        publishToAMQP();
        subscribe();
        Log.println(Log.INFO,"@@@DEMOAPPLOG","Created RabbitMQ Client");
    }

    // Its kind of destructor , needed to end threats which we started
    //-----------------------------------------------------------------------------------------------------
    @Override
    protected void finalize() throws Throwable
    {
        publishThread.interrupt();
        subscribeThread.interrupt();
        super.finalize();
    }

    // Create an internal message queue. In this case is a BlockingDeque used.
    // Blockingqueues implementations are designed to be used primarily for producer-consumer queues.
    //-----------------------------------------------------------------------------------------------------
    private BlockingDeque queue = new LinkedBlockingDeque();

    public void SendMessage(String message)
    {
        try
        {
            queue.putLast(message);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    // Create a setup function for the ConnectionFactory The connection factory encapsulates a set of connection configuration parameters,
    // in this case the CLOUDAMQP_URL. The URL can be found in the control panel for your instance.
    //-----------------------------------------------------------------------------------------------------
    private ConnectionFactory factory = new ConnectionFactory();
    private void setupConnectionFactory()
    {
        try
        {
            factory.setAutomaticRecoveryEnabled(false);
            factory.setUri(url);
        }
        catch (KeyManagementException | NoSuchAlgorithmException | URISyntaxException e1)
        {
            e1.printStackTrace();
        }
    }
    //-----------------------------------------------------------------------------------------------------
    // Publisher,
    // Create a publisher that publish messages from the internal queue. Messages are added back to the queue if an exception is catched.
    // The publisher will try to reconnect every 5 seconds if the connection is broken.
    //-----------------------------------------------------------------------------------------------------
    // A thread ("background" or "worker" threads or use of the AsyncTask class) is needed when we have operations to perform that are not instantaneous,
    // such as network access when connecting to rabbitMQ.
    //-----------------------------------------------------------------------------------------------------
    public void publishToAMQP()
    {
        //Log.println(Log.INFO,"@@@DEMOAPPLOG","Trying to run PublishToAMPQP");
        publishThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true)
                {
                    try
                    {
                        // Establishing connection and creating/declaring queue which we want use
                        //---------------------------------------------------------------------
                        Connection connection = factory.newConnection();
                        Channel ch = connection.createChannel();
                        ch.queueDeclare(QUEUE_NAME, false, false, false, null);
                        // Connecting our que to exchenge
                        //---------------------------------------------------------------------
                        ch.queueBind(QUEUE_NAME,EXCHANGE_NAME,ROUTING_KEY);

                        Log.println(Log.INFO,"@@@DEMOAPPLOG","Trying to send msg on " + QUEUE_NAME +" Channel.");
                        // Getting our message from queue
                        //---------------------------------------------------------------------
                        String message = (String) queue.takeFirst();
                        if(message != null)
                        {
                            try
                            {
                                //Putting message in queue
                                ch.basicPublish(EXCHANGE_NAME, ROUTING_KEY, null, message.getBytes());
                                Log.println(Log.INFO,"@@@DEMOAPPLOG","Sended msg on " + QUEUE_NAME +" Channel with is " + message);
                                //ch.close();
                                //connection.close();
                            }
                            catch (Exception e)
                            {
                                // If we didn't manage to send it just put it back and try send it again
                                //---------------------------------------------------------------------
                                Log.d("","[f] " + message);
                                queue.putFirst(message);
                                throw e;
                            }
                        }

                    }
                    catch (InterruptedException e)
                    {
                        break;
                    }
                    catch (Exception e)
                    {
                        Log.d("", "Connection broken: " + e.getClass().getName());
                        try
                        {
                            // Try again later if we get disconnected
                            //---------------------------------------------------
                            Thread.sleep(5000);
                        }
                        catch (InterruptedException e1)
                        {
                            break;
                        }
                    }
                }

            }
        });
        publishThread.start();
    }
    //-----------------------------------------------------------------------------------------------------
    // Subscriber,
    // someone who is going to receive message
    //-----------------------------------------------------------------------------------------------------

    private void newLocation(byte[] body)
    {
        // From bytes to string conversion
        //---------------------------------------------------------------------
        String newMessage = new String(body, StandardCharsets.UTF_8);
        Log.println(Log.INFO,"@@@DEMOAPPLOG","NEW LOCATION RECIVED!!!! " + newMessage);
        // Giving that what we get to our Hashmap -> Google maps
        //---------------------------------------------------------------------
        maps.PushToStack(newMessage);
    }

    private void subscribe()
    {
        //Log.println(Log.INFO,"@@@DEMOAPPLOG","Trying to run Subscribe");
        subscribeThread = new Thread(new Runnable() {
            @Override
            public void run()
            {
                while(true)
                {
                    try
                    {
                        Connection connection = factory.newConnection();
                        Channel ch = connection.createChannel();
                        ch.queueDeclare(QUEUE_NAME,false,false,false,null);

                        // Connecting our que to exchenge
                        //---------------------------------------------------------------------
                        ch.queueBind(QUEUE_NAME,EXCHANGE_NAME,ROUTING_KEY);

                        Log.println(Log.INFO,"@@@DEMOAPPLOG","Looking for message for me");
                        // Creating our customer , someone who will get that message
                        //---------------------------------------------------------------------
                        MyConsumer consumer = new MyConsumer(ch);
                        // There we are getting message from queue and giving it to our customer
                        //---------------------------------------------------------------------
                        String consumerTag = ch.basicConsume(QUEUE_NAME, true, consumer);
                        // Closing connection
                        //---------------------------------------------------------------------
                        ch.basicCancel(consumerTag);
                        ch.close();
                        connection.close();
                    }
                    catch (Exception e1)
                    {
                        Log.d("", "Connection broken: " + e1.getClass().getName());
                        try
                        {
                            // Try again later if we get disconnected for example
                            //---------------------------------------------------
                            Thread.sleep(5000);
                        }
                        catch (InterruptedException e)
                        {
                            break;
                        }
                    }
                }
            }
        });
        subscribeThread.start();
    }
    // Our class ( client ) who will get
    //-----------------------------------------------------------------------------------------------------
    private class MyConsumer extends DefaultConsumer
    {
        Channel channel;
        public MyConsumer(Channel channel)
        {
            super(channel);
            this.channel = channel;
        }

        // Override method which is ran when our client get message.
        // There we can implement that what we want to do when we get that message
        //-----------------------------------------------------------------------------------------------------
        @Override
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException
        {
            //String routingKey = envelope.getRoutingKey();
            //String contentType = properties.getContentType();
            long deliveryTag = envelope.getDeliveryTag();
            Log.println(Log.INFO,"@@@DEMOAPPLOG","WE GOT MESSAGE");
            newLocation(body);
            channel.basicAck(deliveryTag, true);
        }

    }
}
