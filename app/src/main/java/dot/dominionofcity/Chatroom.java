package dot.dominionofcity;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

enum Status {OK, WARNING, ERROR}

public /*abstract*/ class Chatroom {
    private User me;
    private Sender sender;
    private final List<Message> outMessages;
    private Receiver receiver;
    private int receivedNo;
    private String url;
    //private Handler handler;
    private SharedPreferences sharedPref;
    private Context context;
    private ChatroomView chatroomView;
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String TAG = "Chatroom";

//    Chatroom(User me, String url, Handler handler) throws MalformedURLException {
//        this(me, url, handler, null);
//    }
//
//    Chatroom(User me, String url, Handler handler, SharedPreferences sf) throws MalformedURLException {
//        Log.i(TAG, "Set up");
//        this.me = me;
//        this.url = url;
//        this.handler = handler;
//        this.sf = sf;
//        sender = new Sender(url + "/writeMessage.php");
//        outMessages = new ArrayList<>();
//        receiver = new Receiver(url + "/readMessage.php", handler) {
//            @Override
//            void read(Message message) {
//                Chatroom.this.read(message);
//            }
//        };
//        receivedNo = 0;
//    }

    Chatroom(Context context, String url, ChatroomView chatroomView)
            throws MalformedURLException {
        this(context, url, chatroomView, null);
    }

    Chatroom(Context context, String url, final ChatroomView chatroomView,
             SharedPreferences sharedPref) throws MalformedURLException {
        Log.i(TAG, "Set up");
        this.context = context;
        this.url = url;
        this.chatroomView = chatroomView;
        this.sharedPref = sharedPref;
//        sender = new Sender(url + "/writeMessage.php");
        outMessages = new ArrayList<>();
//        receiver = new Receiver(url + "/readMessage.php") {
//            @Override
//            void read(Message message) {
//                Chatroom.this.read(message);
//            }
//        };
//        receiver = new Receiver(url + "/readMessage.php");
        receivedNo = 0;
        chatroomView.setSubmitListerner(new View.OnClickListener() {
            private Message message = new Message(null, null, null);
            @Override
            public void onClick(View view) {
                message.setContent(chatroomView.getMessage());
                message.setMode(chatroomView.getMode());
                if (message.getMode().equals(Mode.PERSON)) {
                    message.setReceiver(chatroomView.getReceiver());
                }
                enter(message);
            }
        });
    }

//    public Chatroom with(@Nullable Context context, final ChatroomView chatroomView) {
//        chatroomView.setSubmitListerner(new View.OnClickListener() {
//            private Message message = new Message(null, null, null);
//            @Override
//            public void onClick(View view) {
//                message.setContent(chatroomView.getMessage());
//                message.setMode(chatroomView.getMode());
//                if (message.getMode().equals(Mode.PERSON)) {
//                    message.setReceiver(chatroomView.getReceiver());
//                }
//                enter(message);
//            }
//        });
//        return this;
//    }

    public Chatroom setMe(User me) {
        this.me = me;
        return this;
    }

    //abstract void read(Message message);

//    private void read(final Message message) {
//        MessageView messageView = new MessageView(context
//        ).initMessage(message);
//        chatroomView.addMessage(messageView);
//    }

    private void read(final MessageRelationPair message) {
        chatroomView.post(new Runnable() {
            @Override
            public void run() {
                MessageView messageView = new MessageView(context
                ).initMessage(message.getMessage(), message.getRelation());
                chatroomView.addMessage(messageView);
            }
        });
    }

    public void enter(Message message) {
        Log.v(TAG, "Out-> " + message);
        sender.enter(message);
    }

    Chatroom on() throws MalformedURLException, InterruptedException {
        off();
        Log.i(TAG, "Log on");
        sender = new Sender(url + "/writeMessage.php");
        //receiver = new Receiver(url + "/readMessage.php", handler) {
//        receiver = new Receiver(url + "/readMessage.php") {
//            @Override
//            void read(Message message) {
//                Chatroom.this.read(message);
//            }
//        };
        receiver = new Receiver(url + "/readMessage.php");
        sender.start();
        receiver.start();
        return this;
    }

    Chatroom off() throws InterruptedException {
        Log.i(TAG, "Log off");
        if (null != sender && sender.getOnline()) {
            sender.offline();
            sender.join();
        }
        if (null != receiver && receiver.getOnline()) {
            receiver.offline();
            sender.join();
        }
        return this;
    }

    private class Sender extends Thread {
        private URL url;
        private Boolean online;

        Sender(String url) throws MalformedURLException {
            this.url = new URL(url);
            online = true;
        }

        void offline() {
            online = false;
        }

        boolean getOnline() {
            return online;
        }

        @Override
        public void run() {
            while (true) {
                synchronized (outMessages) {
                    while (outMessages.size() == 0) {
                        if (!online) return;
                        try {
                            outMessages.wait(1000);
                        } catch (InterruptedException ignored) {
                        }
                    }
                    if (!online) return;
                    try {
                        if (send())
                            outMessages.clear();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        synchronized void enter(Message message) {
            synchronized (outMessages) {
                outMessages.add(message);
                outMessages.notifyAll();
            }
        }

        private boolean send() throws IOException {
//            HttpURLConnection conn;
//            OutputStream os;
//            InputStream is;
//            Scanner scanner;
//            StringBuilder response;
//            try {
//                //connect url
//                conn = (HttpURLConnection) url.openConnection();
//                conn.setDoOutput(true);
//                //send data
//                os = conn.getOutputStream();
//                os.write("messages=".getBytes());
//                os.write(mapper.writeValueAsBytes(messages));
//                /*for (Message message : messages) {
//                    os.write(mapper.writeValueAsString(message).getBytes());
//                }*/
//                os.close();
//                //receive response
//                is = conn.getInputStream();
//                scanner = new Scanner(is);
//                response = new StringBuilder();
//                while (scanner.hasNextLine()) {
//                    response.append(scanner.nextLine() + "\n");
//                }
//                scanner.close();
//                if (response.toString().equals("{status:OK}")) return true;
//            } catch (java.io.IOException ignored) {
//            }
//            return false;
            ConnectionHandler conn =
                    new ConnectionHandler(url.openConnection())
                    .useSession(sharedPref);
            String response = conn.post(
                    mapper.writeValueAsString(outMessages)
            );
            return !(null == response ||
                    !response.startsWith("{\"status\":\"OK\"}"));
        }
    }

    private /*abstract*/ class Receiver extends Thread {
        private String url;
        private static final int INTERVAL = 1000;
        private Boolean online;
        //private final Handler handler;

//        Receiver(String url, Handler handler) throws MalformedURLException {
//            this.url = url;
//            this.handler = handler;
//            online = true;
//        }

        Receiver(String url) throws MalformedURLException {
            this.url = url;
            online = true;
        }

//        abstract void read(Message message);

        void offline() {
            online = false;
        }

        boolean getOnline() {
            return online;
        }

        @Override
        public void run() {
            while (online) {
                try {
                    receive();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    sleep(INTERVAL);
                } catch (InterruptedException ignored) {
                }
            }
        }

        void receive() throws IOException {
//            try {
//                packet = mapper.readValue(url, ReceivePacket.class);
//            } catch (IOException ignored) {
//            }
//            if (null == packet || packet.status.equals(Status.ERROR)) return;
//            for (Message message : packet.messages) {
//                reader.setMessage(message);
//                handler.post(reader);
//            }

            ConnectionHandler conn = new ConnectionHandler(url)
                    .useSession(sharedPref);
            String response = conn.get("start=" + receivedNo);
            ReceivePacket packet = mapper.readValue(
                    response,
                    ReceivePacket.class
            );
            if (null == packet || !packet.getStatus().equals(Status.OK))
                return;
            Log.v(TAG, "No. of message-> " + packet.getMessages().size());
            for (MessageRelationPair message :
                    packet.getMessages()) {
                //message.getMessage().setSelf(me);
                Log.v(TAG, "In-> " + message.getMessage());
                //reader.setMessage(message);
//                handler.post(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        read(message);
//                    }
//                });
//                chatroomView.post(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        read(message);
//                    }
//                });
                Chatroom.this.read(message);
            }
            receivedNo = packet.getReadNo();
        }
    }

//    private abstract class Reader implements Runnable {
//        protected Message message = null;
//
//        Reader(Message message) {
//            this.message = message;
//        }
//
//        public Message getMessage() {
//            return message;
//        }
//
//        void setMessage(Message message) {
//            this.message = message;
//        }
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

@JsonIgnoreProperties(ignoreUnknown=true)
class ReceivePacket {
    private Status status;
    //private List<Message> messages;
    private List<MessageRelationPair> messages;
    private int readNo;

//    ReceivePacket() {
//        messages = new ArrayList<>();
//    }

//    ReceivePacket(Status status, List<Message> messages, int readNo) {
//        this.status = status;
//        this.messages = messages;
//        this.readNo = readNo;
//    }

    ReceivePacket(Status status, List<MessageRelationPair> messages, int readNo) {
        this.status = status;
        this.messages = messages;
        this.readNo = readNo;
    }

    ReceivePacket() {
        messages = new ArrayList<>();
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

//    public List<Message> getMessages() {
//        return messages;
//    }

    public List<MessageRelationPair> getMessages() {
        return messages;
    }

//    public void setMessages(List<Message> messages) {
//        this.messages = messages;
//    }

    public void setMessages(List<MessageRelationPair> messages) {
        this.messages = messages;
    }

    public int getReadNo() {
        return readNo;
    }

    public void setReadNo(int readNo) {
        this.readNo = readNo;
    }
}