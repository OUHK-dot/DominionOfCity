package dot.dominionofcity.chatroom;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import dot.dominionofcity.R;

import static android.content.Context.NOTIFICATION_SERVICE;

public class Beeper {
    public static final int notifyID = 69;
    private boolean on;
    private Notification.InboxStyle content;
    //    private RemoteViews notificationLayout;
    private Notification.Builder builder;
    private NotificationManager manager;
    private int messageCount;

    public Beeper(Context context) {
        initNotification(context);
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
        if (on) {
            initNotificationContent();
        }
    }

    private void initNotificationContent() {
        content = new Notification.InboxStyle()
                .setBigContentTitle("Dominion of City Chatroom");
        messageCount = 0;
//        notificationLayout = new RemoteViews(context.getPackageName(),
//                R.layout.chatroom_notification_layout);
    }

    private void initNotification(Context context) {
        initNotificationContent();
//        notificationLayout = new RemoteViews(context.getPackageName(),
//                R.layout.chatroom_notification_layout);

        // Creates an Intent for the Activity
        Intent intent = new Intent(context, ChatroomActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT
        );

        builder = new Notification.Builder(context)
                .setSmallIcon(R.color.blue)
                .setContentTitle("Dominion of City Chatroom")
                .setContentText("New message(s)")
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        manager = (NotificationManager)
                context.getSystemService(NOTIFICATION_SERVICE);
        on = true;
    }

    public void beep(Message message) {
        messageCount++;
        content.addLine(
                String.format("%s: %s",
                        message.getSender().getName(),
                        message.getContent()
                )
        ).setSummaryText("you have " + messageCount
                + " message" + ((messageCount == 1) ? "" : "s"));
        builder.setStyle(content);

//        RemoteViews notificationLine = new RemoteViews(
//                context.getPackageName(),
//                R.id.chatroom_notification_line
//        );
//        notificationLine.setTextViewText(
//                R.id.chatroom_notification_sender_name,
//                message.getSender().getName()
//        );
//        notificationLine.setTextViewText(
//                R.id.chatroom_notification_content,
//                message.getContent()
//        );
//        notificationLine.setTextViewText(
//                R.id.chatroom_notification_send_time,
//                Message.dateFormat.format(message.getSendTime())
//        );
//        notificationLayout.addView(R.id.chatroom_notification,
//                notificationLine);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            notificationBuilder.setCustomContentView(notificationLayout);
//        }
//        else notificationBuilder.setContent(notificationLayout);
        manager.notify(notifyID, builder.build());
    }

}
