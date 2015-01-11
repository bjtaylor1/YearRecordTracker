package com.bjt.yearrecordtracker.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ben on 11/01/15.
 */
public class GetDataTask extends AsyncTask<DataItem, Void, List<DataItemResult>> {

    private final ProgressDialog progressDialog;
    private final Context activity;
    private final UpdateDataItemResults updateDataItemResults;

    public GetDataTask(Context activity, UpdateDataItemResults updateDataItemResults) {
        this.activity = activity;
        this.updateDataItemResults = updateDataItemResults;
        progressDialog = new ProgressDialog(activity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.setMessage(activity.getResources().getString(R.string.refreshing));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

    }

    @Override
    protected void onPostExecute(List<DataItemResult> dataItemResults) {
        super.onPostExecute(dataItemResults);
        updateDataItemResults.updateDataItemResults(dataItemResults);
        progressDialog.dismiss();
    }

    @Override
    protected List<DataItemResult> doInBackground(DataItem... dataItems) {
        List<DataItemResult> results = new ArrayList<DataItemResult>();

        final DataItemResult refreshedAtResult = new DataItemResult("Refreshed at");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM HH:mm");
        refreshedAtResult.setResult(simpleDateFormat.format(new Date()));
        results.add(refreshedAtResult);

        for (DataItem dataItem : dataItems) {
            DataItemResult dataItemResult = new DataItemResult(dataItem.getKey());
            if (dataItem.getUrl() != null && dataItem.getParser() != null) {
                try {
                    Document doc = Jsoup.connect(dataItem.getUrl()).get();
                    dataItemResult.setResult(dataItem.getParser().getResult(doc));
                } catch (Exception e) {
                    dataItemResult.setResult("(errored)");
                }
            }
            results.add(dataItemResult);
        }

        return results;
    }

}
