package sk.blazicek.udpchat;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sk.blazicek.library.FileProcessing;
import sk.blazicek.library.Network;
import sk.blazicek.udpchat.chat.Chat;
import sk.blazicek.udpchat.chat.Message;
import sk.blazicek.udpchat.udp.UDPReceiver;
import sk.blazicek.udpchat.udp.UDPSender;
import sk.blazicek.udpchat.udp.UDPTimeout;
import sk.blazicek.udpchat.udp.task.send.Broadcast;
import sk.blazicek.udpchat.udp.task.send.SendMessage;
import sk.blazicek.udpchat.udp.task.send.SendRename;
import sk.blazicek.udpchat.udp.task.send.SendTask;

public class MainActivity extends AppCompatActivity implements Runnable {
    // UDP
    private UDPSender udpSender;
    private UDPReceiver udpReceiver;
    private UDPTimeout udpTimeout;

    // Chats
    private List<Message> send = new ArrayList<>();
    private Map<String, Chat> activeChats = new HashMap<String, Chat>();
    private Chat current = null;

    // GUI Components
    private TextView leftCharacters;
    private EditText editText;

    // GUI Settings Components
    private TextView space1;
    private TextView space2;
    private TextView space3;
    private TextView portTextView;
    private TextView maxSizeTextView;
    private EditText portInput;
    private EditText sizeInput;

