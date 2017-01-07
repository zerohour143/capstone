package com.example.capstone.redflow;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;

public class home extends AppCompatActivity {
    String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(com.example.capstone.redflow.R.layout.home);

        userID = getIntent().getStringExtra("userID");

        //Toast.makeText(this, "Welcome " + userID, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(com.example.capstone.redflow.R.menu.actionbar, menu);
        return super.onCreateOptionsMenu(menu);
    }


    public void request(View view) {
        Intent intent = new Intent(this, request.class);
        intent.putExtra("userID", userID);
        startActivity(intent);
    }

    public void history(View view) {
        Toast.makeText(getApplicationContext(), "HISTORY", Toast.LENGTH_SHORT).show();
       /* Intent intent = new Intent(this, history.class);
        startActivity(intent);*/
        Intent intent = new Intent(this, Donation_history.class);
        startActivity(intent);
    }

    public void profile(View view) {
        Intent intent = new Intent(this, profile.class);
        intent.putExtra("userID", userID);
        startActivity(intent);
    }

    public void notification(View view) {
        Toast.makeText(getApplicationContext(), "NOTIFICATION", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, notification.class);
        startActivity(intent);
    }

    public void donor(View view) {
        Toast.makeText(getApplicationContext(), "DONOR", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, beadonor.class);
        startActivity(intent);
    }

    public void location(View view) {
        Toast.makeText(getApplicationContext(), "Red Cross locations", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, redcross_location.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    /*FOR ACTION BAR EVENTS*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionlogout:
                Logout();
                return true;
            case R.id.actionabout:
                about();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void Logout(){
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Do you really want to logout?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "successfully logged out", Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                        backtologin();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    public void backtologin(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
    public void about(){
        Intent intent = new Intent(this, about.class);
        startActivity(intent);
    }
/////////////////////////////////////////////////////
}
