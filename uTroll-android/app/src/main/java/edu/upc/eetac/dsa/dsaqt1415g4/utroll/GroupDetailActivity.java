package edu.upc.eetac.dsa.dsaqt1415g4.utroll;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;

import java.util.ArrayList;

import edu.upc.eetac.dsa.dsaqt1415g4.utroll.api.AppException;
import edu.upc.eetac.dsa.dsaqt1415g4.utroll.api.Group;
import edu.upc.eetac.dsa.dsaqt1415g4.utroll.api.User;
import edu.upc.eetac.dsa.dsaqt1415g4.utroll.api.UserCollection;
import edu.upc.eetac.dsa.dsaqt1415g4.utroll.api.uTrollAPI;

public class GroupDetailActivity extends ListActivity {
    private final static String TAG = GroupDetailActivity.class.getName();
    String urlGroup = null;
    String contentType = null;
    String urlGetUsers = null;

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

    private class FetchUsersTask extends
            AsyncTask<String, Void, UserCollection> {
        private ProgressDialog pd;

        @Override
        protected UserCollection doInBackground(String... params) {
            UserCollection users = null;
            try {
                users = uTrollAPI.getInstance(GroupDetailActivity.this).getUsersInGroup(params[0]);
            } catch (AppException e) {
                e.printStackTrace();
            }
            return users;
        }

        @Override
        protected void onPostExecute(UserCollection result) {
            addUsers(result);
            if (pd != null) {
                pd.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(GroupDetailActivity.this);
            pd.setTitle("Searching...");
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
        urlGetUsers = (String) getIntent().getExtras().get("url-users");

        usersList = new ArrayList<User>();
        adapter = new GroupDetailAdapter(this, usersList);
        setListAdapter(adapter);

        (new FetchUsersTask()).execute(urlGetUsers);
    }

    public void joinGroup(View v) {
        (new joinGroupTask()).execute(urlGroup, contentType);
    }

    private GroupDetailAdapter adapter;
    private ArrayList<User> usersList;

    private void addUsers(UserCollection users){
        usersList.addAll(users.getUsers());
        adapter.notifyDataSetChanged();
    }

}