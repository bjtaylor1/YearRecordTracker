package com.bjt.yearrecordtracker.app;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;


public class MainActivity extends Activity implements UpdateDataItemResults {

    private ListView data_list;
    private ArrayAdapter<DataItemResult> list_adapter;
    private boolean refreshing;
    private MenuItem refreshButton;
    private static final String CACHED_DATA = "cached_data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        data_list = (ListView) findViewById(R.id.data_list);
        list_adapter = new ArrayAdapter<DataItemResult>(this, android.R.layout.simple_list_item_1) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater layoutInflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View rowView = layoutInflater.inflate(R.layout.left_right_text, parent, false);
                TextView left = (TextView) rowView.findViewById(R.id.left_text_main);
                TextView right = (TextView)rowView.findViewById(R.id.right_text);
                DataItemResult dataItemResult = this.getItem(position);
                left.setText(dataItemResult.getKey());
                right.setText(dataItemResult.getResult());
                View rule = rowView.findViewById(R.id.rule);
                if(dataItemResult.getResult() == null || dataItemResult.getKey().equals("Refreshed at")) {
                    rule.setVisibility(View.VISIBLE);
                    left.setTypeface(Typeface.DEFAULT_BOLD);
                } else {
                    rule.setVisibility(View.GONE);

                }
                return rowView;
            }
        };
        data_list.setAdapter(list_adapter);

        final File cachedDataFile = getFileStreamPath(CACHED_DATA);
        if(cachedDataFile != null && cachedDataFile.exists()) {
            FileInputStream fileInputStream = null;
            ObjectInputStream objectInputStream = null;
            try {
                fileInputStream = openFileInput(CACHED_DATA);
                objectInputStream = new ObjectInputStream(fileInputStream);
                List<DataItemResult> results = (List<DataItemResult>) objectInputStream.readObject();
                updateDataItemResults(results);
            } catch (Exception e) {
                Log.w(e.getClass().getName(), e);
            } finally {
                Util.closeQuietly(fileInputStream, objectInputStream);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        refreshButton = menu.findItem(R.id.refresh);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.refresh) {
            doRefresh();
        }
        return super.onOptionsItemSelected(item);
    }

    private void doRefresh() {
        refreshButton.setEnabled(false);
        GetDataTask getDataTask = new GetDataTask(this, this);
        try {
            getDataTask.execute(DataItemDefinitions.ALL);
        } catch (Exception e) {
            Toast.makeText(this, R.string.getdatafailed, Toast.LENGTH_LONG).show();
        }
        finally {
            refreshButton.setEnabled(true);
        }
    }

    @Override
    public void updateDataItemResults(List<DataItemResult> dataItemResults) {
        saveResults(dataItemResults);
        setResults(dataItemResults);
    }

    private void saveResults(List<DataItemResult> dataItemResults) {
        FileOutputStream fileOutputStream = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            fileOutputStream = openFileOutput(CACHED_DATA, MODE_PRIVATE);
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(dataItemResults);
        } catch (Exception e) {
            Log.w(e.getClass().getName(), e);
        } finally {
            Util.closeQuietly(fileOutputStream, objectOutputStream);
        }
    }

    private void setResults(List<DataItemResult> dataItemResults) {
        list_adapter.clear();
        for(DataItemResult d : dataItemResults) {
            list_adapter.add(d);
        }

        list_adapter.notifyDataSetChanged();

    }
}
