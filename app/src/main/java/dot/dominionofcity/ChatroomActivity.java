package dot.dominionofcity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.EditText;
import android.widget.ToggleButton;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class ChatroomActivity extends AppCompatActivity {
    private Chatroom chatroom;
//    private ChatroomView chatroomView;
//    private Message outMessage;
    private static final String url = "http://come2jp.com/dominion";
    private static final String SPNAME = "STH";
//    private User me;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        //chatroomView = (ChatroomView) findViewById(R.id.chatroom);
//        me = new User(2, "Dixon");
        try {
//            chatroom = new Chatroom(me, url, new Handler(),
//                    getSharedPreferences(SPNAME, MODE_PRIVATE)) {
//                @Override
//                void read(Message message) {
////                    TextView messageView = new TextView(ChatroomActivity.this);
////                    messageView.setText(message.toString());
//                    MessageView messageView = new MessageView(
//                            ChatroomActivity.this
//                    ).initMessage(message);
//                    chatroomView.addMessage(messageView);
//                }
//            }.with(this, chatroomView);
            chatroom = new Chatroom(this, url,
                    (ChatroomView) findViewById(R.id.chatroom),
                    getSharedPreferences(SPNAME, MODE_PRIVATE));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void session(View view) {
        final int uid = Integer.parseInt(
                ((EditText) findViewById(R.id.uid)).getText().toString()
        );
        String name = ((EditText) findViewById(R.id.chatroom_notification_sender_name)).getText().toString();
        final int rid = Integer.parseInt(
                ((EditText) findViewById(R.id.rid)).getText().toString()
        );
        User me = new User(uid, name);
        chatroom.setMe(me);
//        outMessage.setSender(me);
        Thread netThread = new Thread() {
            @Override
            public void run() {
                try {
                    ConnectionHandler conn = new ConnectionHandler(new URL(url + "/trySession.php"))
                            .useSession(getSharedPreferences(SPNAME, MODE_PRIVATE));
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

//    public void start(View view) {
//        //        chatroomView = (ChatroomView) findViewById(R.id.chatroom);
////        try {
////            chatroom = new Chatroom(url, new Handler(),
////                    getSharedPreferences(SPNAME, MODE_PRIVATE)) {
////                @Override
////                void read(Message message) {
////                    TextView messageView = new TextView(MainActivity.this);
////                    messageView.setText(message.toString());
////                    chatroomView.addMessage(messageView);
////                }
////            }.on();
////            message = new Message();
////            chatroomView.getSend().setOnClickListener(new View.OnClickListener() {
////                @Override
////                public void onClick(View view) {
////                    message.setContent(chatroomView.getMessage());
////                    chatroom.enter(message);
////                }
////            });
////        } catch (MalformedURLException | InterruptedException e) {
////            e.printStackTrace();
////        }
//
//        ObjectMapper mapper = new ObjectMapper();
//        Message m = new Message(new User(10000, "dixon"), "fuck you", Mode.PERSON, new User(20000, "Jesus"));
//        m.setSendTime(new Date());
//        String j = null;
//        try {
//            j = mapper.writeValueAsString(m);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//        Log.i("JSON", j);
//
//        //test
//        final String finalJ = "messages=[" + j + "]";
//        Thread netThread = new Thread() {
//            @Override
//            public void run() {
//                try {
//                    ConnectionHandler conn = new ConnectionHandler(new URL(url + "/writeMessage.php"))
//                            .useSession(getSharedPreferences(SPNAME, MODE_PRIVATE));
//                    Log.i("NET", conn.post(finalJ));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//        netThread.start();
//    }
//
//    public void receive(View view) {
//        ConnectionHandler conn = null;
//        try {
//            conn = new ConnectionHandler(new URL(url + "/readMessage.php"))
//                    .useSession(getSharedPreferences(SPNAME, MODE_PRIVATE));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        Chatroom.Receiver.ReceivePacket packet = mapper.readValue(
//                conn.get("start=" + readNo + 1),
//                Chatroom.Receiver.ReceivePacket.class
//        );
//        if (null == packet || !packet.status.equals(Status.OK)) return;
//        for (Message message : packet.messages) {
//            reader.setMessage(message);
//            handler.post(reader);
//        }
//        readNo = packet.readNo;
//    }

//    public void noti(View view) {
//        int notifyID = 1;
//
//        Notification.InboxStyle inboxStyle = new Notification.InboxStyle();
//        String[] lines = new String[] { "magiclen.org 發了一篇新文章", "今天天氣是晴天", "sthklsalnfk", "skrsldfknskslrs", "dkfnringnlfinh" };
//        inboxStyle.setBigContentTitle("Dominion of City Chatroom");
//        inboxStyle.setSummaryText("more message");
//        for (int i = 0; i < lines.length; i++) {
//            inboxStyle.addLine(String.format(Locale.getDefault(), "%d: %s", i + 1, lines[i]));
//        }
//
//        // Creates an Intent for the Activity
//        Intent notifyIntent = new Intent(ChatroomActivity.this, ChatroomActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
////        TaskStackBuilder stackBuilder = TaskStackBuilder.create(ChatroomActivity.this);
////        // Adds the back stack
////        stackBuilder.addParentStack(ChatroomActivity.class);
////        // Adds the Intent to the top of the stack
////        stackBuilder.addNextIntent(notifyIntent);
////        // Gets a PendingIntent containing the entire back stack
////        PendingIntent notifyPendingIntent =
////                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        PendingIntent pendingIntent = PendingIntent.getActivity(
//                ChatroomActivity.this, 0,
//                notifyIntent,
//                PendingIntent.FLAG_CANCEL_CURRENT
//        );
//
//        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        Notification.Builder builder = new Notification.Builder(this)
//                .setSmallIcon(R.color.blue)
//                .setContentTitle("Dominion of City Chatroom")
//                .setContentText("New message")
//                .setStyle(inboxStyle)
//                .setDefaults(Notification.DEFAULT_VIBRATE)
//                //.setContentIntent(pendingIntent)
//                .setContentIntent(pendingIntent)
//                ;//.setAutoCancel(true);
//        notificationManager.beep(notifyID, builder.build()); //send notification
//    }

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
