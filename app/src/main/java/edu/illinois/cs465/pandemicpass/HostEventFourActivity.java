package edu.illinois.cs465.pandemicpass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

public class HostEventFourActivity extends AppCompatActivity implements View.OnClickListener {

    private Button eventCodeButton;
    private DatePickerDialog datePickerDialog;
    private DatePickerDialog verificationDatePickerDialog;
    private Button eventDateButton;
    private Button verificationDateButton;
    private EditText eventNameText;
    private EditText eventLocationEditText;
    private EditText eventDescriptionEditText;
    private Switch vaxSwitch;
    private Switch testSwitch;

    private String eventName;
    private String eventLocation;
    private String eventDescription;
    private boolean vaxAllowed;
    private boolean testAllowed;
    private int eventMonth;
    private int eventDay;
    private int eventYear;
    private DateFormat dateFormat;

    private DatabaseReference dbReferenceEvent;
    private DatabaseReference dbReferenceUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_event_four);

        dbReferenceEvent = FirebaseDatabase.getInstance()
                .getReference("Event");

        dbReferenceUser = FirebaseDatabase.getInstance()
                .getReference("User");

        initDatePicker();
        eventDateButton = findViewById(R.id.eventDatePickerButtonSummary);
        eventDateButton.setText(getTodaysDate());

        initVerificationDatePicker();
        verificationDateButton = findViewById(R.id.verificationDatePickerButtonSummary);
        verificationDateButton.setText(getTodaysDate());

        eventCodeButton = (Button) findViewById(R.id.genEventCode);
        eventCodeButton.setOnClickListener(this);

        eventNameText = (EditText) findViewById(R.id.finalEventName);
        eventLocationEditText = (EditText) findViewById(R.id.finalEventLocationEditText);
        eventDescriptionEditText = (EditText) findViewById(R.id.finalEventDescriptionEditText);
        vaxSwitch = (Switch) findViewById(R.id.finalVaxSwitch);
        testSwitch = (Switch) findViewById(R.id.finalTestSwitch);

        dateFormat = DateFormat.getDateTimeInstance(
                DateFormat.LONG, DateFormat.LONG,
                Locale.getDefault());

        initExtras();
    }

    private void initExtras() {
        eventName = getIntent().getExtras().getString("event_name");
        eventNameText.setText(eventName);
        vaxAllowed = getIntent().getExtras().getBoolean("vax_allowed");
        vaxSwitch.setChecked(vaxAllowed);
        testAllowed = getIntent().getExtras().getBoolean("test_allowed");
        testSwitch.setChecked(testAllowed);

        eventMonth = getIntent().getExtras().getInt("event_month");
        eventDay = getIntent().getExtras().getInt("event_day");
        eventYear = getIntent().getExtras().getInt("event_year");
        String date = makeDateString(eventMonth, eventDay, eventYear);
        eventDateButton.setText(date);

        eventLocation = getIntent().getExtras().getString("event_location");
        eventLocationEditText.setText(eventLocation);
        eventDescription = getIntent().getExtras().getString("event_description");
        eventDescriptionEditText.setText(eventDescription);
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month = month + 1;
                String date = makeDateString(month, day, year);
                eventDateButton.setText(date);

                eventYear = year;
                eventMonth = month;
                eventDay = day;
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
    }

    private void initVerificationDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month = month + 1;
                String date = makeDateString(month, day, year);
                verificationDateButton.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        verificationDatePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
    }

    private String getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);

        return makeDateString(month, day, year);
    }

    private String makeDateString(int month, int day, int year) {
        return day + " " + getMonthFormat(month) + " " + year;
    }

    private String getMonthFormat(int month) {
        if (month == 1)
            return "JAN";
        if (month == 2)
            return "FEB";
        if (month == 3)
            return "MAR";
        if (month == 4)
            return "APR";
        if (month == 5)
            return "MAY";
        if (month == 6)
            return "JUN";
        if (month == 7)
            return "JUL";
        if (month == 8)
            return "AUG";
        if (month == 9)
            return "SEP";
        if (month == 10)
            return "OCT";
        if (month == 11)
            return "NOV";
        if (month == 12)
            return "DEC";
        return "JAN";
    }

    public void openEventDatePicker(View view) {
        datePickerDialog.show();
    }

    public void openVerificationDatePicker(View view) {
        verificationDatePickerDialog.show();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.genEventCode) {
            String eventName = eventNameText.getText().toString().trim();
            String eventLocation = eventLocationEditText.getText().toString();
            String eventDescription = eventDescriptionEditText.getText().toString();
            boolean vaxAllowed = vaxSwitch.isChecked();
            boolean testAllowed = testSwitch.isChecked();
            Calendar calendar = Calendar.getInstance();
            // Need month - 1 cuz DatePickerDialog is weird
            calendar.set(eventYear, eventMonth - 1, eventDay);
            String eventDate = dateFormat.format(calendar.getTime());

            if (eventName.isEmpty()) {
                eventNameText.setError("Event name is required.");
                eventNameText.requestFocus();
            } else if (eventLocation.isEmpty()) {
                eventLocationEditText.setError("Event location is required.");
                eventLocationEditText.requestFocus();
            } else if (eventDescription.isEmpty()) {
                eventDescriptionEditText.setError("Event description is required.");
                eventDescriptionEditText.requestFocus();
            } else {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String eventCode = UUID.randomUUID().toString().substring(0, 7);

                dbReferenceEvent.orderByChild("eventCode").equalTo(eventCode).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            Event event = new Event(userId, "Ashank", eventCode, eventName, eventDate, eventLocation, eventDescription, new ArrayList<Guest>(), vaxAllowed, testAllowed);
                            dbReferenceEvent.push().setValue(event).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        // will probably remove the toast for success and just redirect instead
                                        Toast.makeText(HostEventFourActivity.this, "Success", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(HostEventFourActivity.this, "Fail", Toast.LENGTH_LONG).show();
                                    }

                                }
                            });
                        }
                        else {
                            Log.e("firebase", "duplicate key");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                Intent intent = new Intent(this, JoinEventCodeActivity.class);

                intent.putExtra("event_code", eventCode);

                startActivity(intent);
            }
        }
    }
}