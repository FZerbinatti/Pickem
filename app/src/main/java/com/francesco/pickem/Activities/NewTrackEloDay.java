package com.francesco.pickem.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.francesco.pickem.Models.EloTracker;
import com.francesco.pickem.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NewTrackEloDay extends AppCompatActivity {

    String elotracker_id;
    String elotracker_date;
    String elotracker_wins;
    String elotracker_losses;
    String elotracker_elo;
    String elotracker_lps;
    private String TAG ="NewTrackEloActivity: ";
    private int mYear, mMonth, mDay, mHour, mMinute;
    Context context;
    EloActivity eloService;

    TextView add_eloday_date;
    EditText add_eloday_wins, add_eloday_losses, add_eloday_lps;
    Spinner add_eloday_spinner;
    Button button_save_today_elotracking;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_track_elo_day);
        add_eloday_date = findViewById(R.id.add_eloday_date);
        add_eloday_wins = findViewById(R.id.add_eloday_wins);
        add_eloday_losses = findViewById(R.id.add_eloday_losses);
        add_eloday_lps = findViewById(R.id.add_eloday_lps);
        button_save_today_elotracking = findViewById(R.id.button_save_today_elotracking);
        add_eloday_spinner = findViewById(R.id.add_eloday_spinner);
        context = this;
        eloService = new EloActivity();

        fillSpinner();

        /*ArrayList <Elo> listOfElos = new ArrayList<>();
        listOfElos.add()
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item,list);*/

        setDate();


        Intent intent = getIntent();
        if (intent.hasExtra( getResources().getString(R.string.elotracker_id) )){

            elotracker_id = intent.getStringExtra(getResources().getString(R.string.elotracker_id) );
            elotracker_date = intent.getStringExtra(getResources().getString(R.string.elotracker_date) );
            elotracker_wins = intent.getStringExtra(getResources().getString(R.string.elotracker_wins) );
            elotracker_losses = intent.getStringExtra(getResources().getString(R.string.elotracker_losses) );
            elotracker_elo = intent.getStringExtra(getResources().getString(R.string.elotracker_elo) );
            elotracker_lps = intent.getStringExtra(getResources().getString(R.string.elotracker_lps) );

            add_eloday_date.setText(elotracker_date);
            add_eloday_wins.setText(elotracker_date);
            add_eloday_losses.setText(elotracker_date);
            add_eloday_lps.setText(elotracker_date);

        }

        button_save_today_elotracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String date = add_eloday_date.getText().toString();
                String wins = add_eloday_wins.getText().toString();
                String losses = add_eloday_losses.getText().toString();
                String lps = add_eloday_lps.getText().toString();
                String elo = add_eloday_spinner.getSelectedItem().toString();



                if (wins.isEmpty()){add_eloday_wins.setError("Please add wins");} else
                if (losses.isEmpty()){add_eloday_losses.setError("Please add wins");}else
                if (lps.isEmpty()){add_eloday_lps.setError("Please add wins");}else{



                    EloTracker eloTracker = new EloTracker(
                            elotracker_id,
                            date,
                            Integer.parseInt(wins),
                            Integer.parseInt(losses),
                            elo,
                            Integer.parseInt(lps));

                    Calendar calendar = Calendar.getInstance();
                    int year = calendar.get(Calendar.YEAR);
                    Log.d(TAG, "onClick: year:"+year);


                    FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users))
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(getString(R.string.firebase_users_elotracker))
                            .child(String.valueOf(year))
                            .child(date )
                            .setValue(eloTracker).addOnCompleteListener(task2 -> {

                        if (task2.isSuccessful()){
                            Log.d(TAG, "added: Notification Region "+elotracker_id);

                        }else{
                            Log.d(TAG, "ERROR: registered: Notification Region "+elotracker_id);
                        }
                    });
                    Intent intent = new Intent(NewTrackEloDay.this, EloTrackerActivity.class);
                    startActivity(intent);
                    finish();
                }




            }
        });








    }

    private void fillSpinner() {

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, R.layout.spinner_item, eloService.getAllElos(context));
        //adapter.setDropDownViewResource( android.R.layout.simple_spinner_item);

        add_eloday_spinner.setAdapter(adapter);

    }

    private void setDate() {

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = df.format(c);
        elotracker_id = formattedDate;
        add_eloday_date.setText(formattedDate);

        add_eloday_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                String formatted = String.format("%02d", monthOfYear + 1);
                                add_eloday_date.setText(year + "-" + formatted + "-" +dayOfMonth );

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }

        });

    }
}