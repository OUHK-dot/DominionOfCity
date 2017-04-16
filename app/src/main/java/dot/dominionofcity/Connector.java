package dot.dominionofcity;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

enum Status {OK, WARNING, ERROR}

public class Connector {
    private Context context;
    private Sender sender;
    private final List<String> messages = new ArrayList<>();
    private Receiver receiver;
    private int receivedNo = 0;
    private String url;
    private static final ObjectMapper mapper = new ObjectMapper();
    private ReceiveListener receiveListener;
    private static final String TAG = "Connector";

    Connector(Context context, String url) {
        Log.i(TAG, "Set up");
        this.context = context;
        this.url = url;
    }

    public void setReceiveListener(ReceiveListener listener) {
        this.receiveListener = listener;
    }

    public void enter(String message) {
        Log.v(TAG, "Send-> " + message);
        if (null != sender)
            sender.enter(message);
        else messages.add(message);
    }

    public List<List<JsonNode>> receive() {
        return receiver.getMessages();
    }

    Connector on()
            throws MalformedURLException, InterruptedException {
        off();
        Log.i(TAG, "Log on");
        sender = new Sender(url + "/writeMessage.php");
        sender.start();
        receiver = new Receiver(url + "/readMessage.php");
        receiver.start();
        return this;
    }

    Connector off() throws InterruptedException {
        Log.i(TAG, "Log off");
        if (null != sender && sender.isOnline()) {
            sender.offline();
            sender.join();
        }
        if (null != receiver && receiver.isOnline()) {
            receiver.offline();
            sender.join();
        }
        return this;
    }

    private class Sender extends Thread {
        private URL url;
        private Boolean online = true;

        Sender(String url) throws MalformedURLException {
            this.url = new URL(url);
        }

        void offline() {
            online = false;
        }

        boolean isOnline() {
            return online;
        }

        @Override
        public void run() {
            while (online) {
                synchronized (Connector.this.messages) {
                    if (Connector.this.messages.size() == 0) {
                        try {
                            Connector.this.messages.wait(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        try {
                            if (send())
                                Connector.this.messages.clear();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        void enter(String message) {
            synchronized (Connector.this.messages) {
                Connector.this.messages.add(message);
                Connector.this.messages.notifyAll();
            }
        }

        private boolean send() throws IOException {
            ConnectionHandler conn =
                    new ConnectionHandler(url.openConnection())
                            .useSession(context);
//            String response = conn.post(
//                    mapper.writeValueAsString(Connector.this.messages)
//            );
            String response = conn.post(Connector.this.messages.toString());
            return !(null == response ||
                    !response.startsWith("{\"status\":\"OK\"}"));
        }
    }

    private class Receiver extends Thread {
        private String url;
        private static final int INTERVAL = 1000;
        private Boolean online = true;
        private final List<List<JsonNode>> messages = new ArrayList<>();

        Receiver(String url) {
            this.url = url;
        }

        void offline() {
            online = false;
        }

        boolean isOnline() {
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

        private void receive() throws IOException {
            ConnectionHandler conn = new ConnectionHandler(url)
                    .useSession(context);
            String response = conn.get("start=" + receivedNo);
            ReceivePacket packet = mapper.readValue(
                    response,
                    ReceivePacket.class
            );
            if (null == packet || !packet.getStatus().equals(Status.OK))
                return;
            receivedNo = packet.getReadNo();
            Log.v(TAG, "Receive-> " + response);
            synchronized (this.messages) {
                this.messages.add(packet.getMessages());
            }
            if (null != receiveListener)
                receiveListener.onReceived(packet.getMessages());
        }

        public List<List<JsonNode>> getMessages() {
            synchronized (this.messages) {
                List<List<JsonNode>> messages = new ArrayList<>();
                messages.addAll(this.messages);
                this.messages.clear();
                return messages;
            }
        }
    }

    public interface ReceiveListener {
        void onReceived(List<JsonNode> messages) throws IOException;
    }
}

@JsonIgnoreProperties(ignoreUnknown=true)
class ReceivePacket {
    private Status status;
    private List<JsonNode> messages;
    private int readNo;

    public ReceivePacket() {
    }

    public ReceivePacket(Status status, List<JsonNode> messages, int readNo) {
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

    public List<JsonNode> getMessages() {
        return messages;
    }

    @JsonValue
    public void setMessages(List<JsonNode> messages) {
        this.messages = messages;
    }

    public int getReadNo() {
        return readNo;
    }

    public void setReadNo(int readNo) {
        this.readNo = readNo;
    }
}