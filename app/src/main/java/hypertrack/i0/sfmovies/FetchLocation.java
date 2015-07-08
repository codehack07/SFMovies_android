package hypertrack.i0.sfmovies;

import android.content.Context;
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
public class FetchLocation  extends AsyncTask<String, Void, Void> {

    MainActivityFragment mainActivityFragment;
    ArrayList<MyMarker> mMyMarkersArray;

    public FetchLocation(MainActivityFragment f) {
        this.mainActivityFragment = f;
    }

    String LOG_TAG = FetchLocation.class.getSimpleName();

    @Override
    protected Void doInBackground(String... Title) {


        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String moviesjsondata = null;

        try {

            String mtitle = "";
            StringTokenizer str = new StringTokenizer(Title[0]);

            while (str.hasMoreElements()) {
                mtitle += str.nextElement() + "%20";
            }
            Log.v(LOG_TAG, mtitle);

            URL url = new URL(mainActivityFragment.globalurl + "movies/" + mtitle);
            Log.v(LOG_TAG, url.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                moviesjsondata = null;
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

        try {
            getlocations(moviesjsondata);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }


    @Override
    protected void onPostExecute(Void a) {
        mainActivityFragment.plotMarkers(mMyMarkersArray);
    }


    private void getlocations(String moviesjson) throws JSONException {

        final String Movie = "data";
        JSONObject moviesJson = new JSONObject(moviesjson);
        JSONArray moviesArray = moviesJson.getJSONArray(Movie);
        double lat, lng;
        ArrayList<String> locations = new ArrayList<String>();
        mMyMarkersArray = new ArrayList<MyMarker>();
        Context context = mainActivityFragment.context;
        Geocoder geo = new Geocoder(context, Locale.getDefault());

        for (int i = 0; i < moviesArray.length(); i++) {
            String location;
            JSONObject MovieObj = moviesArray.getJSONObject(i);
            String loc = MovieObj.getString("locations");
            try {
                List<Address> list = geo.getFromLocationName(loc, 3);
                if (list.size() > 0) {
                    Address address = list.get(0);
                    lat = address.getLatitude();
                    lng = address.getLongitude();
                    mMyMarkersArray.add(new MyMarker(loc, "icon1", lat, lng));

                    System.out.println("lat long =" + lat + " " + lng);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}