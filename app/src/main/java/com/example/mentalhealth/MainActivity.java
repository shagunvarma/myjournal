package com.example.mentalhealth;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.lang.UScript;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String fileStorageName = "userJournalStorage";
    private List<JournalEntry> journals = new ArrayList<>();
    private int newJournalScreen = 22;
    private ProgressDialog DisplayLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //https://material.io/develop/android/components/bottom-app-bar/
        //https://www.truiton.com/2017/01/android-bottom-navigation-bar-example/
        //https://medium.com/@suragch/how-to-add-a-bottom-navigation-bar-in-android-958ed728ef6c
        //https://medium.com/material-design-in-action/implementing-bottomappbar-menu-and-navigation-c4f069e579ec
        //https://www.androidhive.info/2017/12/android-working-with-bottom-navigation/
        BottomAppBar menu = findViewById(R.id.bottomAppBar);
        setSupportActionBar(menu);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewJournalEntry();
            }
        });
        ReadJournalFile asyncTask = new ReadJournalFile();
        asyncTask.execute();
    }

    //https://stackoverflow.com/questions/12293884/how-can-i-send-back-data-using-finish
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == newJournalScreen && resultCode == RESULT_OK) {
            String type = data.getStringExtra("TypeOfResponse");
            if (type != null) {
                if (type.equals("add")) {
                    JournalEntry entry = data.getParcelableExtra("JournalEntry");
                    if (entry != null) {
                        journals.add(entry);
                    }
                } else if (type.equals("edit")) {
                    int index = data.getIntExtra("index", -1);
                    if (index != -1) {
                        JournalEntry entry = data.getParcelableExtra("JournalEntry");
                        if (entry != null) {
                            journals.set(index, entry);
                            // re-write file
                            try {
                                FileOutputStream file = openFileOutput(fileStorageName, Context.MODE_PRIVATE);
                                for (int i = 0; i < journals.size(); i++) {
                                    JournalEntry journal = journals.get(i);
                                    file.write(journal.getWritable().getBytes());
                                }
                                file.close();
                            } catch(Exception e){

                            }

                        }
                    }
                } else if (type.equals("delete")) {
                    int index = data.getIntExtra("index", -1);
                    if (index != -1) {
                        journals.remove(index);
                        try {
                            FileOutputStream file = openFileOutput(fileStorageName, Context.MODE_PRIVATE);
                            for (int i = 0; i < journals.size(); i++) {
                                JournalEntry journal = journals.get(i);
                                file.write(journal.getWritable().getBytes());
                            }
                            file.close();
                        } catch(Exception e){

                        }
                    }
                }
                createCardView();
            }
        }
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
        startActivityForResult(intent, newJournalScreen);
    }


    private void createCardView() {
        // Current issues:
            //No spacing between chunks
            //Add edit/view buttons to entries
        int maxDisplay = 10;
        LinearLayout parent = findViewById(R.id.JournalEntriesDisplay);
        parent.removeAllViews();
        int greater = 0;
        if ((journals.size() - maxDisplay) > 0) {
            greater = journals.size() - maxDisplay;
        }
        for (int i = journals.size() - 1; i >= greater; i--) {
            final JournalEntry entry = journals.get(i);
            final int index = i;
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
            Button view = chunk.findViewById(R.id.ViewButton);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, NewJournalEntry.class);
                    intent.putExtra("JournalEntry", entry);
                    intent.putExtra("index", index);
                    startActivityForResult(intent, newJournalScreen);
                }
            });
            parent.addView(chunk);
        }
    }

    //https://www.tutorialspoint.com/android-asynctask-example-and-explanation

    private class ReadJournalFile extends AsyncTask<String, String, List<JournalEntry>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            DisplayLoading = new ProgressDialog(MainActivity.this);
            DisplayLoading.setMessage("Please wait while your journal loads.");
            DisplayLoading.setIndeterminate(false);
            DisplayLoading.setCancelable(false);
            DisplayLoading.show();
        }
        @Override
        protected List<JournalEntry> doInBackground(String[] strings) {
            journals.clear();
            //https://www.dev2qa.com/android-read-write-internal-storage-file-example/
            try {
                FileInputStream file = openFileInput(fileStorageName);
                InputStreamReader inputStreamReader = new InputStreamReader(file);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line = "";
                String date = "";
                float stars = (float) 0.0;
                String firstPos = "";
                String secondPos = "";
                String thirdPos = "";
                String extraInfo = "";
                boolean firstLine = true;
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
                        firstLine = true;
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
                            if (firstLine) {
                                extraInfo = extraInfo + line;
                                firstLine = false;
                            } else {
                                extraInfo = extraInfo + "\n" + line;
                            }
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
            return journals;
        }
        @Override
        protected void onPostExecute(List<JournalEntry> readJournals) {
            super.onPostExecute(readJournals);
            createCardView();
            DisplayLoading.hide();
        }
    }

}

