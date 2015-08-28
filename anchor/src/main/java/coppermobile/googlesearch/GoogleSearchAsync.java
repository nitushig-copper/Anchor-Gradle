package coppermobile.googlesearch;

import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;

public class GoogleSearchAsync extends AsyncTask<String, Void, GeocodeResponse> {
	//deprceted
	//private final String googleSearchUrl = "https://maps-api-ssl.google.com/maps/api/geocode/json?sensor=false&address=";

    public static final int MAX_RESULT_COUNT = 10000;

	private final String googleSearchUrl = "http://maps.googleapis.com/maps/api/geocode/json?sensor=true&";
	private GoogleSearchListener googleSearchListener = null;
    private int mMaxResultCount = MAX_RESULT_COUNT;

	/**
	 * only one mFsRequest can be run at a time of this asynctask if another mFsRequest raise then previous mFsRequest will be cancel.
	 * */
	public GoogleSearchAsync(GoogleSearchListener googleSearchListener) {
		this.googleSearchListener = googleSearchListener;
	}

    public void setGoogleSearchListener(GoogleSearchListener googleSearchListener) {
        this.googleSearchListener = googleSearchListener;
    }

    public void setMaxResultCount(int mMaxResultCount) {
        this.mMaxResultCount = mMaxResultCount;
    }

    @Override
	protected GeocodeResponse doInBackground(String... params) {

        String searchQuery = params[0];
        if (searchQuery.matches("[0-9].[0-9],[0-9].[0-9]")) {
            return searchByLatLng(searchQuery);
        } else {
            return searchByAddress(params[0]);
        }
    }

    @Override
    protected void onPostExecute(GeocodeResponse result) {
        super.onPostExecute(result);
        if (googleSearchListener != null&&!isCancelled()) {
            googleSearchListener.searchedResult(result);
        }
    }

    public GeocodeResponse searchByAddress(String searchQuery) {
        String latLngUrl = googleSearchUrl + "address=";
        return lookUp(latLngUrl, searchQuery);
    }

    public GeocodeResponse searchByLatLng(String latLng) {
        String latLngUrl = googleSearchUrl + "latlng=";
        return lookUp(latLngUrl, latLng);
    }

    private GeocodeResponse lookUp(String url, String query) {
        GeocodeResponse responce = null;
        try {
            String urlStr = url + URLEncoder.encode(query, "UTF-8");

            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(new HttpGet(urlStr));
            String result = EntityUtils.toString(response.getEntity());
            responce = new GeocodeResponse(result,mMaxResultCount);

        } catch (Exception e) {
            e.printStackTrace();
            responce = new GeocodeResponse("{results: [ ],status:\"ZERO_RESULTS\"}");
            responce.setErrorMessage(e.getLocalizedMessage());
            responce.setSuccess(false);
        }
        return responce;
    }
}
