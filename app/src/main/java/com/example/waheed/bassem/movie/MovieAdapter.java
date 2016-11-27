package com.example.waheed.bassem.movie;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Custom Adapter for the movie list
 */
public class MovieAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Movie> mMovieList;

    public MovieAdapter(Context context, ArrayList<Movie> movieList) {
        mContext = context;
        mMovieList = movieList;
    }

    @Override
    public int getCount() {
        return mMovieList.size();
    }

    @Override
    public Object getItem(int position) {
        return mMovieList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // declaring a view holder
        ViewHolder viewHolder;

        // checking if the view wasn't created so we will inflate a new one
        if (convertView == null) {
            // inflating the layout
            convertView = LayoutInflater.from(mContext).inflate(R.layout.grid_item, parent, false);
            // finding the views
            TextView posterTextView = (TextView) convertView.findViewById(R.id.poster_movie_name);
            ImageView posterImageView = (ImageView) convertView.findViewById(R.id.poster_image_view);
            // storing the view in a view holder
            viewHolder = new ViewHolder(posterTextView, posterImageView);
            convertView.setTag(viewHolder);
        }

        // retrieving the views from the view holder
        // Making instance of Movie to get the data from the mMovieList
        // TextView and ImageView to populate the list item with data
        viewHolder = (ViewHolder) convertView.getTag();
        Movie movie = mMovieList.get(position);
        TextView posterTextView = viewHolder.getTextView();
        ImageView posterImageView = viewHolder.getImageView();

        // filling the views with the corresponding data
        posterTextView.setText(movie.getMovieName());

        Picasso.with(mContext)
                .load(movie.getMovieImageUrl())
                .into(posterImageView);

        return convertView;
    }

    /**
     * updates the movie list when we have new data
     *
     * @param movieList = the new movie list to be attached to the adapter
     */
    public void updateListData(ArrayList<Movie> movieList) {
        mMovieList = movieList;
        notifyDataSetChanged();
    }


    /**
     * a view holder to store the views instead of calling find view by id every time
     */
    private class ViewHolder {
        private TextView posterTextView;
        private ImageView posterImageView;

        public ViewHolder(TextView t, ImageView v) {
            this.posterTextView = t;
            this.posterImageView = v;
        }

        public TextView getTextView() {
            return posterTextView;
        }

        public ImageView getImageView() {
            return posterImageView;
        }
    }
}







