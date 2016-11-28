package com.example.android.booklisting;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class BookListing extends AppCompatActivity {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = BookListing.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_listing);

        // Kick off an {@link AsyncTask} to perform the network request
        BookListingAsyncTask task = new BookListingAsyncTask();
        task.execute();
    }

    /**
     * Update the screen to display information from the given {@link Book}.
     */
    private void updateUi(final ArrayList<Book> books){

        //Display message if the list is empty
        if (books.size() == 0){
            Toast.makeText(this, "No book found.", Toast.LENGTH_LONG).show();
            return;
        }

        // Create a new {@link ArrayAdapter} of books
         BookAdapter adapter = new BookAdapter(this, books);

        // Find a reference to the {@link ListView} in the layout
        ListView bookListView = (ListView) findViewById(R.id.list);

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        bookListView.setAdapter(adapter);

        //Set onClickListener on the bookListView
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Get the current Book object
                Book currentBook = books.get(position);
                //Get the book url from the Book object
                //and set a new intent to open the url
                Uri uri = Uri.parse(currentBook.getUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }
    /**
     * {@link AsyncTask} to perform the network request on a background thread, and then
     * update the UI with the first book in the response.
     */
    public class BookListingAsyncTask extends AsyncTask<URL, Void, ArrayList<Book>> {

        @Override
        protected ArrayList<Book> doInBackground(URL... urls) {
            //Get the user search term from MainActivity
            Intent intent = getIntent();
            String search = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

            //Update the request url with user search term
            String GOOGLE_REQUEST_URL = "https://www.googleapis.com/books/v1/volumes?q="
                    + search + "&maxResults=10";

            // Create URL object
            URL url = createUrl(GOOGLE_REQUEST_URL);

            // Perform HTTP request to the URL and receive a JSON response back
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error making the HTTP request", e);
            }

            // Extract relevant fields from the JSON response and create an {@link Book} object
            ArrayList<Book> books = extractItemsFromJson(jsonResponse);

            // Return the {@link Book} object as the result for the {@link BookListingAsyncTask}
            return books;
        }

        /**
         * Update the screen with the given books (which was the result of the
         * {@link BookListingAsyncTask}).
         */
        @Override
        protected void onPostExecute(ArrayList<Book> books) {
            if (books == null) {
                return;
            }
            updateUi(books);
        }

        /**
         * Returns new URL object from the given string URL.
         */
        private URL createUrl(String stringUrl) {
            URL url = null;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException exception) {
                Log.e(LOG_TAG, "Problem creating the URL string", exception);
                return null;
            }
            return url;
        }

        /**
         * Make an HTTP request to the given URL and return a String as the response.
         */
        private String makeHttpRequest(URL url) throws IOException {

            String jsonResponse = "";
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;

            //If URL is null return early
            if (url == null) {
                return jsonResponse;
            }
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.connect();

                //If url connection is successful (code 200)
                //read the input stream and parse
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                } else {
                    Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem retrieving the books JSON results", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    // function must handle java.io.IOException here
                    inputStream.close();
                }
            }
            return jsonResponse;
        }

        /**
         * Convert the {@link InputStream} into a String which contains the
         * whole JSON response from the server.
         */
        private String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream,
                        Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }

        /**
         * Return an {@link Book} object by parsing out information
         * about the first book from the input bookListingJSON string.
         */
        private ArrayList<Book> extractItemsFromJson(String bookListingJSON) {
            //Create an array to store the book information (title, author(s), ISBN)
             ArrayList<Book> books = new ArrayList<>();

            //If Json string is empty or null, then return early
            if (TextUtils.isEmpty(bookListingJSON)) {
                return null;
            }

            try {
                JSONObject baseJsonResponse = new JSONObject(bookListingJSON);
                JSONArray itemsArray = baseJsonResponse.getJSONArray("items");

                // If there are results in the items array
                for (int i=0; i< itemsArray.length(); i++) {
                    // Extract out the fifth Item (which is volumeInfo)
                    JSONObject fifthItem = itemsArray.getJSONObject(i);
                    JSONObject volumeInfo = fifthItem.getJSONObject("volumeInfo");

                    //Extract the authors array from volumeInfo JSON object
                    JSONArray authorsArray = volumeInfo.getJSONArray("authors");

                    //Initialize a StringBuilder to store author(s) name
                    StringBuilder sb = new StringBuilder();

                    // Extract out the title, author, and url values
                    String title = volumeInfo.getString("title");
                    //Extract the author(s) from the array
                    for (int j = 0; j < authorsArray.length(); j++) {
                        String author = authorsArray.getString(j);
                        sb.append(author).append(" ");
                    }

                    //Create a new string authors to store all the strings appended by
                    // StringBuilder
                    String authors = sb.toString();

                    //Extract the url from the volumeInfo object
                    String url = volumeInfo.getString("infoLink");

                    //Create a new {@link Book} object
                    Book book = new Book(title, authors, url);

                    //Add the book to the array list
                    books.add(book);

                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Problem parsing the books JSON results", e);
            }
            return books;
        }
    }
}
