package hypertrack.i0.sfmovies;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * Created by anuj on 5/7/15.
 */
public class FetchLocation  extends AsyncTask<String, Void, String[]> {

    MainActivityFragment mainActivityFragment;

    public FetchLocation(MainActivityFragment f)
    {
        this.mainActivityFragment=f;
    }

    String LOG_TAG=FetchLocation.class.getSimpleName();

    @Override
    protected String[] doInBackground(String... Title) {



        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String moviesjsondata = null;

        try {

            String mtitle="";
            StringTokenizer str = new StringTokenizer(Title[0]);

            while(str.hasMoreElements())
            {
                mtitle+=str.nextElement()+"%20";
            }
            Log.v(LOG_TAG, mtitle);

            URL url = new URL("http://52.25.133.178:8080/HyperTrack/movies/"+mtitle);
            Log.v(LOG_TAG,url.toString());
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream =urlConnection.getInputStream();
            StringBuffer  buffer = new StringBuffer();
            if(inputStream==null)
            {
                moviesjsondata=null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;

            while ((line = reader.readLine()) != null) {

                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                moviesjsondata = null;
            }
            moviesjsondata = buffer.toString();
            Log.v(LOG_TAG, "movies json string" + moviesjsondata);

        } catch (Exception e) {
            Log.e("PlaceholderFragment", "Error ", e);

            moviesjsondata = null;
        }

        try
        {
            String [] locations = getlocations(moviesjsondata);
            for(String s:locations) {
                Log.v(LOG_TAG, s);
            }
            return locations;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }


    @Override
    protected  void onPostExecute(String[] result)
    {
 mainActivityFragment.updatelocation(result);
    }



    private String[] getlocations(String moviesjson) throws JSONException
    {

        final String Movie = "data";
        JSONObject moviesJson = new JSONObject(moviesjson);
        JSONArray moviesArray = moviesJson.getJSONArray(Movie);

        ArrayList<String> locations= new ArrayList<String>();

        for(int i=0;i<moviesArray.length();i++)
        {
            String location;
            JSONObject MovieObj = moviesArray.getJSONObject(i);
            String loc = MovieObj.getString("locations");
            locations.add(loc);
        }
        String[] array = new String[locations.size()];
        int i=0;
        for(String s: locations){
            array[i++] = s;
        }

        return array;

    }
}
