package hypertrack.i0.sfmovies;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.StringTokenizer;

/**
 * Created by anuj on 5/7/15.
 */
public class Fetchsuggestion extends AsyncTask<String,Void,String[]> {

    MainActivityFragment mainActivityFragment;

    public Fetchsuggestion(MainActivityFragment f)
    {
        this.mainActivityFragment=f;
    }

    String LOG_TAG = FetchLocation.class.getSimpleName();

    @Override
    protected String[] doInBackground(String... Title) {


        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String titlejson = null;

        try {

            String mtitle="";
            StringTokenizer str = new StringTokenizer(Title[0]);
            //str.nextElement();
            if(str.countTokens()>1) {
                int count =str.countTokens();
                while (count>1) {
                    mtitle += str.nextElement() + "%20";
                    count--;
                }
            }
            else
            {
                mtitle=Title[0];
            }
            Log.v(LOG_TAG, mtitle);

            URL url = new URL("http://52.25.133.178:8080/HyperTrack/recommend/"+mtitle);
            Log.v(LOG_TAG,url.toString());
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream =urlConnection.getInputStream();
            StringBuffer  buffer = new StringBuffer();
            if(inputStream==null)
            {
                titlejson=null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;

            while ((line = reader.readLine()) != null) {

                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                titlejson = null;
            }
            titlejson = buffer.toString();
            Log.v(LOG_TAG, "movies json string" + titlejson);

        } catch (Exception e) {
            Log.e("PlaceholderFragment", "Error ", e);
            titlejson = null;
        }

        try
        {
            String [] suggestions = getsuggestions(titlejson);
            for(String s:suggestions) {
                Log.v(LOG_TAG, s);
            }
            return suggestions;
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return null;
    }

    private String[] getsuggestions(String titlejson) throws JSONException
    {

        final String Movie = "data";
        JSONObject titleJson = new JSONObject(titlejson);
        JSONArray titlesArray = titleJson.getJSONArray(Movie);

        String[] arr =new String[titlesArray.length()];
        for(int i=0;i<titlesArray.length();i++)
            arr[i]=titlesArray.getString(i);

        return arr;

    }

    @Override
    protected  void onPostExecute(String[] result)
    {
        mainActivityFragment.updatesuggestion(result);

    }
}
