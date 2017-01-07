package com.example.capstone.redflow.admin;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.capstone.redflow.LoginActivity;
import com.example.capstone.redflow.R;
import com.example.capstone.redflow.about;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;
import java.util.Map;

public class blood_supply_info extends AppCompatActivity {

    private String blood_type;

    private Firebase mRootRef;
    private Query query;

    private TextView bloodtype;
    private TextView bag_quantity;
    private TextView recentlyAdded;
    private EditText vBag_serial;

    private String sBag_serial;
    private int count;

    private DatePicker datePicker;
    private Calendar calendar;
    private TextView dateView;
    private int year, month, day;

    private int mDay, mMonth, mYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blood_supply_info);
        blood_type = getIntent().getStringExtra("blood_type");

        mRootRef = new Firebase("https://redflow-22917.firebaseio.com/");

        bloodtype = (TextView) findViewById(R.id.bloodtype);
        bag_quantity = (TextView) findViewById(R.id.bag_quantity);
        recentlyAdded = (TextView) findViewById(R.id.recently_added);
        vBag_serial = (EditText) findViewById(R.id.bag_serial);

        dateView = (TextView) findViewById(R.id.edittext_date_donated);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month+1, day);


        bloodtype.setText(blood_type);
        query = mRootRef.child("Supply").child(blood_type);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Integer> map = dataSnapshot.getValue(Map.class);
                Map<String, String> map2 = dataSnapshot.getValue(Map.class);

                count = map.get("count");
                bag_quantity.setText("Available: " + count);
                recentlyAdded.setText(map2.get("recent"));
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(com.example.capstone.redflow.R.menu.actionbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void setDate(View view) {
        showDialog(999);
        Toast.makeText(getApplicationContext(), "birthdate",
                Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    mYear = arg1;
                    mMonth = arg2;
                    mDay = arg3;
                    showDate(arg1, arg2+1, arg3);
                }
            };

    private void showDate(int year, int month, int day) {
        dateView.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }

    public void submit_bag(View view) {
        sBag_serial = vBag_serial.getText().toString();
        Firebase blood = mRootRef.child("Blood").push();

        if(sBag_serial.trim().equals("")) {
            Toast.makeText(this, "Please enter a serial number.", Toast.LENGTH_SHORT).show();
        }
        else if(sBag_serial.length() != 8) {
            Toast.makeText(this, "Invalid serial number.", Toast.LENGTH_SHORT).show();
        }
        else {
            if(mYear == 0) {
                mYear = year;
                mMonth = month;
                mDay = day;
            }
            blood.child("donateDay").setValue(mDay);
            blood.child("donateMonth").setValue(mMonth+1);
            blood.child("donateYear").setValue(mYear);
            blood.child("bloodtype").setValue(blood_type);
            blood.child("serial").setValue(sBag_serial.toUpperCase());
            blood.child("userID").setValue("-K_2nAZ1ynR9ZF15HvVw");
            mRootRef.child("Supply").child(blood_type).child("count").setValue(count+1);
            mRootRef.child("Supply").child(blood_type).child("recent").setValue(sBag_serial.toUpperCase());
            Intent intent = new Intent(blood_supply_info.this, blood_supply_info.class);
            intent.putExtra("blood_type", blood_type);
            this.finish();
            Toast.makeText(this, "Successfully added.", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }
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
