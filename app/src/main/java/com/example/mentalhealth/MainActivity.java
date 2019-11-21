package com.example.mentalhealth;

import android.content.Intent;
import android.icu.lang.UScript;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String fileStorageNum = "userJournalStorage";
    private List<JournalEntry> journals = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewJournalEntry();
            }
        });
        readFileData();
        createCardView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createNewJournalEntry() {
        Intent intent = new Intent(this, NewJournalEntry.class);
        startActivity(intent);
    }

    //https://www.dev2qa.com/android-read-write-internal-storage-file-example/
    private void readFileData() {
        journals.clear();
        try {
            FileInputStream file = openFileInput(fileStorageNum);
            InputStreamReader inputStreamReader = new InputStreamReader(file);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = "";
            String date = "";
            float stars = (float) 0.0;
            String firstPos = "";
            String secondPos = "";
            String thirdPos = "";
            String extraInfo = "";
            int count = -1;
            while((line=bufferedReader.readLine()) != null) {
                if (line.equals("StartJournalEntry/")) {
                    date = "";
                    stars = (float) 0.0;
                    firstPos = "";
                    secondPos = "";
                    thirdPos = "";
                    extraInfo = "";
                    count = 0;
                } else if (count == 0) {
                    date = line;
                    count++;
                } else if (count == 1) {
                    stars = Float.parseFloat(line);
                    count++;
                } else if (count == 2) {
                    firstPos = line;
                    count++;
                } else if (count == 3) {
                    secondPos = line;
                    count++;
                } else if (count == 4) {
                    thirdPos = line;
                    count++;
                } else if (line.equals("StartExtraInfo/")) {
                    while ((line=bufferedReader.readLine()) != null && !line.equals("/EndExtraInfo")) {
                        extraInfo = extraInfo + "\n" + line;
                    }
                } else if (line.equals("/EndJournalEntry")) {
                    JournalEntry newJournal = new JournalEntry(date, stars, firstPos, secondPos, thirdPos, extraInfo);
                    journals.add(newJournal);
                    count = -1;
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void createCardView() {
        // Current issues:
            //No spacing between chunks
            //Add edit/view buttons to entries
        int maxDisplay = 5;
        LinearLayout parent = findViewById(R.id.JournalEntriesDisplay);
        parent.removeAllViews();
        int greater = 0;
        if ((journals.size() - maxDisplay) > 0) {
            greater = journals.size() - maxDisplay;
        }
        for (int i = journals.size() - 1; i >= greater; i--) {
            JournalEntry entry = journals.get(i);
            View chunk = getLayoutInflater().inflate(R.layout.chunk_mini_journal_entry, parent, false);
            TextView date = chunk.findViewById(R.id.DateField);
            date.setText(entry.getDate());
            RatingBar rate = chunk.findViewById(R.id.OldRating);
            rate.setRating(entry.getStars());
            TextView first = chunk.findViewById(R.id.FirstPos);
            first.setText(entry.getFirstPos());
            TextView second = chunk.findViewById(R.id.SecondPos);
            second.setText(entry.getSecondPos());
            TextView third = chunk.findViewById(R.id.ThirdPos);
            third.setText(entry.getThirdPos());
            parent.addView(chunk);
        }
    }
}
