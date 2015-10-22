package ch.ethz.inf.vs.a3.vsjgigerchat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.UUID;

import ch.ethz.inf.vs.a3.vsjgigerchat.message.MessageTypes;

public class MainActivity extends ActionBarActivity {

    private Intent myIntent; // used to either jump to Settings, or ChatActivity
    private SharedPreferences sharedPreferences; // used to store Settings
    private EditText usernameEdit;

    public static final String SETTINGS_UPDATE = "Settings_need_update";
    public static final String ADDRESS_IDENTIFIER = "Transmitted_Address";
    public static final String PORT_IDENTIFIER = "Transmitted_Port";
    public static final String USERNAME_IDENTIFIER = "Username_Identifier";
    public static final String SETTINGS_FILE_NAME = "VSjgigerChatSettings";
    public static final String USERNAME_DEFAULT = null;
    public static final String ADDRESS_DEFAULT = null;
    public static final String PORT_DEFAULT = "4446"; // todo: check if we really should implement this as the default value...
    public final int timeout=5000;
    public final String ACK_USERNAME = "server";
    public final String ACK_TYPE = "ack";
    public final String UUID_IDENTIFIER = "Uuid_Identifier";
    public boolean registered;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("DEBUG: MainActivity, onCreate: started executing.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.app_name);
        myIntent = new Intent();
        sharedPreferences = getSharedPreferences(SETTINGS_FILE_NAME, Context.MODE_PRIVATE);
        usernameEdit = (EditText) findViewById(R.id.usernamePick);
        String username = getUsername();
        if(username!= null)
            usernameEdit.setText(username);
        else
            usernameEdit.setHint(R.string.username_hint);

        System.out.println("DEBUG: MainActivity, onCreate: username is: " + username + ". Address is: " + getAddress() + ". Port is: " + getPort() + ".");

    }

    @Override
    public void onResume(){
        super.onResume();
        System.out.println("DEBUG: MainActivity, onResume: username is: " + getUsername() + ". Address is: " + getAddress() + ". Port is: " + getPort() + ".");
    }

    private void setAddress(String newAddress){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ADDRESS_IDENTIFIER, newAddress);
        editor.apply();
        System.out.println("DEBUG: MainActivity, setAddress: theoretical new saved address: " + newAddress + ". Actually saved address: " + getAddress());
    }

    private void setPort(String newPort){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PORT_IDENTIFIER, newPort);
        editor.apply();
        System.out.println("DEBUG: MainActivity, setPort: theoretical new saved port: " + newPort + ". Actually saved port: " + getPort());
    }

    private void setUsername(String newUsername){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USERNAME_IDENTIFIER, newUsername);
        editor.apply();
        System.out.println("DEBUG: MainActivity, setUsername: theoretical new saved username: "+ newUsername+". Actually saved username: "+ getUsername());
    }

    private String getAddress(){
        String address = sharedPreferences.getString(ADDRESS_IDENTIFIER, ADDRESS_DEFAULT);
        System.out.println("DEBUG: MainActivity, getAddress: address: " + address);
        return address;
    }

    private String getPort(){
        String port = sharedPreferences.getString(PORT_IDENTIFIER, PORT_DEFAULT);
        System.out.println("DEBUG: MainActivity, getPort: port: " + port);
        return port;
    }

    private String getUsername(){
        String username = sharedPreferences.getString(USERNAME_IDENTIFIER, USERNAME_DEFAULT);
        System.out.println("DEBUG: MainActivity, getUsername: username: " + username);
        return username;
    }

    public void onJoinClick(View v){
        System.out.println("DEBUG: MainActivity, onJoinClick: started executing.");
        if(getUsername() != null || usernameEdit.getText().toString() != null) // if username was already set at some point
            setUsername(usernameEdit.getText().toString());

        myIntent = new Intent(this, ChatActivity.class);
        myIntent.putExtra(MainActivity.USERNAME_IDENTIFIER, getUsername());
        myIntent.putExtra(MainActivity.ADDRESS_IDENTIFIER, getAddress());
        myIntent.putExtra(MainActivity.PORT_IDENTIFIER, getPort());
        startActivity(myIntent);
    }

    public void onSettingsClick(View v){
        System.out.println("DEBUG: MainActivity, onSettingsClick: started executing.");

        if(getUsername() != null || usernameEdit.getText().toString() != null)
            setUsername(usernameEdit.getText().toString());

        myIntent = new Intent(this, SettingsActivity.class);
        startActivity(myIntent);
    }

    public void onDeregisterClick(View view) {
        AsyncWorker worker = new AsyncWorker();
        JSONObject deregister = registerMessage(false);
        worker.execute(deregister);
    }

    private class AsyncWorker extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            int ownPort= Integer.parseInt(getPort());
            DatagramSocket socket = null;
            try {
                socket = new DatagramSocket(ownPort);
                socket.setSoTimeout(timeout);
            } catch (SocketException e) {
                e.printStackTrace();
            }
            JSONObject packetToSend =(JSONObject) params[0];
            byte[] buffer= new byte[2024];
            try {
                buffer = packetToSend.toString().getBytes("UTF8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            //System.out.println(json.toString());
            try {
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }

            DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
            try {
                socket.receive(receivedPacket);
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(),"Registration wasn't successful... Please try again", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            String ack = new String(receivedPacket.getData(), 0, receivedPacket.getLength());
            JSONObject jsonAck = null;
            try {
                jsonAck = new JSONObject(ack);
                String ackUsername = jsonAck.getString("username");
                String ackUuid = jsonAck.getString("uuid");
                String ackType = jsonAck.getString("type");
                if(ackUsername==ACK_USERNAME&&ackUuid==packetToSend.getString("uuid")&&ackType==ACK_TYPE) {
                    if (packetToSend.getString("type") == "register")
                        Toast.makeText(getApplicationContext(), "registered", Toast.LENGTH_SHORT).show();
                    if (packetToSend.getString("type") == "deregister") {
                        Toast.makeText(getApplicationContext(), "deregistered", Toast.LENGTH_SHORT).show();
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return ack;
        }

    }
    private JSONObject registerMessage(boolean register) {
        String username=sharedPreferences.getString(USERNAME_IDENTIFIER, null);
        String portString = sharedPreferences.getString(PORT_IDENTIFIER, null);
        int port =Integer.parseInt(portString);
        String ip = sharedPreferences.getString(ADDRESS_IDENTIFIER, null);
        String uuidString = sharedPreferences.getString(UUID_IDENTIFIER, null);
        if(uuidString==null){
            UUID uuid= UUID.randomUUID();
            uuidString= uuid.toString();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(UUID_IDENTIFIER, uuidString);
            editor.apply();
        }
        else{
            UUID uuid=UUID.fromString(uuidString);
        }
        JSONObject jsonHeader = new JSONObject();
        JSONObject packetToSend = new JSONObject();
        JSONObject jsonBody = new JSONObject();
        try {
            jsonHeader.put("username", username);
            jsonHeader.put("uuid", uuidString);
            jsonHeader.put("timestamp", "{}");
            if(register){
                jsonHeader.put("type", MessageTypes.REGISTER);
            }
            else{
                jsonHeader.put("type", MessageTypes.DEREGISTER);
            }

            packetToSend.put("header", jsonHeader);
            packetToSend.put("body",jsonBody );


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return packetToSend;
    }
}

