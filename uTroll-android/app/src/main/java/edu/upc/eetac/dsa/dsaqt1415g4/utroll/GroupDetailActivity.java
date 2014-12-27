package edu.upc.eetac.dsa.dsaqt1415g4.utroll;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;

import edu.upc.eetac.dsa.dsaqt1415g4.utroll.api.AppException;
import edu.upc.eetac.dsa.dsaqt1415g4.utroll.api.Group;
import edu.upc.eetac.dsa.dsaqt1415g4.utroll.api.uTrollAPI;

public class GroupDetailActivity extends Activity {
    private final static String TAG = GroupDetailActivity.class.getName();
    String urlGroup = null;
    String contentType = null;

    private class joinGroupTask extends AsyncTask<String, Void, String> {
        private ProgressDialog pd;
        Group group = null;

        @Override
        protected String doInBackground(String... params) {
            try {
                uTrollAPI.getInstance(GroupDetailActivity.this).joinGroup(urlGroup, contentType);
            } catch (AppException e) {
                Log.e(TAG, e.getMessage(), e);
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            finish();
            if (pd != null) {
                pd.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(GroupDetailActivity.this);

            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        urlGroup = (String) getIntent().getExtras().get("url");
        contentType = (String) getIntent().getExtras().get("type");
    }

    public void joinGroup(View v) {
        (new joinGroupTask()).execute(urlGroup, contentType);
    }

}