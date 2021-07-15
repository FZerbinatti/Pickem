package com.dreamsphere.pickem.Activities.SettingsActivities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.dreamsphere.pickem.R;

public class InfoActivity2 extends AppCompatActivity {

    TextView display_text;
    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info2);

/*        display_text = findViewById(R.id.display_text);

        display_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
                intent.putExtra("TYPE", "TEST1");
                intent.putExtra("ALLEGATO", "nome squadra");
                alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);

                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.SECOND, 2);
                Date future10s = calendar.getTime();

                Log.d("AAAAAAAAAAAAAAAAAAAAA", "onClick: ");

                alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                calendar.setTime(future10s);

                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
            }
        });*/


    }
}