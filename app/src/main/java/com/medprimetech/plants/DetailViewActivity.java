package com.medprimetech.plants;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class DetailViewActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);


        String s = getIntent().getStringExtra("ID");
        Log.d("DetailViewActivity", "onCreate: " + s);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "This willgoto the buy action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        View include_1 = findViewById(R.id.includedlayout);
        final ImageView imageView =  include_1.findViewById(R.id.imagedetail);
        final TextView authorView = include_1.findViewById(R.id.author);
        final TextView titleView = include_1.findViewById(R.id.title);
        final TextView msgView = include_1.findViewById(R.id.msg);


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference().child("feed");
        Query myTopPostsQuery = myRef.orderByKey().limitToFirst(1).startAt(s);


        myTopPostsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String imageURL = "";
                String name = "";
                String author = "";
                String msg = "";
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    // TODO: handle the post
                    name = postSnapshot.child("imgTitle").getValue().toString();
                    author = postSnapshot.child("imgAuthor").getValue().toString();


                    imageURL= postSnapshot.child("imgurl").getValue().toString();
                    msg = postSnapshot.child("imgMSG").getValue().toString();


                }

                ImageLoader imageLoader = ImageLoader.getInstance();

                authorView.setText("Author:"+author);
                titleView.setText("Plant Name:"+name);
                msgView.setText("Info: "+msg);
                imageView.setImageBitmap(AppData.imagedata);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

}
