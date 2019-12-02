package com.example.mentalhealth;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
                submitButtonPressed();
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
        ImageButton edit = findViewById(R.id.editButton);
        edit.setVisibility(View.VISIBLE);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableEditing();
            }
        });
        ImageButton delete = findViewById(R.id.deleteButton);
        delete.setVisibility(View.VISIBLE);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteEntryPressed();
            }
        });
    }


    private void enableEditing() {
        Button submit = findViewById(R.id.SubmitEntry);
        String check = submit.getText().toString();
        if (check.equals("Save Changes")) {
            RatingBar stars = findViewById(R.id.ratingBar);
            stars.setIsIndicator(true);
            EditText firstPos = findViewById(R.id.FirstPos);
            firstPos.setEnabled(false);
            EditText secondPos = findViewById(R.id.SecondPos);
            secondPos.setEnabled(false);
            EditText thirdPos = findViewById(R.id.ThirdPos);
            thirdPos.setEnabled(false);
            EditText extra = findViewById(R.id.CommentsText);
            extra.setEnabled(false);
            submit.setText("Close");
        } else {
            RatingBar stars = findViewById(R.id.ratingBar);
            stars.setIsIndicator(false);
            EditText firstPos = findViewById(R.id.FirstPos);
            firstPos.setEnabled(true);
            EditText secondPos = findViewById(R.id.SecondPos);
            secondPos.setEnabled(true);
            EditText thirdPos = findViewById(R.id.ThirdPos);
            thirdPos.setEnabled(true);
            EditText extra = findViewById(R.id.CommentsText);
            extra.setEnabled(true);
            submit.setText("Save Changes");
        }
    }

    private void deleteEntryPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this entry? This cannot be undone.")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Delete the entry
                    }
                })
                .setNegativeButton("Cancel", null);
        builder.create();
        builder.show();
    }

    private void submitButtonPressed() {
        Button submit = findViewById(R.id.SubmitEntry);
        String check = submit.getText().toString();
        if (check.equals("Close")) {
            finish();
        } else if (check.equals("Save Changes")) {
            // Need to make it save
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
            JournalEntry entry = new JournalEntry(date, stars, first, second, third, extra);
            intent.putExtra("JournalEntry", entry);
            setResult(RESULT_OK, intent);
            finish();
        } catch(Exception e) {

        }
    }
}
