package com.example.mentalhealth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NewJournalEntry extends AppCompatActivity {

    private String fileStorageNum = "userJournalStorage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_journal_entry);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button submit = findViewById(R.id.SubmitEntry);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewJournalEntry();
            }
        });
    }

    private void createNewJournalEntry() {
        RatingBar stars = findViewById(R.id.ratingBar);
        float rating = stars.getRating();
        EditText firstPos = findViewById(R.id.FirstPos);
        String first = firstPos.getText().toString();
        EditText secondPos = findViewById(R.id.SecondPos);
        String second = secondPos.getText().toString();
        EditText thirdPos = findViewById(R.id.ThirdPos);
        String third = thirdPos.getText().toString();
        EditText extra = findViewById(R.id.CommentsText);
        String exttaText = extra.getText().toString();
        writeToFile(rating, first, second, third, exttaText);
    }

    private void writeToFile(final Float stars, final String first, final String second, final String third, final String extra) {
        try {
            FileOutputStream file = openFileOutput(fileStorageNum, Context.MODE_APPEND);
            String start = "/StartJournalEntry/";
            file.write(("\n" + start).getBytes());
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
            String date = format.format(new Date());
            file.write(("\n" + date).getBytes());
            file.write(("\n" + stars.toString()).getBytes());
            file.write(("\n" + first).getBytes());
            file.write(("\n" + second).getBytes());
            file.write(("\n" + third).getBytes());
            String extraStart = "\n/StartExtraInfo/";
            file.write(extraStart.getBytes());
            file.write(("\n" + extra).getBytes());
            String extraEnd = "\n/EndExtraInfo";
            file.write(extraEnd.getBytes());
            String end = "\n/EndJournalEntry";
            file.write(end.getBytes());
            file.close();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } catch(Exception e) {

        }
    }
}
