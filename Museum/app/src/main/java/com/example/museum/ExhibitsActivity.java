package com.example.museum;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ExhibitsActivity extends AppCompatActivity {
    private static final String LOG_TAG = ExhibitsActivity.class.getName();
    private FirebaseUser user;
    private RecyclerView mRecyclerView;
    private ArrayList<ExhibitItem> mItemList;
    private ExhibitItemAdapter mAdapter;
    FirebaseFirestore mFireStore;
    private CollectionReference mItems;
//    private String[] exhibits = {"Exhibit 1", "Exhibit 2", "Exhibit 3", "Exhibit 4"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exhibits);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            Log.d(LOG_TAG, "Authenticated user!");
        } else {
            Log.d(LOG_TAG, "Unauthenticated user!");
            finish();
        }

        mRecyclerView = findViewById(R.id.recyclerView);

        mItemList = new ArrayList<>();
        mAdapter = new ExhibitItemAdapter(this, mItemList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(
                this, 1));
        mFireStore = FirebaseFirestore.getInstance();
        mItems = mFireStore.collection("exhibits");

        queryData();
//        initializeData();
    }

    private void initializeData() {
        String[] itemsList = getResources()
                .getStringArray(R.array.exhibit_name);
        String[] itemsInfo = getResources()
                .getStringArray(R.array.exhibit_info);
        String[] itemsPrice = getResources()
                .getStringArray(R.array.exhibit_price);
        String[] itemsAvailable = getResources()
                .getStringArray(R.array.exhibit_available);

//        mItemList.clear();

        for (int i = 0; i < itemsList.length; i++) {
            mItems.add(new ExhibitItem(itemsList[i], itemsInfo[i], itemsPrice[i], itemsAvailable[i]));
        }
    }

    private void queryData() {
        mItemList.clear();
        mItems.orderBy("name").limit(10).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                ExhibitItem item = document.toObject(ExhibitItem.class);
                item.setId(document.getId());
                mItemList.add(item);
            }

            if (mItemList.size() == 0) {
                initializeData();
                queryData();
            }
            mAdapter.notifyDataSetChanged();
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.home_menu, menu);
        MenuItem profileMenuItem = menu.findItem(R.id.profile);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        profileMenuItem.setVisible(currentUser == null || !currentUser.isAnonymous());
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.log_out_button){
            Log.d(LOG_TAG, "Logout clicked!");
            FirebaseAuth.getInstance().signOut();
            finish();
            startActivity(new Intent(this, MainActivity.class));
            return true;
        } else if(item.getItemId() == R.id.profile){
            Log.d(LOG_TAG, "Profile clicked!");
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

}
