package ch.ethz.inf.vs.a3.vsjgigerchat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

    private Intent myIntent; // used to either jump to Settings, or ChatActivity
    private SharedPreferences sharedPreferences; // used to store Settings
    private EditText usernameEdit;

    public final String SETTINGS_UPDATE = "Settings_need_update";
    public final String ADDRESS_IDENTIFIER = "Transmitted_Address";
    public final String PORT_IDENTIFIER = "Transmitted_Port";
    public final String USERNAME_IDENTIFIER = "Username_Identifier";
    public final String SETTINGS_FILE_NAME = "VSjgigerChatSettings";

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


        System.out.println("DEBUG: MainActivity, onCreate: username is "+ username);



        // todo: initialise/setup everything: sharedPrefs, editText, ...

    }

    private void setAddress(String newAddress){
        // todo
    }

    private void setPort(String newPort){
        // todo
    }

    private void setUsername(String newUsername){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USERNAME_IDENTIFIER, newUsername);
        editor.apply();
        System.out.println("DEBUG: MainActivity, setUsername: theoretical new saved username: "+ newUsername+". Actually saved username: "+ getUsername());
        // todo
    }

    private String getAddress(){
        // todo
        return null;
    }

    private String getPort(){
        // todo
        return null;
    }

    private String getUsername(){
        String username = sharedPreferences.getString(USERNAME_IDENTIFIER, null);
        System.out.println("DEBUG: MainActivity, getUsername: username: " + username);
        return username;
    }

    public void onJoinClick(View v){
        System.out.println("DEBUG: MainActivity, onJoinClick: started executing.");
        System.out.println("DEBUG: MainActivity, onJoinClick: getUsername: " + getUsername());
        System.out.println("DEBUG: MainActivity, onJoinClick: Edit.getText: " + usernameEdit.getText().toString());
        if(getUsername() != null || usernameEdit.getText().toString() != null)
            setUsername(usernameEdit.getText().toString());

        myIntent = new Intent(this, ChatActivity.class);
        // todo: pass all the needed Infos that are saved in sharedPrefs (addr + port + username).
        startActivity(myIntent);
    }

    public void onSettingsClick(View v){
        // todo: save username to sharedprefs


        System.out.println("DEBUG: MainActivity, onSettingsClick: started executing.");
        myIntent = new Intent(this, SettingsActivity.class);
        // todo: change to Settings Activity, pass all the saved Settings with myIntent(addr + port), and use those there to display current Settings
        startActivity(myIntent);
    }
    
    private class Udp extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            String username=sharedPreferences.getString(USERNAME_IDENTIFIER, null);
            String portString = sharedPreferences.getString(PORT_IDENTIFIER, null);
            int port =Integer.parseInt(portString);
            int ownPort= Integer.parseInt(getPort());
            String ip = sharedPreferences.getString(ADDRESS_IDENTIFIER, null);
            DatagramSocket socket = null;
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


            try {
                socket = new DatagramSocket(ownPort);
            } catch (SocketException e) {
                e.printStackTrace();
            }
            try {
                socket.setSoTimeout(timeout);
            } catch (SocketException e) {
                e.printStackTrace();
            }
            /*JSONObject jsonHeader = new JSONObject();
            JSONObject packetToSend = new JSONObject();
            JSONObject jsonBody = new JSONObject();
            try {
                jsonHeader.put("username", username);
                jsonHeader.put("uuid", uuidString);
                jsonHeader.put("timestamp", "{}");
                jsonHeader.put("type", MessageTypes.REGISTER);
                packetToSend.put("header", jsonHeader);
                packetToSend.put("body",jsonBody );


            } catch (JSONException e) {
                e.printStackTrace();
            }*/
            byte[] buffer= new byte[256];
            DatagramPacket packetToSend = new DatagramPacket(buffer, buffer.length);
            //System.out.println(json.toString());
            try {
                socket.send(packetToSend);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //socket.receive(getack);

            return null;
            //todo return statements
        }

    }

}
