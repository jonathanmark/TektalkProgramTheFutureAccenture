package com.tektalk.finalcode;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.tektalk.finalcode.R;

import java.util.ArrayList;
import java.util.List;

public class Bookmark extends AppCompatActivity {
    ListView listViewWord;

    DatabaseReference databaseWord;
    private int countWord=0;

    public static List<SavedWord> Wordlist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        databaseWord = FirebaseDatabase.getInstance().getReference("SavedWord");

        listViewWord = (ListView) findViewById(R.id.listWord);

        Wordlist = new ArrayList<>();

    }

    @Override
    protected void onStart() {
        super.onStart();
        final TextView textViews = (TextView) findViewById(R.id.saveCountWords);
        databaseWord.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot wordSnapshot: snapshot.getChildren()){
                    SavedWord savedWord = wordSnapshot.getValue(SavedWord.class);
                    Wordlist.add(savedWord);
                }
                if(snapshot.exists()){
                    countWord=(int) snapshot.getChildrenCount();
                    textViews.setText(Integer.toString(countWord) + " Saved Word");
                } else {
                    textViews.setText("0 Word");
                }

                WordList adapter = new WordList(Bookmark.this, Wordlist);
                listViewWord.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}