package com.example.museum;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;



import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {
    private static final String LOG_TAG = HomeActivity.class.getName();
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            Log.d(LOG_TAG, "Authenticated user!");
        } else {
            Log.d(LOG_TAG, "Unauthenticated user!");
            finish();
        }

        // Button: Explore Exhibits
        Button buttonExplore = findViewById(R.id.buttonExplore);
        buttonExplore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, ExhibitsActivity.class));
            }
        });

        // Button: Purchase Tickets
        Button buttonPurchase = findViewById(R.id.buttonPurchase);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && currentUser.isAnonymous()) {
            buttonPurchase.setVisibility(View.GONE);
        } else {
            buttonPurchase.setVisibility(View.VISIBLE);
        }
        buttonPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, TicketPurchaseActivity.class));
            }
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
            startActivity(new Intent(HomeActivity.this, MainActivity.class));
            return true;
        } else if(item.getItemId() == R.id.profile){
            Log.d(LOG_TAG, "Profile clicked!");
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}