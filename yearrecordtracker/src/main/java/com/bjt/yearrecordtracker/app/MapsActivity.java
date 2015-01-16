package com.bjt.yearrecordtracker.app;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private MenuItem refreshButton;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        progressDialog = new ProgressDialog(this);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.maps, menu);
        refreshButton = menu.findItem(R.id.refresh);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.refresh) {
            setUpMap();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Sets up the maps if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the maps has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the maps.
        if (mMap == null) {
            // Try to obtain the maps from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the maps.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        new GetMapDataTask().execute();

    }

    @Override
    protected void onDestroy() {
        progressDialog.dismiss();
        super.onDestroy();

    }
    String lastRefreshDate;

    class GetMapDataTask extends AsyncTask<Void, String, GetMapDataTask.SpotResult> {

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage(MapsActivity.this.getResources().getString(R.string.refreshing));
            progressDialog.show();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            progressDialog.setMessage(values[0]);
        }

        Polyline polyline = null;
        Marker marker = null;
        @Override
        protected void onPostExecute(SpotResult result) {

            progressDialog.hide();
            if(result == null) {
                Toast.makeText(MapsActivity.this, R.string.error_refreshing, Toast.LENGTH_SHORT).show();
            }else if (result.isEmpty()) {
                Toast.makeText(MapsActivity.this, R.string.no_change, Toast.LENGTH_SHORT).show();
                //no update required
            } else if (result.getLatLngs().isEmpty()) {
                Toast.makeText(MapsActivity.this, R.string.error_nodata,Toast.LENGTH_SHORT).show();
            } else {
                List<LatLng> latLngs = result.getLatLngs();
                if (polyline == null) {
                    PolylineOptions polylineOptions = new PolylineOptions()
                            .color(getResources().getColor(R.color.track_color))
                            .width(8)
                            .addAll(latLngs);
                    polyline = mMap.addPolyline(polylineOptions);
                } else {
                    polyline.setPoints(latLngs);
                }
                LatLng location = latLngs.get(latLngs.size() -1);

                if(marker == null) {
                    marker = mMap.addMarker(new MarkerOptions().position(location).draggable(false));
                } else{
                    marker.setPosition(location);
                }

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(location)
                        .zoom(13).build();
                final CameraUpdate update = CameraUpdateFactory.newCameraPosition(cameraPosition);
                mMap.moveCamera(update);

                if(result.getTime() != null && !result.getTime().isEmpty()) {
                    setTitle("@ " + result.getTime());
                }

            }
        }

        Pattern pattern = Pattern.compile(".*google.maps.LatLng\\(([-\\d\\.]+),([-\\d\\.]+)\\).*");
        Pattern datePattern = Pattern.compile(".*(\\d\\d:\\d\\d:\\d\\d [AP]M).*");

        class SpotResult {
            private final boolean isEmpty;
            private List<LatLng> latLngs;
            private String time;

            SpotResult(List<LatLng> latLngs, String time) {
                this.latLngs = latLngs;
                this.time = time;
                isEmpty = false;
            }

            public SpotResult(){
                isEmpty = true;
            }

            public List<LatLng> getLatLngs() {
                return latLngs;
            }

            public String getTime() {
                return time;
            }

            public boolean isEmpty() {
                return isEmpty;
            }
        }


        @Override
        protected SpotResult doInBackground(Void... args) {
            String url = getIntent().getStringExtra("url");
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            if(lastRefreshDate != null) {
                httpGet.setHeader("If-Modified-Since", lastRefreshDate);
            }

            InputStream inputStream = null;
            InputStreamReader inputStreamReader = null;
            BufferedReader bufferedReader = null;

            try {
                final HttpResponse response = httpClient.execute(httpGet);

                int code = response.getStatusLine().getStatusCode();
                final Header dateHeader = response.getFirstHeader("Date");
                if(dateHeader != null) lastRefreshDate = dateHeader.getValue();

                if(code == 304) {
                    return new SpotResult();
                }

                inputStream = response.getEntity().getContent();
                inputStreamReader = new InputStreamReader(inputStream);
                bufferedReader = new BufferedReader(inputStreamReader);
                String line;
                List<LatLng> latLngs = new ArrayList<LatLng>();
                String time = "";
                publishProgress("Plotting progress...");

                while((line = bufferedReader.readLine()) != null) {

                    final Matcher matcher = pattern.matcher(line.replace(" ", ""));
                    if(matcher.matches()) {
                        String latString = matcher.group(1);
                        String lonString = matcher.group(2);
                        Double lat = Double.parseDouble(latString);
                        Double lon = Double.parseDouble(lonString);
                        latLngs.add(new LatLng(lat, lon));
                    }
                    final Matcher dateMatcher = datePattern.matcher(line);
                    if(dateMatcher.matches()) {
                        time = dateMatcher.group(1);
                    }

                    if(line.contains("polypath.push")) {
                        break;
                    }

                }
                return new SpotResult(latLngs, time);
            } catch (Exception e) {
                Log.e("map", "exception refreshing map", e);
                return null;
            }
            finally {
                Util.closeQuietly(inputStreamReader, inputStream, bufferedReader);
            }
        }
    }
}
