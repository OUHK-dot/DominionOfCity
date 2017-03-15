package dot.dominionofcity;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.TextView;

import java.net.MalformedURLException;

public class MainActivity extends AppCompatActivity {
    private Chatroom chatroom;
    private ChatroomView chatroomView;
    private Message message;
    private static final String url = "http://come2jp.com/dominion";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chatroomView = (ChatroomView) findViewById(R.id.chatroom);
        try {
            chatroom = new Chatroom(url, new Handler()) {
                @Override
                void read(Message message) {
                    TextView messageView = new TextView(MainActivity.this);
                    messageView.setText(message.toString());
                    chatroomView.addMessage(messageView);
                }
            };
            message = new Message();
            chatroomView.getSend().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    message.setContent(chatroomView.getMessage());
                    chatroom.enter(message);
                }
            });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
