package sk.blazicek.udpchat.chat;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sk.blazicek.udpchat.MainActivity;
import sk.blazicek.udpchat.R;

/**
 * @author Jozef Blazicek
 */
public class Chat {
    private MainActivity activity;

    // Informations about chat
    private final String ip;
    private String username;
    private List<Message> messages = new ArrayList<Message>();

    // Metabata
    private boolean current = false;
    private boolean seen = true;

    // View
    private LinearLayout layout;
    private TextView usernameView;
    private TextView ipView;


    public Chat(final String ip, String username, final MainActivity activity) {
        this.activity = activity;
        this.username = username;
        this.ip = ip;

        // Create Layouts
        layout = new LinearLayout(activity);
        layout.setOrientation(LinearLayout.VERTICAL);
        usernameView = new TextView(activity);
        ipView = new TextView(activity);

        TextView space1 = new TextView(activity);
        TextView space2 = new TextView(activity);

        // Setup views
        usernameView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        ipView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        space1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8);
        space2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8);

        space1.setText(" ");
        space2.setText(" ");
        ipView.setText("    " + ip);
        usernameView.setText("  " + username);

        // Add views to layout
        layout.addView(space1);
        layout.addView(usernameView);
        layout.addView(ipView);
        layout.addView(space2);

        // Action Listener - open this chat
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.setCurrent(ip);
            }
        });
    }

    public void addMessage(Message message) {
        if (!current)
            seen = false;
        if (message.sendByUser()) {
            messages.add(message);
            return;
        }

        int last = messages.size();
        while ((last >= 1) && ((message.getId() < messages.get(last - 1).getId()) || (messages.get(last - 1).sendByUser()))) {
            last--;
        }
        while ((last < messages.size()) && (messages.get(last).sendByUser()))
            last++;
        messages.add(last, message);

        Handler handler = new Handler(activity.getMainLooper());
        handler.post(activity);
    }

    public void changeUsername(String username) {
        this.username = username;
        usernameView.setText("   " + username);
    }


    public void setCurrent(boolean current) {
        this.current = current;

        if (current)
            seen = true;
    }


    public String getIp() {
        return ip;
    }

    public String getUsername() {
        return username;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public LinearLayout getLayout(Context context) {
        if (seen) {
            usernameView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
            ipView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));

            usernameView.setTextColor(ContextCompat.getColor(context, R.color.white));
            ipView.setTextColor(ContextCompat.getColor(context, R.color.white));
        } else {
            usernameView.setBackgroundColor(Color.YELLOW);
            ipView.setBackgroundColor(Color.YELLOW);
        }

        return layout;
    }
}
