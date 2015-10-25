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
import org.json.simple.JSONValue;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedHashMap;
import java.util.Map;
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
    public final String ACK_TYPE = "ack";
    public final String UUID_IDENTIFIER = "Uuid_Identifier";
    public boolean registering = true;
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
		AsyncWorker worker = new AsyncWorker();
		String register = registerMessage(true);
        byte[] message = register.getBytes();
        registering=true;
		worker.execute(message);
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
        if(registered==true){
            registering=false;
			AsyncWorker worker = new AsyncWorker();
			String deregister = registerMessage(false);
            byte[] message = deregister.getBytes();
			worker.execute(message);
		}
		else{
			Toast.makeText(getApplicationContext(), "You aren't registered", Toast.LENGTH_SHORT).show();
		}
    }

    
class AsyncWorker extends AsyncTask <byte[],Void, Boolean> {


        protected Boolean doInBackground(byte[]... params) {
            String portString = sharedPreferences.getString(PORT_IDENTIFIER, PORT_DEFAULT);
            System.out.println("DEBUG: MainActivity, AsyncWorker: port "+portString);
            int port =Integer.parseInt(portString);
            InetAddress ip = null;
            DatagramSocket socket;
            DatagramPacket packet=null;
            try {
                String ipString=sharedPreferences.getString(ADDRESS_IDENTIFIER, ADDRESS_DEFAULT);
                ip =InetAddress.getByName(ipString);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            try {
                socket = new DatagramSocket();
                try{
                    socket.setSoTimeout(NetworkConsts.SOCKET_TIMEOUT);
                    socket.connect(ip, port);
                    byte[] packetToSend = params[0];
                    if(packetToSend!=null){
                        packet = new DatagramPacket(packetToSend, packetToSend.length, ip, port);
                        socket.send(packet);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                byte[] buffer = new byte[1024];
                DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
                try {
                    socket.receive(receivedPacket);
                    String ack= new String(receivedPacket.getData(), 0 , receivedPacket.getLength());
                    System.out.println("DEBUG: MainActivity, AsyncWorker: packet received " + ack);
                    JSONObject jsonAck = null;
                    JSONObject jsonheader = null;
                    try {
                        jsonAck = new JSONObject(ack);
                        jsonheader=new JSONObject(jsonAck.get("header").toString());
                        String ackType =jsonheader.getString("type");
                        if(ackType.equals(ACK_TYPE)) {
                            if (registering){
                                System.out.println("DEBUG: MainActivity, AsyncWorker: registered");
                                registered=true;}
                            else{
                                System.out.println("DEBUG: MainActivity, AsyncWorker: deregistered");
                                registered=false;
                            }
                        }
                        else{
                            System.out.println("DEBUG: MainActivity, AsyncWorker: Error");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    System.out.println("DEBUG: MainActivity, AsyncWorker: registration wasn't successful");
                    e.printStackTrace();
                }

            } catch (SocketException e) {
                e.printStackTrace();
            }
            return true;
        }

    }

    private JSONObject registerMessage(boolean register) {
        System.out.println("DEBUG: MainActivity, registerMessage: start");
        String username=sharedPreferences.getString(USERNAME_IDENTIFIER, null);
        String portString = sharedPreferences.getString(PORT_IDENTIFIER, PORT_DEFAULT);
        int port =Integer.parseInt(portString);
        String ip = sharedPreferences.getString(ADDRESS_IDENTIFIER, ADDRESS_DEFAULT);
        String uuidString = sharedPreferences.getString(UUID_IDENTIFIER, null);
        String type=null;
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
        System.out.println("DEBUG: MainActivity, registerMessage: new uuid "+uuidString);
        if(register){
            type=MessageTypes.REGISTER;
        }
        else{
            type=MessageTypes.DEREGISTER;
        }
        JSONObject obj = null;
            Map map = new LinkedHashMap();
            Map headerMap = new LinkedHashMap();
            headerMap.put("username", username);
            headerMap.put("uuid", uuidString);
            headerMap.put("timestamp", "{}");
            if(register){
                headerMap.put("type", MessageTypes.REGISTER);
            }
            else{
                headerMap.put("type", MessageTypes.DEREGISTER);
            }
            JSONObject jsonBody = new JSONObject();
            map.put("header", headerMap);
            map.put("body", jsonBody);
            String jsonText = JSONValue.toJSONString(map);
            System.out.println("DEBUG: MainActivity, registerMessage: json: " +jsonText);
            return jsonText;
    }
}

