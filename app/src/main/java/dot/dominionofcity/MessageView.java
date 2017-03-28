package dot.dominionofcity;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

// I => User of this device
enum Relation {SOMEONE, I, A, B}

public class MessageView extends RelativeLayout {
    private Message message;
    private Relation relation;
    private TextView senderName;
    private TextView content;
    private TextView sendTime;

    public MessageView(Context context) {
        super(context);
        init(context);
    }

    public MessageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.message_view, this, true);
    }

    public MessageView initMessage(Message message) {
        return initMessage(message, Relation.SOMEONE);
    }

    public MessageView initMessage(Message message, Relation relation) {
        this.message = message;
        this.relation = relation;
        senderName = (TextView) this.findViewById(R.id.sender_name);
        content = (TextView) this.findViewById(R.id.content);
        sendTime = (TextView) this.findViewById(R.id.send_time);
        senderName.setText(message.getSender().getName());
        decorateName();
        content.setText(message.getContent());
        decorateContent();
        sendTime.setText(Message.dateFormat.format(message.getSendTime()));
        return this;
    }

    private void decorateName() {
        decorateName(senderName, relation);
    }

    private void decorateName(TextView senderName, Relation relation) {
        int nameColor;
        switch (relation) {
            case SOMEONE:
                nameColor = getResources().getColor(R.color.white);
                break;
            case I:
                LinearLayout.LayoutParams params =
                        new LinearLayout.LayoutParams(
                                LayoutParams.WRAP_CONTENT,
                                LayoutParams.WRAP_CONTENT
                        );
                params.gravity = Gravity.RIGHT;
                params.gravity = Gravity.END;
                this.setLayoutParams(params);
                nameColor = getResources().getColor(R.color.blue);
                break;
            case A:
                nameColor = getResources().getColor(R.color.green);
                break;
            case B:
            default:
                nameColor = getResources().getColor(R.color.magenta);
                break;
        }
        senderName.setTextColor(nameColor);
    }

    private void decorateContent() {
        decorateContent(content, relation);
    }

    private void decorateContent(TextView content, Relation relation) {
        int modeColor;
        switch (message.getMode()) {
            case PERSON:
                modeColor = getResources().getColor(R.color.blue);
                break;
            case TEAM:
                switch (relation) {
                    case A:
                        modeColor = getResources().getColor(R.color.green);
                        break;
                    case B:
                    default:
                        modeColor = getResources().getColor(R.color.magenta);
                        break;
                }
                break;
            case ROOM:
            default:
                modeColor = getResources().getColor(R.color.orange);
                break;
        }
        content.setTextColor(modeColor);
    }
}
