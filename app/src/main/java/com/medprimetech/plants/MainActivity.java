package com.medprimetech.plants;

import android.app.SearchManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import android.Manifest;


public class MainActivity extends AppCompatActivity  {

    private Toolbar tbMainSearch;
    private ListView lvToolbarSerch;
    private String TAG = MainActivity.class.getSimpleName();
    String[] arrays = new String[]{"98411", "98422", "98433", "98444", "98455"};

    private  int first_load = 10;



    //Drawer variables
    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;

    // recycle card varuables
    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<DataModel> data;
    private static ArrayList<DataModel> datafromdb;
    static View.OnClickListener myOnClickListener;
    private static ArrayList<Integer> removedItems;




    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    ImageLoaderConfiguration config ;

    int LastID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create global configuration and initialize ImageLoader with this config
        config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference().child("feed");

        if(false){

            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference().child("feed");
            for(int i =1 ; i<30 ; i++){
                ref.child(String.valueOf(i)).child("imgTitle").setValue("asdasd"+String.valueOf(i));
                ref.child(String.valueOf(i)).child("imgAuthor").setValue("asdasd"+String.valueOf(i));
                ref.child(String.valueOf(i)).child("imgMSG").setValue("asdasd"+String.valueOf(i));
                ref.child(String.valueOf(i)).child("imgurl").setValue("https://firebasestorage.googleapis.com/v0/b/plants-db216.appspot.com/o/neem.jpeg?alt=media&token=5bd4cb1c-311b-4b02-a20c-8f1b3dcc7b09");
            }
        }

        //Query myTopPostsQuery = myRef.orderByKey().limitToFirst(1).startAt("2");
        final Query myTopPostsQuery = myRef.orderByKey().limitToFirst(10);

        myTopPostsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    // TODO: handle the post
                    String name = postSnapshot.child("imgTitle").getValue().toString();
                    String author = postSnapshot.child("imgAuthor").getValue().toString();
                    int id = Integer.parseInt(postSnapshot.getKey().toString());
                    LastID = id;
                    String imageURL= postSnapshot.child("imgurl").getValue().toString();
                    datafromdb.add(new DataModel(name,author,id,imageURL));
                    Log.d(TAG, "onDataChange: "+ postSnapshot.getValue()+postSnapshot.getKey());
                }

                datafromdb.remove(datafromdb.size()-1);
                loadFromDB();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


//        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
//                    // TODO: handle the post
//                    Log.d(TAG, "onDataChange: "+ postSnapshot.child("imgurl").getValue());
//                }
//
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });





        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_CONTACTS, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_SMS, Manifest.permission.CAMERA};

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        setContentView(R.layout.activity_main);

       // mDrawerList = (ListView)findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

        //addDrawerItems();

