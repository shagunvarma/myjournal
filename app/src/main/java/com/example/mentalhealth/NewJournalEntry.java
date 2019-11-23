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
import android.widget.TextView;

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
        Intent intent = getIntent();
        JournalEntry entry = intent.getParcelableExtra("JournalEntry");
        if (entry != null) {
            preLoadEntry(entry);
        }
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

    private void preLoadEntry(JournalEntry entry) {
        TextView title = findViewById(R.id.ToolBarTitle);
        title.setText(entry.getDate());
        RatingBar stars = findViewById(R.id.ratingBar);
        stars.setIsIndicator(true);
        stars.setRating(entry.getStars());
        EditText firstPos = findViewById(R.id.FirstPos);
        firstPos.setEnabled(false);
        firstPos.setText(entry.getFirstPos());
        EditText secondPos = findViewById(R.id.SecondPos);
        secondPos.setEnabled(false);
        secondPos.setText(entry.getSecondPos());
        EditText thirdPos = findViewById(R.id.ThirdPos);
        thirdPos.setEnabled(false);
        thirdPos.setText(entry.getThirdPos());
        EditText extra = findViewById(R.id.CommentsText);
        extra.setEnabled(false);
        extra.setText(entry.getExtraInfo());
        Button submit = findViewById(R.id.SubmitEntry);
        submit.setText("Close");
    }

    private void createNewJournalEntry() {
        Button submit = findViewById(R.id.SubmitEntry);
        String check = submit.getText().toString();
        if (check.equals("Close")) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
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
    }

    private void writeToFile(final Float stars, final String first, final String second, final String third, final String extra) {
        try {
            FileOutputStream file = openFileOutput(fileStorageNum, Context.MODE_APPEND);
            String start = "StartJournalEntry/";
            file.write(("\n" + start).getBytes());
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
            String date = format.format(new Date());
            file.write(("\n" + date).getBytes());
            file.write(("\n" + stars.toString()).getBytes());
            file.write(("\n" + first).getBytes());
            file.write(("\n" + second).getBytes());
            file.write(("\n" + third).getBytes());
            String extraStart = "\nStartExtraInfo/";
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
