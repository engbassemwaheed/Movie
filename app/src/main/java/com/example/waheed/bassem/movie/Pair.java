package com.example.waheed.bassem.movie;

import java.io.Serializable;

/**
 * holds the review and its author
 */

public class Pair implements Serializable{

    private String mTitle;
    private String mBody;

    /**
     *  constructor used to set the data
     * @param title = the title
     * @param body = the body
     */
    public Pair (String title, String body) {
        mTitle = title;
        mBody = body;
    }

    /**
     * getter method for the title
     * @return = the title
     */
    public String getTitle () {
        return mTitle;
    }

    /**
     * getter method for the body
     * @return = body
     */
    public String getBody () {
        return mBody;
    }
}
