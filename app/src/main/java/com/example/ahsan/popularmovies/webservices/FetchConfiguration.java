package com.example.ahsan.popularmovies.webservices;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.example.ahsan.popularmovies.BuildConfig;
import com.example.ahsan.popularmovies.R;
import com.example.ahsan.popularmovies.fragments.MovieListing;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by ahsan on 2016-12-25.
 */

public class FetchConfiguration extends AsyncTask<String, Void, String > {


    private final String LOG_TAG = FetchConfiguration.class.getSimpleName();
    private final String LANGUAGE_LOCALE = "en_US";
   private final  String API_KEY= "api_key";
    private final String IMAGE_BASE_URL = "base_url";
   private final  String IMAGES= "images";


    private Fragment fragment;
   public FetchConfiguration(Fragment callingFragment){
       fragment = callingFragment;
    }


    @Override
    protected String  doInBackground(String... params) {

        // If there's no zip code, there's nothing to look up.  Verify size of params.
//        if (params.length == 0) {
//            return null;
//        }

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String responseJSON= null;


        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
              String BASE_URL="";
            Uri builtUri=null;

            BASE_URL = fragment.getString(R.string.configuration_url);
            builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY, BuildConfig.API_KEY).build();


            URL url = new URL(builtUri.toString());
            Log.v(LOG_TAG, url.toString());

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


        Log.v(LOG_TAG, parseConfigJson(responseJSON ));

             try {
                URL url = new URL( parseConfigJson(responseJSON).toString());
                //save to shared preferences
                return url.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }



        return null;
    }


    private String parseConfigJson(String json){
            String IMAGE_SIZE = fragment.getString(R.string.image_size);
        ;
           try {
               JSONObject rootResponse = new JSONObject(json);
               JSONObject imagesJSON = rootResponse.getJSONObject(IMAGES);
               String image_base_url = imagesJSON.getString(IMAGE_BASE_URL);
               return image_base_url + IMAGE_SIZE;


           } catch (JSONException e) {
               e.printStackTrace();
           }


        return  null;
    }





    @Override
    protected void onPostExecute(String  result) {
        if (result != null) {
            ((MovieListing)fragment).setImageBaseURL(result);
        }

    }
}
