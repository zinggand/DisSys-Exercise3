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
    private EditText addressEdit;

    public static final String SETTINGS_UPDATE = "Settings_need_update";
    public static final String ADDRESS_IDENTIFIER = "Transmitted_Address";
    public static final String PORT_IDENTIFIER = "Transmitted_Port";
    public static final String USERNAME_IDENTIFIER = "Username_Identifier";
    public static final String SETTINGS_FILE_NAME = "VSjgigerChatSettings";

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

    @Override public void onResume(){
        super.onResume();
        myIntent = getIntent();
        String address = myIntent.getStringExtra(ADDRESS_IDENTIFIER);
        String port = myIntent.getStringExtra(PORT_IDENTIFIER);
        setAddress(address);
        setPort(port);
    }

    private void setAddress(String newAddress){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ADDRESS_IDENTIFIER, newAddress);
        editor.apply();
        // todo
    }

    private void setPort(String newPort){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PORT_IDENTIFIER, newPort);
        editor.apply();
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
        String address = sharedPreferences.getString(ADDRESS_IDENTIFIER, null);
        // todo
        return null;
    }

    private String getPort(){
        String port = sharedPreferences.getString(PORT_IDENTIFIER, "4446");
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
        myIntent.putExtra(ADDRESS_IDENTIFIER, getAddress());
        myIntent.putExtra(PORT_IDENTIFIER, getPort());
        myIntent.putExtra(USERNAME_IDENTIFIER, getUsername());
        // todo: pass all the needed Infos that are saved in sharedPrefs (addr + port + username).
        startActivity(myIntent);
    }

    public void onSettingsClick(View v){
        if(getUsername() != null || usernameEdit.getText().toString() != null)
            setUsername(usernameEdit.getText().toString());
        // todo: save username to sharedprefs


        System.out.println("DEBUG: MainActivity, onSettingsClick: started executing.");
        myIntent = new Intent(this, SettingsActivity.class);
        myIntent.putExtra(ADDRESS_IDENTIFIER, getAddress());
        myIntent.putExtra(PORT_IDENTIFIER, getPort());
        // todo: change to Settings Activity, pass all the saved Settings with myIntent(addr + port), and use those there to display current Settings
        startActivity(myIntent);
    }

}
