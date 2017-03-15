package dot.dominionofcity;

import android.os.Handler;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

enum Status {OK, WARNING, ERROR}

abstract class Chatroom {
    private Sender sender;
    private Receiver receiver;
    private static final ObjectMapper mapper = new ObjectMapper();

    Chatroom(String url, Handler handler) throws MalformedURLException {
        sender = new Sender(url + "/writeMessage.php");
        receiver = new Receiver(url + "/readMessage.php", handler) {
            @Override
            void read(Message message) {
                read(message);
            }
        };
        sender.start();
        receiver.start();
    }

    abstract void read(Message message);

    void enter(Message message) {
        sender.enter(message);
    }

    void offline() {
        sender.offline();
        receiver.offline();
    }

    private class Sender extends Thread {
        private URL url;
        private final List<Message> messages;
        private Boolean online;

        Sender(String url) throws MalformedURLException {
            this.url = new URL(url);
            this.messages = new ArrayList<>();
            online = true;
        }

        void offline() {
            online = false;
        }

        @Override
        public void run() {
            while (true) {
                synchronized (messages) {
                    while (messages.size() == 0) {
                        if (!online) return;
                        try {
                            wait(1000);
                        } catch (InterruptedException ignored) {
                        }
                    }
                    if (send())
                        messages.clear();
                }
            }
        }

        synchronized void enter(Message message) {
            synchronized (messages) {
                messages.add(message);
                notifyAll();
            }
        }

        private boolean send() {
            HttpURLConnection conn;
            OutputStream os;
            InputStream is;
            Scanner scanner;
            StringBuilder response;
            try {
                //connect url
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                //send data
                os = conn.getOutputStream();
                os.write("messages=".getBytes());
                os.write(mapper.writeValueAsBytes(messages));
                /*for (Message message : messages) {
                    os.write(mapper.writeValueAsString(message).getBytes());
                }*/
                os.close();
                //receive response
                is = conn.getInputStream();
                scanner = new Scanner(is);
                response = new StringBuilder();
                while (scanner.hasNextLine()) {
                    response.append(scanner.nextLine() + "\n");
                }
                scanner.close();
                if (response.toString().equals("{status:OK}")) return true;
            } catch (java.io.IOException ignored) {
            }
            return false;
        }
    }

    private abstract class Receiver extends Thread {
        private String url;
        private Date lastUpdate;

        private static final int INTERVAL = 1000;
        private Boolean online;
        private final Handler handler;
        Reader reader = new Reader() {

            @Override
            public void run() {
                if (message != null)
                read(message);
                message = null;
            }
        };

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
                receive();
                try {
                    sleep(INTERVAL);
                } catch (InterruptedException ignored) {
                }
            }
        }

        void receive() {
            ReceivePacket packet = null;
            try {
                packet = mapper.readValue(url, ReceivePacket.class);
            } catch (IOException ignored) {
            }
            if (null == packet || packet.status.equals(Status.ERROR)) return;
            for (Message message : packet.messages) {
                reader.setMessage(message);
                handler.post(reader);
            }
            lastUpdate = packet.lastUpdate;
        }

        class ReceivePacket{
            Status status;
            List<Message> messages;
            Date lastUpdate;
            ReceivePacket() {}
        }
    }

    private abstract class Reader implements Runnable {
        protected Message message = null;

        public Message getMessage() {
            return message;
        }

        void setMessage(Message message) {
            this.message = message;
        }
    }
}

enum Mode {PLAYER, TEAM, WORLD}

@JsonIgnoreProperties(ignoreUnknown=true)
class Message {
    private User sender;
    private String content;
    private Mode mode;
    private User receiver;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss:SSS")
    private Date dateTime;

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
            User receiver, Date dateTime){
        this.sender = sender;
        this.content = content;
        this.mode = mode;
        this.receiver = receiver;
        this.dateTime = dateTime;
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

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        String time = new java.text.SimpleDateFormat("HH:mm", Locale.getDefault())
                .format(dateTime);
        return String.format("%s\n%s\n%s", sender.getName(), content, time);
    }
}