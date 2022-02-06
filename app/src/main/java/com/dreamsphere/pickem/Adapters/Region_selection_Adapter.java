package com.dreamsphere.pickem.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.dreamsphere.pickem.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;

public class Region_selection_Adapter extends PagerAdapter {

    private ArrayList<String> leaguesSelectedList;
    private LayoutInflater layoutInflater;
    private Context context;
    private String TAG ="Adapter ";
    String imageRegionPath ;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference regionReference = storage.getReference("region_img");





    public Region_selection_Adapter(ArrayList<String> leaguesSelectedList, Context context) {
        this.leaguesSelectedList = leaguesSelectedList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return leaguesSelectedList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.cardview_item_leagueselection, container, false);

        ImageView imageView;
        //TextView title, desc;

        imageView = view.findViewById(R.id.league_logo);
       // title = view.findViewById(R.id.league_name);

        //imageView.setImageResource(leaguesSelectedList.get(position).getImage());
        //title.setText(leaguesSelectedList.get(position).getLeague_name());




        //imageRegionPath = context.getFilesDir().getAbsolutePath() + "/images/regions/";

        RequestOptions options2 = new RequestOptions()
                .fitCenter()
                //.diskCacheStrategy(DiskCacheStrategy.NONE)
                //.skipMemoryCache(true)
                .error(R.drawable.ic_loading_error);


        regionReference.child(leaguesSelectedList.get(position).replace(" ", "")+".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Glide.with(context)
                        .load(uri) // Uri of the picture
                        .apply(options2)
                        .transition(DrawableTransitionOptions.withCrossFade(500))
                        .into(imageView);

            }
        });





        container.addView(view, 0);
        return view;

    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
