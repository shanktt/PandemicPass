package edu.illinois.cs465.pandemicpass;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HostEventActivity extends AppCompatActivity implements View.OnClickListener {

    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_event_one);

        nextButton = (Button) findViewById(R.id.hostEventOneNextButton);
        nextButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.hostEventOneNextButton) {
            startActivity(new Intent(HostEventActivity.this, HostEventTwoActivity.class));
        }
    }
}