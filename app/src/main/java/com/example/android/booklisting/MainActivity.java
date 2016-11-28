package com.example.android.booklisting;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    //Initialize the string to be extracted
    public final static String EXTRA_MESSAGE = "MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //This method is called when Submit Answers button is clicked.
    public void submitSearch(View view) {

        //Finds the user's search term
        EditText userSearch = (EditText) findViewById(R.id.user_search);
        String Search = userSearch.getText().toString().replace(" ", "+");

        //Check if user entered a search term
        if (Search.matches("")) {
            Toast.makeText(this, "Please enter a search term.", Toast.LENGTH_SHORT).show();
            return;
        } else {

            //Start new BookListing Activity and send user search term
            Intent BookListing = new Intent(MainActivity.this, BookListing.class);
            BookListing.putExtra(EXTRA_MESSAGE, Search);
            startActivity(BookListing);

        }
    }
}
