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

        // todo: read the delivered values from myIntent. display editText's with those values
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        // todo: send the current values from the text edits back to main activity, using myIntent
    }

}
