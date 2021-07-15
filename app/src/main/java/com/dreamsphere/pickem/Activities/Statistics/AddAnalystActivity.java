package com.dreamsphere.pickem.Activities.Statistics;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dreamsphere.pickem.Activities.SettingsActivities.InfoActivity2;
import com.dreamsphere.pickem.Adapters.ChooseAnalystRecyclerViewAdapter;
import com.dreamsphere.pickem.Models.AnalistPerson;
import com.dreamsphere.pickem.Models.AnalistPersonChoosen;
import com.dreamsphere.pickem.R;
import com.dreamsphere.pickem.Services.RecyclerItemClickListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AddAnalystActivity extends AppCompatActivity {

    public static final String TAG ="AddAnalystsActivity ";
    RecyclerView add_analysts_list;
    ListView add_analysts_serverlist;
    TextView analyst_request;
    ImageButton back_arrow;
    ChooseAnalystRecyclerViewAdapter adapter;
    ArrayList<String> finalAnalysts;
    ArrayList <AnalistPerson> analists;
    ArrayList<String>userAnalysts;
    Context context;
    TextView analyst_save;
    String serverSelected="";
    EditText edittext_choose_analyst;
    ImageButton analyst_search;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_analyst);
        back_arrow = findViewById(R.id.back_arrow);
        analyst_request = findViewById(R.id.analyst_request);
        analyst_save= findViewById(R.id.analyst_save);
/*        edittext_choose_analyst = findViewById(R.id.edittext_choose_analyst);
        analyst_search= findViewById(R.id.analyst_search);*/
        analists = new ArrayList<>();
        context = this;

        Toast.makeText(context, "Long Press to select", Toast.LENGTH_SHORT).show();


        actionButton();

        getUserAnalysts();

/*        edittext_choose_analyst.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                analyst_request.setVisibility(View.GONE);
                analyst_save.setVisibility(View.GONE);
                return false;
            }
        });*/





    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    private void actionButton() {

/*        analyst_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                analyst_request.setVisibility(View.VISIBLE);
                analyst_save.setVisibility(View.VISIBLE);
                hideKeyboard(AddAnalystActivity.this);

                //


            }
        });*/


        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



        analyst_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: saved: list size: "+ finalAnalysts.size());
                for(int i=0; i<finalAnalysts.size(); i++){
                    Log.d(TAG, "onClick: "+finalAnalysts.get(i).toString());
                }

                FirebaseDatabase.getInstance().getReference("Users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(getResources().getString(R.string.firebase_users_analists))
                        .setValue(finalAnalysts).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onSuccess: ");

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: "+e.toString());
                    }
                });


            }
        });

        analyst_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(AddAnalystActivity.this, InfoActivity2.class);
                startActivity(intent);
            }
        });

    }

    private void getUserAnalysts() {

        userAnalysts = new ArrayList<>();
        finalAnalysts = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getResources().getString(R.string.firebase_users_analists));

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer interazione =0;
                int count = (int) (snapshot.getChildrenCount());

                for(DataSnapshot dataSnapshot : snapshot .getChildren()){
                    String analist = dataSnapshot.getValue(String.class);
                    finalAnalysts.add(analist);
                    userAnalysts.add(analist);
                    interazione++;
                }
                if (interazione == count){
                    Log.d(TAG, "onDataChange: number of analysts: " +analists.size());
                    getAllAnalysts(userAnalysts);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getAllAnalysts(ArrayList<String>userChoosenAnalysts){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_analysts));

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer interazione =0;
                int count = (int) (snapshot.getChildrenCount());

                for(DataSnapshot dataSnapshot : snapshot .getChildren()){
                    AnalistPerson analist = dataSnapshot.getValue(AnalistPerson.class);
                    analists.add(analist);
                    interazione++;
                }
                if (interazione == count){
                    Log.d(TAG, "onDataChange: number of analysts: " +analists.size());
                    loadListviewAnalists(analists,userChoosenAnalysts);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    private void loadListviewAnalists(ArrayList<AnalistPerson> all_analysts_for_this_region, ArrayList<String>userChoosenAnalysts) {

        ArrayList <AnalistPersonChoosen> allAnalystsToDisplay = new ArrayList<>();

        for(int i=0; i<all_analysts_for_this_region.size(); i++){

            if (userChoosenAnalysts.contains(all_analysts_for_this_region.get(i).getUsername())){
                allAnalystsToDisplay.add(new AnalistPersonChoosen(
                        all_analysts_for_this_region.get(i).getImage(),
                        all_analysts_for_this_region.get(i).getRegion(),
                        all_analysts_for_this_region.get(i).getUserId(),
                        all_analysts_for_this_region.get(i).getUsername(),
                        true
                ));
            }else {
                allAnalystsToDisplay.add(new AnalistPersonChoosen(
                        all_analysts_for_this_region.get(i).getImage(),
                        all_analysts_for_this_region.get(i).getRegion(),
                        all_analysts_for_this_region.get(i).getUserId(),
                        all_analysts_for_this_region.get(i).getUsername(),
                        false
                ));
            }
        }

        Collections.sort(allAnalystsToDisplay, new Comparator<AnalistPersonChoosen>() {
            public int compare(AnalistPersonChoosen v1, AnalistPersonChoosen v2) {
                return v1.getRegion().compareTo(v2.getRegion());
            }
        });



        add_analysts_list = findViewById(R.id.add_analysts_list);

        add_analysts_list.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChooseAnalystRecyclerViewAdapter(getApplicationContext(), allAnalystsToDisplay);
        //adapter.notifyDataSetChanged();
        add_analysts_list.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        add_analysts_list.addOnItemTouchListener(

                new RecyclerItemClickListener(context, add_analysts_list , new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Log.d(TAG, "onItemClick: "+position);
                    }

                    @Override public void onLongItemClick(View view, int position) {


                        if (allAnalystsToDisplay.get(position).getChoosen()){
                            allAnalystsToDisplay.get(position).setChoosen(false);
                            adapter.notifyDataSetChanged();
                            //Log.d(TAG, "onLongItemClick: false");
                            if (finalAnalysts.contains(allAnalystsToDisplay.get(position).getUsername())){
                                finalAnalysts.remove(allAnalystsToDisplay.get(position).getUsername());
                            }
                        }else {
                            allAnalystsToDisplay.get(position).setChoosen(true);
                            //Log.d(TAG, "onLongItemClick: true");
                            adapter.notifyDataSetChanged();
                            finalAnalysts.add(allAnalystsToDisplay.get(position).getUsername());



                        }





                    }
                })
        );

        // set up the RecyclerView






    }






}