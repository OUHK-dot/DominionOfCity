package dot.dominionofcity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private static final String url = "http://come2jp.com/dominion";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void session(View view) {
        Thread netThread = new Thread() {
            @Override
            public void run() {
                try {
                    ConnectionHandler conn = new ConnectionHandler(new URL(url + "/trySession.php"))
                            .useSession(MainActivity.this);
                    conn.post("");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        netThread.start();
    }

    public void goToChatroom(View view) {
        Intent intent = new Intent(this, ChatroomActivity.class);
        startActivity(intent);
    }
}
