package com.example.android.booklisting;

/**
 * Created by jitso on 9/13/2016.
 */
public class Book {

    /** Title of the book */
    private String mTitle;

    /** Author(s) of the book */
    private String mAuthor;

    /** url of the book */
    private String mUrl;

    /**
     * Constructs a new {@link Book}.
     *
     * @param bookTitle is the title of the book
     * @param bookAuthor is the author(s) of the book
     * @param bookUrl is the ISBN of the book
     */
    public Book(String bookTitle, String bookAuthor, String bookUrl) {
        mTitle = bookTitle;
        mAuthor = bookAuthor;
        mUrl = bookUrl;
    }

    //Get the time of the book
    public String getTitle() {return mTitle;}

    //Get the place of the book
    public String getAuthor() {return mAuthor;}

    //Get the url of the book
    public String getUrl() {return mUrl;}
}
