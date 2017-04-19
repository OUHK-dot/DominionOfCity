package dot.dominionofcity;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

public class Chatroom extends Messager {
//    private Messager messager;
    private Context context;
    private ChatroomView chatroomView;
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String TAG = "Chatroom";

    private Beeper beeper;

    Chatroom(Context context, String url, final ChatroomView chatroomView)
            throws MalformedURLException {
        super(context, url);
        Log.i(TAG, "Set up");
        this.context = context;
        this.chatroomView = chatroomView;
//        this.messager = new Messager(context, url);
//        messager.setReceiveListener(new Messager.ReceiveListener() {
        setReceiveListener(new Messager.ReceiveListener() {
            @Override
            public void onReceived(List<JsonNode> messages) throws IOException {
                for (JsonNode message : messages) {
                    MessageRelationPair pair =
                            mapper.treeToValue(message, MessageRelationPair.class);
                    Chatroom.this.read(pair);
                }
            }
        });
        chatroomView.setSubmitListerner(new View.OnClickListener() {
            private Message message = new Message(null, null, null);
            @Override
            public void onClick(View view) {
                message.setContent(chatroomView.getMessage());
                message.setMode(chatroomView.getMode());
                if (message.getMode().equals(Mode.PERSON)) {
                    message.setReceiver(chatroomView.getReceiver());
                }
                try {
//                    messager.enter(mapper.writeValueAsString(message));
                    enter(mapper.writeValueAsString(message));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        });

        beeper = new Beeper(context);
    }

    public void setBeeperOn(boolean on) {
        beeper.setOn(on);
    }

    private void read(final MessageRelationPair messageRelationPair) {
        chatroomView.post(new Runnable() {
            @Override
            public void run() {
                MessageView messageView = new MessageView(context)
                        .initMessage(messageRelationPair.getMessage(),
                                messageRelationPair.getRelation());
                chatroomView.addMessage(messageView);
            }
        });

        if (beeper.isOn()) {
            beeper.beep(messageRelationPair.getMessage());
        }
    }

//    Chatroom on() throws MalformedURLException, InterruptedException {
//        messager.on();
//        return this;
//    }
//
//    Chatroom off() throws InterruptedException {
//        messager.off();
//        return this;
//    }

}

@JsonIgnoreProperties(ignoreUnknown=true)
class MessageRelationPair {
    private Message message;
    private Relation relation;

    MessageRelationPair() {}

    MessageRelationPair(Message message, Relation relation) {
        this.message = message;
        this.relation = relation;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public Relation getRelation() {
        return relation;
    }

    public void setRelation(Relation relation) {
        this.relation = relation;
    }
}