package dot.dominionofcity;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import java.net.MalformedURLException;

//public class ChatroomImplementation {
//    private Context context;
//    private Chatroom chatroom;
//    private ChatroomView chatroomView;
//
//    public ChatroomImplementation() {}
//
//    public ChatroomImplementation(Context context,
//                                  String url,
//                                  SharedPreferences sharedPref,
//                                  ChatroomView chatroomView) {
//        this.context = context;
//        chatroom = new Chatroom(url, sharedPref);
//        this.chatroomView = chatroomView;
//    }
//
//    public ChatroomImplementation init() {
//        chatroomView.setSubmitListerner(new View.OnClickListener() {
//            private Message message = new Message(null, null, null);
//            @Override
//            public void onClick(View view) {
//                message.setContent(chatroomView.getMessage());
//                message.setMode(chatroomView.getMode());
//                if (message.getMode().equals(Mode.PERSON)) {
//                    message.setReceiver(chatroomView.getReceiver());
//                }
//                chatroom.enter(message);
//            }
//        });
//        return this;
//    }
//
//    public ChatroomImplementation start()
//            throws MalformedURLException, InterruptedException {
//        chatroom.on();
//        return this;
//    }
//
//    public ChatroomImplementation stop() throws InterruptedException {
//        chatroom.off();
//        return this;
//    }
//}
