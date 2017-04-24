package dot.dominionofcity.chatroom;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ToggleButton;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import dot.dominionofcity.ConnectionHandler;
import dot.dominionofcity.R;

public class ChatroomActivity extends AppCompatActivity {
    private Chatroom chatroom;
    private static final String url = "http://come2jp.com/dominion";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        try {
            chatroom = new Chatroom(this, url,
                    (ChatroomView) findViewById(R.id.chatroom));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void session(View view) {
        final int uid = Integer.parseInt(
                ((EditText) findViewById(R.id.uid)).getText().toString()
        );
        final int rid = Integer.parseInt(
                ((EditText) findViewById(R.id.rid)).getText().toString()
        );
        Thread netThread = new Thread() {
            @Override
            public void run() {
                try {
                    ConnectionHandler conn = new ConnectionHandler(new URL(url + "/trySession.php"))
                            .useSession(ChatroomActivity.this);
                    conn.post("uid=" + uid + "&rid=" + rid);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        netThread.start();
    }

    public void toggle(View view) throws MalformedURLException, InterruptedException {
        if (((ToggleButton) view).isChecked())
            chatroom.on();
        else chatroom.off();
    }

    @Override
    protected void onPause() {
        super.onPause();
        chatroom.setBeeperOn(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        chatroom.setBeeperOn(false);
    }
}
