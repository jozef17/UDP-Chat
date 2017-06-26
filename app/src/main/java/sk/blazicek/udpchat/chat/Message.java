package sk.blazicek.udpchat.chat;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

import sk.blazicek.udpchat.R;
import sk.blazicek.udpchat.udp.task.send.SendMessage;

/**
 * @author Jozef Blazicek
 */
public class Message {
    private int id;
    private boolean sendByUser;

    // Layout
    private LinearLayout layout;

    // Other
    private SendMessage sendMessage;

    public Message(Context context, SendMessage sendMessage) {
        this(sendMessage.getMessage(), sendMessage.getId(), true, context, sendMessage);
    }

    public Message(String message, int id, Context context) {
        this(message, id, false, context, null);
    }

    private Message(String message, int id, boolean sendByUser, Context context, SendMessage sendMessage) {
        this.sendMessage = sendMessage;
        this.sendByUser = sendByUser;
        this.id = id;

        // Get time
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int month = Calendar.getInstance().get(Calendar.MONTH);
        int year = Calendar.getInstance().get(Calendar.YEAR);

        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minute = Calendar.getInstance().get(Calendar.MINUTE);
        int sec = Calendar.getInstance().get(Calendar.SECOND);

        String time = String.format("%02d.%02d.%d %02d:%02d:%02d", day, month, year, hour, minute, sec);

        // Create Layouts
        layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        TextView space1 = new TextView(context);
        TextView space2 = new TextView(context);
        TextView messageView = new TextView(context);
        TextView timeView = new TextView(context);

        // Setup views
        messageView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        timeView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        space1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8);
        space2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8);

        space1.setText(" ");
        space2.setText(" ");

        // Add views to layout
        layout.addView(space1);
        layout.addView(messageView);
        layout.addView(timeView);
        layout.addView(space2);

        if (sendByUser) {
            timeView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
            messageView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));

            timeView.setTextColor(ContextCompat.getColor(context, R.color.white));
            messageView.setTextColor(ContextCompat.getColor(context, R.color.white));

            messageView.setText("    " + message);
            timeView.setText("  " + time);
        } else {
            timeView.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
            messageView.setBackgroundColor(ContextCompat.getColor(context, R.color.white));

            timeView.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
            messageView.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));

            timeView.setGravity(Gravity.RIGHT);
            messageView.setGravity(Gravity.RIGHT);

            messageView.setText(message + "    ");
            timeView.setText(time + "  ");
        }
    }


    public void confirm() {
        sendMessage = null;
    }

    public SendMessage siConfirmed() {
        return sendMessage;
    }


    // Getters
    public LinearLayout getLayout() {
        return layout;
    }

    public int getId() {
        return id;
    }

    public boolean sendByUser() {
        return sendByUser;
    }
}
