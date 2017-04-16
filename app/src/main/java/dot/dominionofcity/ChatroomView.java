package dot.dominionofcity;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import java.net.MalformedURLException;

public class ChatroomView extends LinearLayout {
    private ScrollView messageWindow;
    private LinearLayout messageList;
    private Spinner modeSpinner;
    private TextView receiverName;
    private User receiver;
    private EditText enterMessage;
    private Button submit;
    private Handler handler;

    public ChatroomView(Context context) throws MalformedURLException {
        super(context);
        init(context);
    }

    public ChatroomView(Context context, AttributeSet attrs)
            throws MalformedURLException {
        super(context, attrs);
        init(context);
    }

    public ChatroomView(Context context, AttributeSet attrs, int defStyle)
            throws MalformedURLException {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(final Context context) throws MalformedURLException {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.chatroom_view, this, true);
        messageWindow = (ScrollView) this.findViewById(R.id.message_window);
        messageList = (LinearLayout) this.findViewById(R.id.message_list);
        modeSpinner = (Spinner) this.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> modeList = ArrayAdapter.createFromResource(
                context,
                R.array.mode,
                android.R.layout.simple_spinner_dropdown_item
        );
        modeSpinner.setAdapter(modeList);
        modeSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int i, long l) {
                receiverName.setText("");
                receiver = null;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        receiverName = (TextView) this.findViewById(R.id.receiver);
        enterMessage = (EditText) this.findViewById(R.id.enter_message);
        submit = (Button) this.findViewById(R.id.submit);
        handler = new Handler();
    }

    public void addMessage(View view) {
        messageList.addView(view);
        if (messageList.getMeasuredHeight() <= messageWindow.getScrollY() +
                messageWindow.getHeight()) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    messageWindow.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
        }
    }

    public void removeMessage(View view) {
        messageList.removeView(view);
    }

    public void clearMessage() {
        messageList.removeAllViews();
    }

    public Mode getMode() {
        String mode = modeSpinner.getSelectedItem().toString();
        switch (mode) {
            case "Team": return Mode.TEAM;
            case "Room": return Mode.ROOM;
            default: return Mode.PERSON;
        }
    }

    public User getReceiver() {
        return receiver;
    }

    public String getMessage() {
        String message = String.valueOf(enterMessage.getText());
        enterMessage.setText("");
        return message;
    }

    public void setSubmitListerner(OnClickListener listener) {
        submit.setOnClickListener(listener);
    }
}
