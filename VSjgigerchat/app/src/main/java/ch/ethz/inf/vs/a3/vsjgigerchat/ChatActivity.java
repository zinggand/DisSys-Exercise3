package ch.ethz.inf.vs.a3.vsjgigerchat;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by Andreas on 19.10.2015.
 */
public class ChatActivity extends ActionBarActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("DEBUG: ChatActivity, onCreate: started executing.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        TextView tv = (TextView) findViewById(R.id.chat_activity_text_view);
        tv.setText("Bla bla bla... do something in this activity");

        // todo: start the magic :P connect to the server, and display what ever we need to display
    }

    @Override
    public void onDestroy(){
        System.out.println("DEBUG: ChatActivity, onDestroy: finishing up, and closing Activity");
        super.onDestroy();
        // todo: potentially deregister from server in here
    }

}
