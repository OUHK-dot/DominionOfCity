package dot.dominionofcity;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

enum Mode {
    PERSON("Person"),
    TEAM("Team"),
    ROOM("Room");

    private String theMode;

    Mode(String aMode) {
        theMode = aMode;
    }

    @Override public String toString() {
        return theMode;
    }
}

@JsonIgnoreProperties(ignoreUnknown=true)
public class Message {
    private User sender;
    private String content;
    private Mode mode;
    private User receiver;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date sendTime;
    public static final SimpleDateFormat dateFormat =
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

    Message(@Nullable User sender, @Nullable String content, @Nullable Mode mode,
            @Nullable User receiver, @Nullable Date sendTime){
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
        if (null == me) return;
        if (null != sender && sender.getId() == me.getId())
            sender.setName(me.getName());
        if (null != receiver && mode.equals(Mode.PERSON) &&
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
        String senderName = "me";
        if (null != sender) senderName = sender.getName();
        String time = null;
        if (null != sendTime)
            time = dateFormat.format(sendTime);
        String receiverIdentity;
        switch (mode) {
            case PERSON:
                receiverIdentity = receiver.getName();
                break;
            case TEAM:
            case ROOM:
            default:
                receiverIdentity = mode.toString();
        }
        return String.format("%s->%s: %s @%s", senderName,
                receiverIdentity, content, time);
    }
}
