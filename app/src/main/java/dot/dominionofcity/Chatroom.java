package dot.dominionofcity;

import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

enum Status {OK, WARNING, ERROR}

abstract class Chatroom {
    private User me;
    private Sender sender;
    private final List<Message> outMessages;
    private Receiver receiver;
    private int receivedNo;
    private static final ObjectMapper mapper = new ObjectMapper();
    private String url;
    private Handler handler;
    private SharedPreferences sf;
    private static final String TAG = "Chatroom";

    Chatroom(User me, String url, Handler handler) throws MalformedURLException {
        this(me, url, handler, null);
    }

    Chatroom(User me, String url, Handler handler, SharedPreferences sf) throws MalformedURLException {
        Log.i(TAG, "Set up");
        this.me = me;
        this.url = url;
        this.handler = handler;
        this.sf = sf;
        sender = new Sender(url + "/writeMessage.php");
        outMessages = new ArrayList<>();
        receiver = new Receiver(url + "/readMessage.php", handler) {
            @Override
            void read(Message message) {
                Chatroom.this.read(message);
            }
        };
        receivedNo = 0;
    }

    public void setMe(User me) {
        this.me = me;
    }

    abstract void read(Message message);

    void enter(Message message) {
        Log.v(TAG, "Out-> " + message);
        sender.enter(message);
    }

    Chatroom on() throws MalformedURLException, InterruptedException {
        off();
        Log.i(TAG, "Log on");
        sender = new Sender(url + "/writeMessage.php");
        receiver = new Receiver(url + "/readMessage.php", handler) {
            @Override
            void read(Message message) {
                Chatroom.this.read(message);
            }
        };
        sender.start();
        receiver.start();
        return this;
    }

    Chatroom off() throws InterruptedException {
        if (null != sender && null != receiver) {
            Log.i(TAG, "Log off");
            sender.offline();
            receiver.offline();
            sender.join();
            receiver.join();
            sender = null;
            receiver = null;
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
            ConnectionHandler conn = new ConnectionHandler(url.openConnection())
                    .useSession(sf);
            String response = conn.post(mapper.writeValueAsString(outMessages));
            return !(null == response || !response.startsWith("{\"status\":\"OK\"}"));
        }
    }

    private abstract class Receiver extends Thread {
        private String url;
        private static final int INTERVAL = 1000;
        private Boolean online;
        private final Handler handler;

        Receiver(String url, Handler handler) throws MalformedURLException {
            this.url = url;
            this.handler = handler;
            online = true;
        }

        abstract void read(Message message);

        void offline() {
            online = false;
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

            ConnectionHandler conn = new ConnectionHandler(url).useSession(sf);
            String response = conn.get("start=" + receivedNo);
            ReceivePacket packet = mapper.readValue(
                    response,
                    ReceivePacket.class
            );
            if (null == packet || !packet.status.equals(Status.OK)) return;
            Log.v(TAG, "No. of message-> " + packet.messages.size());
            for (Message message : packet.messages) {
                message.setSelf(me);
                Log.v(TAG, "In-> " + message);
                //reader.setMessage(message);
                handler.post(new Reader(message) {

                    @Override
                    public void run() {
                        if (message != null)
                            read(message);
                    }
                });
            }
            receivedNo = packet.readNo;
        }
    }

    private abstract class Reader implements Runnable {
        protected Message message = null;

        Reader(Message message) {
            this.message = message;
        }

        public Message getMessage() {
            return message;
        }

        void setMessage(Message message) {
            this.message = message;
        }
    }
}

enum Mode {PERSON, TEAM, ROOM}

@JsonIgnoreProperties(ignoreUnknown=true)
class Message {
    private User sender;
    private String content;
    private Mode mode;
    private User receiver;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date sendTime;
    private static final SimpleDateFormat dateFormat =
            new java.text.SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());

    Message() {}

    Message(User sender) {
        this(sender, null, null, null, null);
    }

    Message(User sender, String content, Mode mode) {
        this(sender, content, mode, null, null);
    }

    Message(User sender, String content, Mode mode, User receiver) {
        this(sender, content, mode, receiver, null);
    }

    Message(@NotNull User sender, String content, Mode mode,
            User receiver, Date sendTime){
        this.sender = sender;
        this.content = content;
        this.mode = mode;
        this.receiver = receiver;
        this.sendTime = sendTime;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public void setSelf(User me) {
        if (sender.getId() == me.getId())
            sender.setName(me.getName());
        if (mode.equals(Mode.PERSON) &&
                receiver.getId() == me.getId())
            receiver.setName(me.getName());
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    @Override
    public String toString() {
        String time = null;
        if (null != sendTime)
            time = dateFormat.format(sendTime);
        switch (mode) {
            case PERSON:
                return String.format("%s->%s: %s @%s", sender.getName(), receiver.getName(), content, time);
            case TEAM:
                return String.format("%s->%s: %s @%s", sender.getName(), Mode.TEAM, content, time);
            default:
                return String.format("%s->%s: %s @%s", sender.getName(), Mode.ROOM, content, time);
        }
    }
}

@JsonIgnoreProperties(ignoreUnknown=true)
class ReceivePacket{
    Status status;
    List<Message> messages;
    int readNo;

    ReceivePacket() {
        messages = new ArrayList<>();
    }

    ReceivePacket(Status status, List<Message> messages, int readNo) {
        this.status = status;
        this.messages = messages;
        this.readNo = readNo;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public int getReadNo() {
        return readNo;
    }

    public void setReadNo(int readNo) {
        this.readNo = readNo;
    }
}