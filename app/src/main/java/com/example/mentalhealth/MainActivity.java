package com.example.mentalhealth;

import android.content.Intent;
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

public class MainActivity extends AppCompatActivity {

    private String fileStorageNum = "userJournalStorage";

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
    private void createCardView() {
        // Current issues:
            //No spacing between chunks
            //Loads oldest to newest, need to flip
            //Add edit/view buttons to entrys
        int maxDisplay = 5;
        int currentDispaly = 0;
        try {
            LinearLayout parent = findViewById(R.id.JournalEntriesDisplay);
            parent.removeAllViews();
            FileInputStream file = openFileInput(fileStorageNum);
            InputStreamReader inputStreamReader = new InputStreamReader(file);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = "";
            View chunk = null;
            int count = -1;
            while((line=bufferedReader.readLine()) != null && currentDispaly <= maxDisplay) {
                if (line.equals("StartJournalEntry/")) {
                    chunk = getLayoutInflater().inflate(R.layout.chunk_mini_journal_entry, parent, false);
                    count = 0;
                } else if (count == 0) {
                    TextView date = chunk.findViewById(R.id.DateField);
                    date.setText(line);
                    count++;
                } else if (count == 1) {
                    float stars = Float.parseFloat(line);
                    RatingBar rate = chunk.findViewById(R.id.OldRating);
                    rate.setRating(stars);
                    count++;
                } else if (count == 2) {
                    TextView first = chunk.findViewById(R.id.FirstPos);
                    first.setText(line);
                    count++;
                } else if (count == 3) {
                    TextView second = chunk.findViewById(R.id.SecondPos);
                    second.setText(line);
                    count++;
                } else if (count == 4) {
                    TextView third = chunk.findViewById(R.id.ThirdPos);
                    third.setText(line);
                    count++;
                } else if (line.equals("/EndJournalEntry")) {
                    parent.addView(chunk);
                    count = -1;
                    currentDispaly++;
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
