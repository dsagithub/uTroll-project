package edu.upc.eetac.dsa.dsaqt1415g4.utroll;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.ArrayList;

import edu.upc.eetac.dsa.dsaqt1415g4.utroll.api.AppException;
import edu.upc.eetac.dsa.dsaqt1415g4.utroll.api.User;
import edu.upc.eetac.dsa.dsaqt1415g4.utroll.api.UserCollection;
import edu.upc.eetac.dsa.dsaqt1415g4.utroll.api.uTrollAPI;

public class UserSearchActivity extends ListActivity {
    private final static String TAG = uTrollMainActivity.class.toString();
    private ArrayList<User> usersList;
    private UserAdapter adapter;

    private class FetchUsersTask extends
            AsyncTask<Void, Void, UserCollection> {
        private ProgressDialog pd;

        @Override
        protected UserCollection doInBackground(Void... params) {
            UserCollection users = null;
            try {
                EditText et = (EditText) findViewById(R.id.inputUser);
                String username = et.getText().toString();
                users = uTrollAPI.getInstance(UserSearchActivity.this)
                        .getUsersByUsername(username);
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
            pd = new ProgressDialog(UserSearchActivity.this);
            pd.setTitle("Searching...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_users);

        usersList = new ArrayList<User>();
        adapter = new UserAdapter(this, usersList);
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
    }

    public void searchUsers(View v) {
        (new FetchUsersTask()).execute();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        User user = usersList.get(position);

        SharedPreferences prefs = getSharedPreferences("uTroll-profile",
                Context.MODE_PRIVATE);
        final String username = prefs.getString("username", null);

        Intent intent = new Intent(this, UserDetailActivity.class);
        intent.putExtra("url", user.getLinks().get("self").getTarget());
        intent.putExtra("username", username);
        startActivity(intent);
    }

    private void addUsers(UserCollection users){
        usersList.clear();
        usersList.addAll(users.getUsers());
        adapter.notifyDataSetChanged();
    }
}