    // Metadata
    private String ip;
    private boolean settings = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Const.LOG)
            Log.v("MainActivity", "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton refresh = (FloatingActionButton) findViewById(R.id.refresh);
        Button button = (Button) findViewById(R.id.button);

        // init GUI components
        leftCharacters = (TextView) findViewById(R.id.textView);
        editText = (EditText) findViewById(R.id.editText);
        editText.setText(getUsername());

        // Init settingsLayout
        space1 = new TextView(this);
        space2 = new TextView(this);
        space3 = new TextView(this);
        portTextView = new TextView(this);
        maxSizeTextView = new TextView(this);
        portInput = new EditText(this);
        sizeInput = new EditText(this);

        space1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        space2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        space3.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        portTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        maxSizeTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        portInput.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        sizeInput.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

        portTextView.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        maxSizeTextView.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        portInput.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        sizeInput.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));

        portInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        sizeInput.setInputType(InputType.TYPE_CLASS_NUMBER);

        portTextView.setText(" " + getResources().getString(R.string.port));
        maxSizeTextView.setText(" " + getResources().getString(R.string.size));

        // Action Listeners
        final MainActivity activity = this;
        refresh.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (Const.LOG)
                    Log.v("refresh.OnClickListener", "onClick");

                send(new Broadcast(getUsername(), activity));
            }
        });

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (Const.LOG)
                    Log.v("button.OnClickListener", "onClick");

                if (settings) {
                    udpSender.setMaxSize(Integer.parseInt(sizeInput.getText().toString()));
                    udpSender.setPort(Integer.parseInt(portInput.getText().toString()));
                    settings = false;
                    run();
                } else if (current == null) {
                    saveUsername();
                    send(new SendRename(activity, getUsername()));
                } else {
                    SendMessage sendMessage = new SendMessage(current.getIp(), editText.getText().toString());
                    Message message = new Message(activity, sendMessage);

                    current.addMessage(message);
                    send(sendMessage);

                    synchronized (send) {
                        send.add(message);
                    }
                    run();
                }
            }
        });

        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (current != null) {
                    int leftLength = 2000 - s.length();
                    String text = s.toString();
                    String left = Integer.toString(leftLength) + " ";

                    if (leftLength < 0) {
                        text.substring(0, 1999);
                        editText.setText(text);
                    }
                    if (leftLength >= 1000)
                        left = left.charAt(0) + " " + left.substring(1);

                    leftCharacters.setText(left);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.settings) {
            settings = true;
            run();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        udpReceiver = new UDPReceiver(this, ip, send);
        udpSender = new UDPSender(this);
        udpTimeout = new UDPTimeout(this, send);

        udpSender.start();
        udpReceiver.start();
        udpTimeout.start();

        send(new Broadcast(getUsername(), this));
    }

    @Override
    protected void onStop() {
        super.onStop();

        udpTimeout.stop();
        udpReceiver.stop();
        udpSender.stop();
    }

    @Override
    public void onBackPressed() {
        if (settings) {
            settings = false;
            run();
            return;
        }

        if (current != null) {
            current.setCurrent(false);
            current = null;
            run();
        }
    }


    public String getUsername() {
        if (Const.LOG)
            Log.v("MainActivity", "getUsername");

        // Get Device IP
        ip = Network.getDeviceIP(this);

        // Create default username
        String username = getResources().getString(R.string.defaultUser) + " " + ip;

        // Load username from file
        String fromFile = FileProcessing.load(this, "username");
        if (fromFile != null)
            return fromFile;
        return username;
    }

    private void saveUsername() {
        if (Const.LOG)
            Log.v("MainActivity", "saveUsername");

        String username = ((EditText) findViewById(R.id.editText)).getText().toString();
        FileProcessing.save(this, username, "username");
    }


    /**
     * GUI update called from different thread
     */
    @Override
    public void run() {
        if (Const.LOG)
            Log.v("MainActivity", "run");

        Button button = (Button) findViewById(R.id.button);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout);

        linearLayout.removeAllViewsInLayout();

        if (settings) {
            // Set title
            ((CollapsingToolbarLayout) findViewById(R.id.toolbar_layout)).setTitle(getResources().getString(R.string.settings));

            // Add Components
            linearLayout.addView(space1);
            linearLayout.addView(portTextView);
            linearLayout.addView(portInput);
            linearLayout.addView(space2);
            linearLayout.addView(maxSizeTextView);
            linearLayout.addView(sizeInput);
            linearLayout.addView(space3);
            linearLayout.addView(button);

            findViewById(R.id.refresh).setVisibility(View.INVISIBLE);
            button.setText(getResources().getString(R.string.save));
            portInput.setText(udpSender.getPort() + "");
            sizeInput.setText(udpSender.getMaxSize() + "");// TODO
        } else if (current == null) {
            // Set title
            ((CollapsingToolbarLayout) findViewById(R.id.toolbar_layout)).setTitle(getResources().getString(R.string.app_name));

            // Add Components
            linearLayout.addView(editText);
            linearLayout.addView(button);

            Object[] ip = activeChats.keySet().toArray();
            for (Object o : ip) {
                Chat chat = activeChats.get(o);
                linearLayout.addView(chat.getLayout(this));
            }

            findViewById(R.id.refresh).setVisibility(View.VISIBLE);
            button.setText(getResources().getString(R.string.changeUsername));
            editText.setText(getUsername());
        } else {
            // Set title
            ((CollapsingToolbarLayout) findViewById(R.id.toolbar_layout)).setTitle(current.getUsername());

            // Add Components
            linearLayout.addView(editText);
            linearLayout.addView(leftCharacters);
            linearLayout.addView(button);

            List<Message> messages = current.getMessages();
            for (int j = messages.size() - 1; j >= 0; j--) {
                linearLayout.addView(messages.get(j).getLayout());
            }


            findViewById(R.id.refresh).setVisibility(View.INVISIBLE);
            leftCharacters.setText(getResources().getString(R.string.maxCharacters));
            button.setText(getResources().getString(R.string.send));
            editText.setText("");
        }
    }


    /**
     * Ads task to udpSendders task List
     */
    public void send(SendTask task) {
        if (Const.LOG)
            Log.v("MainActivity", "send");

        udpSender.addTask(task);
    }


    public void setCurrent(String ip) {
        current = activeChats.get(ip);
        current.setCurrent(true);
        run();
    }

    public Object[] getActiveChats() {
        return activeChats.keySet().toArray();
    }

    public Chat getChat(String ip) {
        if (Const.LOG)
            Log.v("MainActivity", "getChat(" + ip + ")");

        return activeChats.get(ip);
    }

    public void registerChat(Chat chat) {
        if (Const.LOG)
            Log.v("MainActivity", "registerChat(" + chat.getIp() + ")");

        activeChats.put(chat.getIp(), chat);
    }
}
