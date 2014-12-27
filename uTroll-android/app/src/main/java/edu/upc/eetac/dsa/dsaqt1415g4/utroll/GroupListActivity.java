package edu.upc.eetac.dsa.dsaqt1415g4.utroll;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.ArrayList;

import edu.upc.eetac.dsa.dsaqt1415g4.utroll.api.AppException;
import edu.upc.eetac.dsa.dsaqt1415g4.utroll.api.Group;
import edu.upc.eetac.dsa.dsaqt1415g4.utroll.api.GroupCollection;
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

//        Authenticator.setDefault(new Authenticator() {
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication(username, password //Esto estaba mal en los gists
//                        .toCharArray());
//            }
//        });

        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("david", "david" //Esto estaba mal en los gists
                        .toCharArray());
            }
        });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_utroll_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Group group = groupsList.get(position);

        Intent intent = new Intent(this, GroupDetailActivity.class);
        intent.putExtra("url", group.getLinks().get("join").getTarget()); //URL para unirse a un grupo
        intent.putExtra("type", group.getLinks().get("join").getParameters().get("type"));
        startActivity(intent);
    }

    private void addGroups(GroupCollection groups){
        groupsList.addAll(groups.getGroups());
        adapter.notifyDataSetChanged();
    }
}
