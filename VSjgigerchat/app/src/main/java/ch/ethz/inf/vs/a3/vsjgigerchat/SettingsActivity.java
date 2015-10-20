package ch.ethz.inf.vs.a3.vsjgigerchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

/**
 * Created by Andreas on 19.10.2015.
 */
public class SettingsActivity extends ActionBarActivity {

    private Intent myIntent;
    private String port, address;
    private EditText addressEdit, portEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("DEBUG: SettingsActivity, onCreate: started executing.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(R.string.settings_activity_title);
        addressEdit = (EditText) findViewById(R.id.address_picker);
        portEdit = (EditText) findViewById(R.id.port_picker);

        myIntent = getIntent();
        String address = myIntent.getStringExtra(MainActivity.ADDRESS_IDENTIFIER);
        String port = myIntent.getStringExtra(MainActivity.PORT_IDENTIFIER);

        if(address!= null)
            addressEdit.setText(address);
        else
            addressEdit.setHint(R.string.address_hint);

        if(port!=null)
            portEdit.setText(port);
        else
            portEdit.setHint(R.string.port_hint);
        // todo: read the delivered values from myIntent. display editText's with those values
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        address = addressEdit.getText().toString();
        port = portEdit.getText().toString();
        myIntent = new Intent(this, MainActivity.class);

        myIntent.putExtra(MainActivity.ADDRESS_IDENTIFIER, address);
        myIntent.putExtra(MainActivity.PORT_IDENTIFIER, port);
        startActivity(myIntent);
        // todo: send the current values from the text edits back to main activity, using myIntent
    }

}
