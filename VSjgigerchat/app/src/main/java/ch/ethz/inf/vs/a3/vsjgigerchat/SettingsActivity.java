package ch.ethz.inf.vs.a3.vsjgigerchat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Andreas on 19.10.2015.
 */
public class SettingsActivity extends ActionBarActivity {

    private String port, address;
    private EditText addressEdit, portEdit;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("DEBUG: SettingsActivity, onCreate: started executing.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(R.string.settings_activity_title);
        addressEdit = (EditText) findViewById(R.id.address_picker);
        portEdit = (EditText) findViewById(R.id.port_picker);

        sharedPreferences = getSharedPreferences(MainActivity.SETTINGS_FILE_NAME, Context.MODE_PRIVATE);

        address = sharedPreferences.getString(MainActivity.ADDRESS_IDENTIFIER, MainActivity.ADDRESS_DEFAULT);
        port = sharedPreferences.getString(MainActivity.PORT_IDENTIFIER, MainActivity.PORT_DEFAULT);

        System.out.println("DEBUG: SettingsActivity, onCreate: read address: " +address+". read port: "+port+".");


        if(address!= MainActivity.ADDRESS_DEFAULT)
            addressEdit.setText(address);
        else
            addressEdit.setHint(R.string.address_hint);

        if(port != MainActivity.PORT_DEFAULT)
            portEdit.setText(port);
        else
            portEdit.setHint(R.string.port_hint);
    }

    public void onSaveClick(View v){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String nport, naddr;
        naddr = addressEdit.getText().toString();
        if(naddr != "");
            editor.putString(MainActivity.ADDRESS_IDENTIFIER, naddr);
        nport = portEdit.getText().toString();
        if(nport != "")
            editor.putString(MainActivity.PORT_IDENTIFIER, nport);
        editor.apply();
        nport = sharedPreferences.getString(MainActivity.PORT_IDENTIFIER,MainActivity.PORT_DEFAULT);
        naddr = sharedPreferences.getString(MainActivity.ADDRESS_IDENTIFIER, MainActivity.ADDRESS_DEFAULT);
        Toast.makeText(this,"Saved Port to: "+nport+". Address to: "+naddr, Toast.LENGTH_LONG).show();

        System.out.println("DEBUG: SettingsActivity, onSaveClick executed. newly saved port: " + nport+". newly saved address: "+naddr+".");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

}
