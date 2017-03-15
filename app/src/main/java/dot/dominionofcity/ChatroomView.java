package dot.dominionofcity;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.net.MalformedURLException;

public class ChatroomView extends LinearLayout {
    private LinearLayout messageArea;
    static final LayoutParams defaultMessageParams = new LayoutParams(
            LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
    );
    private EditText enterMessage;

    public void addMessage(View view) {
        addMessage(view, defaultMessageParams);
    }

    public void addMessage(View view, LayoutParams params) {
        messageArea.addView(view, params);
    }

    public void removeMessage(View view) {
        messageArea.removeView(view);
    }

    public void clearMessage() {
        messageArea.removeAllViews();
    }

    public String getMessage() {
        String message = String.valueOf(enterMessage.getText());
        enterMessage.setText("");
        return message;
    }

    public View getSend() {
        return this.findViewById(R.id.send);
    }

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
        messageArea = (LinearLayout) this.findViewById(R.id.message_area);
        enterMessage = (EditText) this.findViewById(R.id.enter_message);
    }
}
