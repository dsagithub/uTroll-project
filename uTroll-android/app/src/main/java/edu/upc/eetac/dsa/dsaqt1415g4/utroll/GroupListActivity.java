package edu.upc.eetac.dsa.dsaqt1415g4.utroll;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.ArrayList;

import edu.upc.eetac.dsa.dsaqt1415g4.utroll.api.AppException;
import edu.upc.eetac.dsa.dsaqt1415g4.utroll.api.Group;
import edu.upc.eetac.dsa.dsaqt1415g4.utroll.api.GroupCollection;
import edu.upc.eetac.dsa.dsaqt1415g4.utroll.api.User;
import edu.upc.eetac.dsa.dsaqt1415g4.utroll.api.uTrollAPI;


public class GroupListActivity extends ListActivity {
    private final static String TAG = GroupListActivity.class.toString();

    private GroupAdapter adapter;
    private ArrayList<Group> groupsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_groups_list);

        groupsList = new ArrayList<Group>();
        adapter = new GroupAdapter(this, groupsList);
        setListAdapter(adapter);

        SharedPreferences prefs = getSharedPreferences("uTroll-profile",
                Context.MODE_PRIVATE);
        final String username = prefs.getString("username", null);
        final String password = prefs.getString("password", null);

        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password //Esto estaba mal en los gists
                        .toCharArray());
            }
        });

//        Authenticator.setDefault(new Authenticator() {
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication("david", "david" //Esto estaba mal en los gists
//                        .toCharArray());
//            }
//        });

        (new checkUserGroupTask()).execute();
        (new FetchGroupsTask()).execute();
    }

    private class FetchGroupsTask extends
            AsyncTask<Void, Void, GroupCollection> {
        private ProgressDialog pd;

        @Override
        protected GroupCollection doInBackground(Void... params) {
            GroupCollection groups = null;
            try {
                groups = uTrollAPI.getInstance(GroupListActivity.this)
                        .getGroups();
            } catch (AppException e) {
                e.printStackTrace();
            }
            return groups;
        }

        @Override
        protected void onPostExecute(GroupCollection result) {
            addGroups(result);
            if (pd != null) {
                pd.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(GroupListActivity.this);
            pd.setTitle("Searching...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }

    }

    private class checkUserGroupTask extends
            AsyncTask<Void, Void, User> {
        private ProgressDialog pd;

        @Override
        protected User doInBackground(Void... params) {
            User user = null;
            try {
                user = uTrollAPI.getInstance(GroupListActivity.this).getUser((String) getIntent().getExtras().get("user"));
            } catch (AppException e) {
                e.printStackTrace();
            }
            return user;
        }

        @Override
        protected void onPostExecute(User result) {
            TextView tv = (TextView) findViewById(R.id.tvGroupListGroupid);
            tv.setText(Integer.toString(result.getGroupid()));
            if (result.getGroupid() != 0)
                    invalidateOptionsMenu();

            TextView tvPoints = (TextView) findViewById(R.id.tvGroupListPoints);
            tvPoints.setText(Integer.toString(result.getPoints()));

            if (pd != null) {
                pd.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(GroupListActivity.this);
            pd.setTitle("Searching...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }

    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        TextView tv = (TextView) findViewById(R.id.tvGroupListGroupid);
        int n = Integer.parseInt(tv.getText().toString());
        if (n != 0)
            menu.getItem(1).setEnabled(false);
        return true;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_groups, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refreshGroupsMenuItem:
                Bundle tempBundle = new Bundle();
                onCreate(tempBundle);
                return true;
            case R.id.createGroupMenuItem:
                Intent intent = new Intent(this, CreateGroupActivity.class);
                intent.putExtra("user", (String) getIntent().getExtras().get("user"));
                startActivityForResult(intent, WRITE_ACTIVITY);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Método para que se visualice el nuevo grupo
    private final static int WRITE_ACTIVITY = 0;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case WRITE_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    Bundle res = data.getExtras();
                    String jsonGroup = res.getString("json-group");
                    Group group = new Gson().fromJson(jsonGroup, Group.class);
                    groupsList.add(0, group);
                    adapter.notifyDataSetChanged();
                }
                break;
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Group group = groupsList.get(position);

        Intent intent = new Intent(this, GroupDetailActivity.class);
        intent.putExtra("user", (String) getIntent().getExtras().get("user"));
        intent.putExtra("url", group.getLinks().get("join").getTarget()); //URL para unirse a un grupo
        intent.putExtra("type", group.getLinks().get("join").getParameters().get("type"));
        intent.putExtra("url-update", group.getLinks().get("update").getTarget()); //URL para cambiar el estado de un grupo
        intent.putExtra("type-update", group.getLinks().get("update").getParameters().get("type"));
        intent.putExtra("url-group", group.getLinks().get("self").getTarget()); //URL del grupo
        intent.putExtra("url-users", group.getLinks().get("users").getTarget());
        startActivity(intent);
    }

    private void addGroups(GroupCollection groups){
        groupsList.addAll(groups.getGroups());
        adapter.notifyDataSetChanged();
    }
}
