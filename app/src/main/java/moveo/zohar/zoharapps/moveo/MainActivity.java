package moveo.zohar.zoharapps.moveo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView=(TextView)findViewById(R.id.rotation) ;
        Intent intent = new Intent(getApplicationContext(), MyGyroService.class);
        startService(intent);

    }

    @Override
    protected void onPause() {
        // Unregister since the activity is paused.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                mMessageReceiver);

        super.onPause();
    }

    @Override
    protected void onStop() {
        Intent myService = new Intent(MainActivity.this, MyGyroService.class);
        stopService(myService);
        super.onStop();
    }

    @Override
    protected void onResume() {
        // Register to receive messages.
        // Im registering an observer (mMessageReceiver) to receive Intents
        // with actions named "clockwise-rotation".
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("clockwise-rotation"));
        super.onResume();
    }

    // Our handler for received Intents. This will be called whenever an Intent
    // with an action named "custom-event-name" is broadcasted.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            Log.d("receiver", "Got message: " + message);
            if (message.equals("anticlockwise")) {
                getWindow().getDecorView().setBackgroundColor(Color.YELLOW);
                textView.setText(message);

            } else if (message.equals("clockwise")) {
                getWindow().getDecorView().setBackgroundColor(Color.BLUE);
                textView.setText(message);


            }
        }
    };

}
