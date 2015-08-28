package coppermobile.googlesearch;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class GeocodeResponse {
    private ArrayList<AddressResult> results;
    private String status;
    private boolean isSuccess;
    private String errorMessage;

    public GeocodeResponse(String jsonStr,int maxResultCount){
        parseResult(jsonStr,maxResultCount);
    }

    public GeocodeResponse(String jsonStr) {
        parseResult(jsonStr,GoogleSearchAsync.MAX_RESULT_COUNT);
    }

    private void parseResult(String jsonStr,int maxResultCount) {

        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            JSONArray jsonResultArray = jsonObj.getJSONArray("results");
            this.status = jsonObj.getString("status");
            if (this.status.equals("OK")) {
                results = new ArrayList<AddressResult>();
                for (int i = 0; i < jsonResultArray.length(); i++) {
                    JSONObject resultObj = jsonResultArray.getJSONObject(i);
                    try {
                        AddressResult addResult = new AddressResult(resultObj);
                        if (addResult.isValid()) {
                            results.add(addResult);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (results.size()==maxResultCount) break;
                }
                this.isSuccess = true;
            } else {
                this.isSuccess = false;
            }
        } catch (Exception e) {
            this.status = "FAIL";
            this.isSuccess = false;
            this.errorMessage = e.getLocalizedMessage();
            e.printStackTrace();
        }
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public String getErrorMessage() {
        return "Unknown error occurred. Please try again later.";//errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ArrayList<AddressResult> getResults() {
        return results;
    }

    public String getStatus() {
        return status;
    }

}
