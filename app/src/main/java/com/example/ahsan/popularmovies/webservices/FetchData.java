package com.example.ahsan.popularmovies.webservices;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.ahsan.popularmovies.BuildConfig;
import com.example.ahsan.popularmovies.R;
import com.example.ahsan.popularmovies.fragments.MovieListing;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ahsan on 2016-12-25.
 */

public class FetchData extends AsyncTask<String, Void, JSONArray> {
    private static final String PAGINATION = "1";
    private static final String RESULTS_ARRAY = "results";
    private final String LOG_TAG = FetchData.class.getSimpleName();
    private final String LANGUAGE_LOCALE = "en_US";
    private final String API_KEY = "api_key";
    private final String LANGUAGE = "language";
    private final String PAGE_NO = "page";

    private MovieListing fragment;

    public FetchData(MovieListing callingFragment) {
        fragment = callingFragment;
    }


    @Override
    protected JSONArray doInBackground(String... params) {

        // If there's no zip code, there's nothing to look up.  Verify size of params.
        if (params.length == 0) {
            return null;
        }


        String sortOrder = params[0];

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String responseJSON = null;


        try {

            String BASE_URL = "";
            Uri builtUri = null;
//
//
//            if (sortOrder.equals(fragment.SORTBY_TOP_RATED)) {
//
//                BASE_URL = fragment.getString(R.string.top_rated_movie_url);
//            } else if (sortOrder.equals(fragment.SORTBY_POPULAR)) {
//                BASE_URL = fragment.getString(R.string.popular_movie_url);
//            }


              builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(LANGUAGE, LANGUAGE_LOCALE)
                    .appendQueryParameter(PAGE_NO, PAGINATION)
                    .appendQueryParameter(API_KEY, BuildConfig.API_KEY).build();


            URL url = new URL(builtUri.toString());
            Logger.t(4).d(url.toString());


            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            responseJSON = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

    //    Logger.json(responseJSON.toString());



        return parseResponse(responseJSON);
    }


    private JSONArray parseResponse(String json) {
        String IMAGE_SIZE = fragment.getActivity().getString(R.string.image_size);
        try {
            JSONObject rootResponse = new JSONObject(json);
            JSONArray resultsArray = rootResponse.getJSONArray(RESULTS_ARRAY);
            //we want post path/title/
            return resultsArray;


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }


        return null;
    }


    @Override
    protected void onPostExecute(JSONArray result) {
//        if (result != null) {
//            try {
//
//                fragment.setData(result);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }

    }
}
