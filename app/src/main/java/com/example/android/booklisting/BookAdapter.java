package com.example.android.booklisting;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jitso on 9/15/2016.
 */
public class BookAdapter extends ArrayAdapter<Book> {

    private static final String LOG_TAG = BookAdapter.class.getSimpleName();

    /**
     * Constructs a new {@link BookAdapter}.
     *
     * @param context of the app
     * @param books   is the list of books, which is the data source of the adapter
     */
    public BookAdapter(Activity context, ArrayList<Book> books) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, books);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        // Get the {@link Book} object located at this position in the list
        Book currentBook = getItem(position);

        //Find the TextView with the view id book_title
        TextView titleTextView = (TextView) listItemView.findViewById(R.id.book_title);
        //Set the text on the TextView from {@link Book} object
        titleTextView.setText(currentBook.getTitle());

        //Find the TextView with the view id book_author
        TextView authorTextView = (TextView) listItemView.findViewById(R.id.book_author);
        //Set the text on the TextView from {@link Book} object
        authorTextView.setText(currentBook.getAuthor());

        //Return the list with book title and book author
        return listItemView;
    }
}
