package edu.upc.eetac.dsa.dsaqt1415g4.utroll;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import edu.upc.eetac.dsa.dsaqt1415g4.utroll.api.AppException;
import edu.upc.eetac.dsa.dsaqt1415g4.utroll.api.Group;
import edu.upc.eetac.dsa.dsaqt1415g4.utroll.api.User;
import edu.upc.eetac.dsa.dsaqt1415g4.utroll.api.UserCollection;
import edu.upc.eetac.dsa.dsaqt1415g4.utroll.api.uTrollAPI;

public class GroupDetailActivity extends ListActivity {
    private final static String TAG = GroupDetailActivity.class.getName();
    String urlJoinGroup = null;
    String contentType = null;
    String urlUpdateGroup = null;
    String contentTypeUpdate = null;
    String urlGroup = null;
    String urlGetUsers = null;
    String urlUser = null;
    User user = null;
    Group group = null;

    private class joinGroupTask extends AsyncTask<String, Void, String> {
        private ProgressDialog pd;

        @Override
        protected String doInBackground(String... params) {
            try {
                uTrollAPI.getInstance(GroupDetailActivity.this).joinGroup(urlJoinGroup, contentType);
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
                group = uTrollAPI.getInstance(GroupDetailActivity.this).getGroupByGroupid(params[2]);
                user = uTrollAPI.getInstance(GroupDetailActivity.this).getUser(params[1]);
                users = uTrollAPI.getInstance(GroupDetailActivity.this).getUsersInGroup(params[0]);
            } catch (AppException e) {
                e.printStackTrace();
            }
            return users;
        }

        @Override
        protected void onPostExecute(UserCollection result) {
            if ((user.getGroupid() == 0) && (group.getState().equals("open")) && (user.getPoints() >= group.getPrice())) {
                Button btn = (Button) findViewById(R.id.joinGroupBtn);
                btn.setEnabled(true);
            }

            if ((user.getVote().equals("none")) && (group.getState().equals("active")) && (user.getGroupid() == group.getGroupid())) {
                Button btn = (Button) findViewById(R.id.voteTrollBtn);
                btn.setEnabled(true);
            }

//            if (user.getUsername().equals(group.getCreator())) { //Si es el creador del grupo
//                if (group.getState().equals("open")) {
//                    Button btn1 = (Button) findViewById(R.id.activateGroupBtn);
//                    btn1.setEnabled(true);
//                } else if (group.getState().equals("active")) {
//                    Button btn1 = (Button) findViewById(R.id.closeGroupBtn);
//                    btn1.setEnabled(true);
//                }
//            }

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

    private class changeGroupStateTask extends AsyncTask<String, Void, String> {
        private ProgressDialog pd;

        @Override
        protected String doInBackground(String... params) {
            try {
                uTrollAPI.getInstance(GroupDetailActivity.this).changeGroupState(params[0], params[1], params[2]);
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

    private class FillSpinnerTask extends
            AsyncTask<String, Void, UserCollection> {
        private ProgressDialog pd;

        @Override
        protected UserCollection doInBackground(String... params) {
            UserCollection users = null;
            try {
                users = uTrollAPI.getInstance(GroupDetailActivity.this)
                        .getUsersInGroup(params[0]);
            } catch (AppException e) {
                e.printStackTrace();
            }
            return users;
        }

        @Override
        protected void onPostExecute(UserCollection result) {
            fillSpinner(result);
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

    private class voteTrollTask extends AsyncTask<String, Void, String> {
        private ProgressDialog pd;

        @Override
        protected String doInBackground(String... params) {
            try {
                user = uTrollAPI.getInstance(GroupDetailActivity.this).getUser(params[1]);
                if (user.getVote().equals("none")) {
                    uTrollAPI.getInstance(GroupDetailActivity.this)
                            .voteTroll(params[0]);
                } else {
                    Context context = getApplicationContext();
                    CharSequence text = "Ya has votado";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            } catch (AppException e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            finish();
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

        urlJoinGroup = (String) getIntent().getExtras().get("url");
        contentType = (String) getIntent().getExtras().get("type");
        urlUpdateGroup = (String) getIntent().getExtras().get("url-update");
        contentTypeUpdate = (String) getIntent().getExtras().get("type-update");
        urlGetUsers = (String) getIntent().getExtras().get("url-users");
        urlGroup = (String) getIntent().getExtras().get("url-group");
        urlUser = (String) getIntent().getExtras().get("user");

        usersList = new ArrayList<User>();
        adapter = new GroupDetailAdapter(this, usersList);
        setListAdapter(adapter);

        Button btn = (Button) findViewById(R.id.joinGroupBtn);
        btn.setEnabled(false);
        Button btn1 = (Button) findViewById(R.id.voteTrollBtn);
        btn1.setEnabled(false);
//        Button btn1 = (Button) findViewById(R.id.activateGroupBtn);
//        btn1.setEnabled(false);
//        Button btn2 = (Button) findViewById(R.id.closeGroupBtn);
//        btn2.setEnabled(false);

        (new FetchUsersTask()).execute(urlGetUsers, urlUser, urlGroup);
        (new FillSpinnerTask()).execute(urlGetUsers);
    }

    public void joinGroup(View v) {
        (new joinGroupTask()).execute(urlJoinGroup, contentType);
    }

    public void voteTroll(View v) {
        Spinner spinner = (Spinner) findViewById(R.id.SpinnerVoteTroll);
        int n = (spinner.getSelectedItem().toString().indexOf("(") - 1);
        String username = spinner.getSelectedItem().toString().substring(0, n);

        urlUser = (String) getIntent().getExtras().get("user");

        (new voteTrollTask()).execute(username, urlUser);
    }

    public void activateGroup(View v) {
        (new changeGroupStateTask()).execute(urlUpdateGroup, contentTypeUpdate, "active");
    }

    public void closeGroup(View v) {
        (new changeGroupStateTask()).execute(urlUpdateGroup, contentTypeUpdate, "closed");
    }

    private GroupDetailAdapter adapter;
    private ArrayList<User> usersList;

    private void addUsers(UserCollection users) {
        usersList.addAll(users.getUsers());
        adapter.notifyDataSetChanged();
    }

    private void fillSpinner(UserCollection users) {
//        List<User> usersList = new ArrayList<User>();
//        usersList.addAll(users.getUsers());
//        ArrayAdapter<User> dataAdapter = new ArrayAdapter<User>(this, android.R.layout.simple_spinner_item, usersList);

        List<String> stringList = new ArrayList<String>();
        int i = 0;
        while (i < users.getUsers().size()) {
            stringList.add(users.getUsers().get(i).getUsername() + " (" + users.getUsers().get(i).getName() + ")");
            i++;
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, stringList);

        // Drop down layout style - list view
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner _EmpSpinner = null;
        _EmpSpinner = (Spinner) findViewById(R.id.SpinnerVoteTroll);

        // Attaching data adapter to spinner
        _EmpSpinner.setAdapter(dataAdapter);
    }
}