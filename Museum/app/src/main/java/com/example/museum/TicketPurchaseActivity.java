package com.example.museum;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class TicketPurchaseActivity extends AppCompatActivity {
    private static final String LOG_TAG = TicketPurchaseActivity.class.getName();
    private Spinner spinnerTicketType;
    private EditText editTextQuantity;
    private Button buttonPurchase;
    private FirebaseFirestore db;

    private NotificationHandler mNotificationHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_purchase);

        spinnerTicketType = findViewById(R.id.spinnerTicketType);
        editTextQuantity = findViewById(R.id.editTextQuantity);
        buttonPurchase = findViewById(R.id.buttonPurchase);

        db = FirebaseFirestore.getInstance();
        retrieveData();

        mNotificationHandler = new NotificationHandler(this);


        buttonPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(TicketPurchaseActivity.this, R.anim.button_anim);
                String ticketType = spinnerTicketType.getSelectedItem().toString();
                String quantity = editTextQuantity.getText().toString();

                if (quantity.isEmpty()) {
                    findViewById(R.id.buttonPurchase).startAnimation(animation);
                    Toast.makeText(TicketPurchaseActivity.this, "Please enter quantity", Toast.LENGTH_SHORT).show();
                    return;
                }
                String message = "Ticket purchased: " + quantity + " " + ticketType;
                mNotificationHandler.send(message);
                Toast.makeText(TicketPurchaseActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void retrieveData() {
        db.collection("exhibits").orderBy("name").limit(10).get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<String> items = new ArrayList<>();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    String item = document.getString("name");
                    items.add(item);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerTicketType.setAdapter(adapter);

        })
         .addOnFailureListener(e -> {
            Log.e(LOG_TAG, "Error retrieving data", e);
             Toast.makeText(TicketPurchaseActivity.this, "Error retrieving data", Toast.LENGTH_SHORT).show();
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
            startActivity(new Intent(TicketPurchaseActivity.this, MainActivity.class));
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
