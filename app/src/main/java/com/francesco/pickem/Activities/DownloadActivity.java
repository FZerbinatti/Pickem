package com.francesco.pickem.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.DownloadManager;
import android.content.Context;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.francesco.pickem.Models.RegionDetails;
import com.francesco.pickem.R;
import com.francesco.pickem.SQLite.DatabaseHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DownloadActivity extends AppCompatActivity {

    ProgressBar progressbar_regions, progressbar_teams ,progressbar_schedule;
    TextView regions_percentage, teams_percentage, schedule_percentage;
    private StorageReference storageReference;
    DatabaseHelper databaseHelper;
    private FirebaseAuth mAuth;
    private String TAG ="DownloadActivity: : ";
    final String downloadsPath = Environment.getExternalStorageDirectory() + "/data/";
    final String regionPath = "regions.json";
    private File file;
    String filaPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        filaPath ="";


        progressbar_regions = findViewById(R.id.progressbar_regions_downloads);
        progressbar_teams = findViewById(R.id.progressbar_teams_downloads);
        progressbar_schedule = findViewById(R.id.progressbar_schedule_downloads);
        regions_percentage = findViewById(R.id.regions_percentage);
        teams_percentage = findViewById(R.id.teams_percentage);
        schedule_percentage = findViewById(R.id.schedule_percentage);
        storageReference = FirebaseStorage.getInstance().getReference();


        regions();


/*        holder.line2.setText(String.format("RSSI: %d", rssi));
        holder.progressBar.setProgress(rssiPercentage);
        ColorStateList colorStateList = ContextCompat.getColorStateList(holder.progressBar.getContext(), progressBarColor(rssiPercentage));
        holder.progressBar.setProgressTintList(colorStateList);
        holder.signal_power.setText(rssiPercentage+ "%");*/
    }



    private void regions() {

        downloadJson_Regions();

        try {
            writeJsonRegions(downloadsPath+regionPath);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }



    private void writeJsonRegions(String path) throws IOException, JSONException {

        Log.d(TAG, "writeJsonRegions: path: "+path);

        String jsonDataString = readJsonDataFromFile(path);
        JSONArray jsonArray = new JSONArray(jsonDataString);

        for (int i=0; i<jsonArray.length(); i++){

            JSONObject jsonObject = jsonArray.getJSONObject(i);

            RegionDetails newRegion = new RegionDetails();
            newRegion.setRegion_ID(jsonObject.getInt(getString(R.string.region_ID)));
            newRegion.setRegion_logo(jsonObject.getString(getString(R.string.region_logo)));
            newRegion.setRegion_name(jsonObject.getString(getString(R.string.region_name)));
            newRegion.setRegion_name_ext(jsonObject.getString(getString(R.string.region_name_ext)));
            Log.d(TAG, "onSuccess: region Name downloaded: "+jsonObject.getString(getString(R.string.region_name)));

            databaseHelper.insertRegion(newRegion);

        }
    }

    private void downloadJson_Regions() {
        storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference regionsReference = storageReference.child("regions.json");

        regionsReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d(TAG, "onSuccess: Environment.DIRECTORY_DOWNLOADS:"+ Environment.DIRECTORY_DOWNLOADS);
                downloadFile(DownloadActivity.this, "regions", ".json", downloadsPath, uri.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    private void downloadFile(Context context, String fileName, String fileExtension, String destinationDirectory, String url) {


        Log.d(TAG, "downloadFile: path2: "+destinationDirectory);

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName+fileExtension);

        downloadManager.enqueue(request);





    }

    /*private void donloadRegionJson() throws IOException {

        StorageReference riversRef = storageReference.child("regions.json");
        //download from storage i file json di Teams e Regions

        String root = Environment.getExternalStorageDirectory().toString();

        File localFile = new File(downloadsPath);
        if (!localFile.exists()) {
            localFile.mkdirs();
        }
        File file = new File (localFile, "regions.json");

        Log.d(TAG, "donloadRegionJson: file.getAbsolutePath(): "+file.getAbsolutePath());
        riversRef.getFile(file)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        // Successfully downloaded data to local file
                        try {
                            String jsonDataString = readJsonDataFromFile(downloadsPath);
                            JSONArray jsonArray = new JSONArray(jsonDataString);

                            for (int i=0; i<jsonArray.length(); i++){

                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                RegionDetails newRegion = new RegionDetails();
                                newRegion.setRegion_ID(jsonObject.getInt(getString(R.string.region_ID)));
                                newRegion.setRegion_logo(jsonObject.getString(getString(R.string.region_logo)));
                                newRegion.setRegion_name(jsonObject.getString(getString(R.string.region_name)));
                                newRegion.setRegion_name_ext(jsonObject.getString(getString(R.string.region_name_ext)));
                                Log.d(TAG, "onSuccess: region Name downloaded: "+jsonObject.getString(getString(R.string.region_name)));

                                databaseHelper.insertRegion(newRegion);



                            }

                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle failed download
                // ...
            }
        });

    }*/

    private String readJsonDataFromFile(String path) throws IOException {


        InputStream inputStream= null;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            String jsonString = null;
            inputStream = new FileInputStream(path);
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader( inputStream, "UTF-8")
            );

        }finally {
            if (inputStream!= null){
                inputStream.close();
            }
        }
        return  new String(stringBuilder);

    }

    private Integer progressBarColor(Integer percentage){

        if (percentage < 20 ){
            return (R.color.r1);
        }else if (percentage >= 20 && percentage <40){
            return (R.color.r2);
        }else if (percentage >= 40 && percentage <55){
            return (R.color.y1);
        } else if (percentage >= 55 && percentage <70){
            return (R.color.y2);
        }else if (percentage >= 70 && percentage <80){
            return (R.color.g1);
        }else if (percentage >= 80 && percentage <90){
            return (R.color.g2);
        }else if (percentage >=90){
            return (R.color.g3);
        }else return R.color.transparent;
    }
}