//        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(MainActivity.this, "Time for an upgrade!", Toast.LENGTH_SHORT).show();
//            }
//        });



        //recycler
        myOnClickListener = new MyOnClickListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        data = new ArrayList<DataModel>();
        datafromdb = new ArrayList<DataModel>();
        adapter = new CustomAdapter(data);
        recyclerView.setAdapter(adapter);



        //loadinitcard();



        final int[] pastVisiblesItems = new int[1];
        final int[] visibleItemCount = new int[1];
        final int[] totalItemCount = new int[1];

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if(dy > 0) //check for scroll down
                {
                    visibleItemCount[0] = layoutManager.getChildCount();
                    totalItemCount[0] = layoutManager.getItemCount();
                    pastVisiblesItems[0] = ((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition();


                    if (loading)
                    {
                        if ( (visibleItemCount[0] + pastVisiblesItems[0]) >= totalItemCount[0])
                        {
                            loading = false;
                            Log.v("...", "Last Item Wow !");
                            //Do pagination.. i.e. fetch new data


                            Query myTopPostsQuery = myRef.orderByKey().limitToFirst(2).startAt(String.valueOf(LastID));


                            myTopPostsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                        // TODO: handle the post
                                        String name = postSnapshot.child("imgTitle").getValue().toString();
                                        String author = postSnapshot.child("imgAuthor").getValue().toString();
                                        int id = Integer.parseInt(postSnapshot.getKey().toString());
                                        LastID = id;
                                        String imageURL= postSnapshot.child("imgurl").getValue().toString();
                                        datafromdb.add(new DataModel(name,author,id,imageURL));
                                        Log.d(TAG, "onDataChange: "+ postSnapshot.getValue()+postSnapshot.getKey());
                                    }

                                    datafromdb.remove(datafromdb.size()-1);
                                    loadFromDB();

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });







//
//                            ImageLoader imageLoader = ImageLoader.getInstance();
//                            imageLoader.loadImage("https://firebasestorage.googleapis.com/v0/b/plants-db216.appspot.com/o/neem.jpeg?alt=media&token=5bd4cb1c-311b-4b02-a20c-8f1b3dcc7b09", new SimpleImageLoadingListener() {
//                                @Override
//                                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                                    // Do whatever you want with Bitmap
//                                    Log.d(TAG, "onLoadingComplete: ");
//                                    loading[0] = true;
//                                    data.add(new DataModel(
//                                            MyData.nameArray[0],
//                                            MyData.versionArray[0],
//                                            MyData.id_[0],
//                                            loadedImage
//                                    ));
//                                    adapter.notifyDataSetChanged();
//                                }
//                            });



                        }
                    }
                }
            }
        });

        mActivityTitle = "Feeds";
        setupDrawer();

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        setUpViews();



        getSupportActionBar().setTitle("Plantae");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


    }



    private void loadinitcard(){
        if(first_load>0){


                ImageLoader imageLoader = ImageLoader.getInstance();

                final int finalI = first_load;

                imageLoader.loadImage("https://www.google.com/images/branding/googlelogo/2x/googlelogo_color_120x44dp.png", new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        // Do whatever you want with Bitmap
                        Log.d(TAG, "onLoadingComplete: ");
                        data.add(new DataModel(
                                MyData.nameArray[finalI],
                                MyData.versionArray[finalI],
                                MyData.id_[finalI],
                                loadedImage
                        ));
                        first_load --;
                        adapter.notifyDataSetChanged();
                        Log.d(TAG, "onLoadingComplete: @@@");
                        loadinitcard();
                    }
                });






        }
    }



    private void loadFromDB(){
        if(datafromdb.size() > 0){
            loading = false;

            ImageLoader imageLoader = ImageLoader.getInstance();

            final int finalI = first_load;

            imageLoader.loadImage(datafromdb.get(0).imageURL, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    // Do whatever you want with Bitmap
                    Log.d(TAG, "onLoadingComplete: ");
                    data.add(new DataModel(
                            datafromdb.get(0).getName(),
                            datafromdb.get(0).getAuthor(),
                            datafromdb.get(0).getId(),
                            loadedImage
                    ));
                    datafromdb.remove(0);
                    adapter.notifyDataSetChanged();
                    Log.d(TAG, "onLoadingComplete: @@@");
                    loadFromDB();
                }
            });

        }else{
            loading = true;
        }

    }


    private void addDrawerItems() {
        String[] osArray = { "Profile", "My Plants", "Write", "Sell" };
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);

    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Navigation!");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

        };





    }

    private void setUpViews() {
        tbMainSearch = (Toolbar)findViewById(R.id.tb_toolbarsearch);

        setSupportActionBar(tbMainSearch);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_search, menu);

        return true;
    }





    private static class MyOnClickListener implements View.OnClickListener {

        private final Context context;

        private MyOnClickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            //removeItem(v);
            int selectedItemPosition = recyclerView.getChildPosition(v);

            Log.d("test", "onClick: "+data.get(selectedItemPosition).getId());

        }


    }




//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        super.onOptionsItemSelected(item);
////        if (item.getItemId() == R.id.add_item) {
////            //check if any items to add
////            if (removedItems.size() != 0) {
////                addRemovedItemToList();
////            } else {
////                Toast.makeText(this, "Nothing to add", Toast.LENGTH_SHORT).show();
////            }
////        }
//
//
//
//        if(mDrawerToggle.onOptionsItemSelected(item)) {
//            if (removedItems.size() != 0) {
//                addRemovedItemToList();
//            } else {
//                Toast.makeText(this, "Nothing to add", Toast.LENGTH_SHORT).show();
//            }
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

//    private static void addRemovedItemToList() {
//        for(int i=0; i< MyData.nameArray.length;i++) {
//
//
//            int addItemAtListPosition = 3;
//            data.add(addItemAtListPosition, new DataModel(
//                    MyData.nameArray[i],
//                    MyData.versionArray[i],
//                    MyData.id_[i],
//                    MyData.drawableArray[i]
//            ));
//            adapter.notifyItemInserted(addItemAtListPosition);
//
//        }
//    }


    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }



}




