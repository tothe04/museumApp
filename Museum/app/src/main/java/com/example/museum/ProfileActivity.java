package com.example.museum;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private static final String LOG_TAG = ProfileActivity.class.getName();

    private EditText userNameEditText;
    private EditText emailEditText;
    private EditText phoneEditText;
    private EditText addressEditText;
    private Button saveButton;

    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        userNameEditText = findViewById(R.id.userNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        emailEditText.setEnabled(false);
        phoneEditText = findViewById(R.id.phoneEditText);
        addressEditText = findViewById(R.id.addressEditText);
        saveButton = findViewById(R.id.saveButton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserProfile();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadUserData();
    }

    private void loadUserData() {
        if (currentUser != null) {
            db.collection("users").document(currentUser.getUid())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                User user = documentSnapshot.toObject(User.class);
                                if (user != null) {
                                    userNameEditText.setText(user.getUserName());
                                    emailEditText.setText(user.getEmail());
                                    phoneEditText.setText(user.getPhone());
                                    addressEditText.setText(user.getAddress());
                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("ProfileActivity", "Error retrieving user data: " + e.getMessage());
                        }
                    });
        }
    }

    private void updateUserProfile() {
        String userName = userNameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String phone = phoneEditText.getText().toString();
        String address = addressEditText.getText().toString();

        if (currentUser != null) {
            Map<String, Object> userData = new HashMap<>();
            userData.put("userName", userName);
            userData.put("email", email);
            userData.put("phone", phone);
            userData.put("address", address);

            db.collection("users").document(currentUser.getUid())
                    .set(userData, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(ProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("ProfileActivity", "Error updating user profile: " + e.getMessage());
                            Toast.makeText(ProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void deleteUserProfile() {
        // Delete user data from Firestore
        db.collection("users").document(currentUser.getUid())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("ProfileActivity", "User data deleted successfully from Firestore");
                        currentUser.delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("ProfileActivity", "User account deleted successfully");
                                        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e("ProfileActivity", "Error deleting user account: " + e.getMessage());
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("ProfileActivity", "Error deleting user data from Firestore: " + e.getMessage());
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
            startActivity(new Intent(this, MainActivity.class));
            return true;
        } else if(item.getItemId() == R.id.profile){
            Log.d(LOG_TAG, "Profile clicked!");
            startActivity(new Intent(ProfileActivity.this, ProfileActivity.class));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void deleteProfile(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Deletion");
        builder.setMessage("Are you sure you want to delete your profile?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteUserProfile();
            }
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }
}
