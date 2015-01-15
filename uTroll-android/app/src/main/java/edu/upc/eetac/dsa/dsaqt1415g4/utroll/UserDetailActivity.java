package edu.upc.eetac.dsa.dsaqt1415g4.utroll;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import edu.upc.eetac.dsa.dsaqt1415g4.utroll.api.AppException;
import edu.upc.eetac.dsa.dsaqt1415g4.utroll.api.FriendList;
import edu.upc.eetac.dsa.dsaqt1415g4.utroll.api.User;
import edu.upc.eetac.dsa.dsaqt1415g4.utroll.api.uTrollAPI;

public class UserDetailActivity extends Activity {
    private final static String TAG = UserDetailActivity.class.getName();
    User user = null;
    String urlReviews = null;
    String urlUser = null;
    String username = null;
    FriendList friend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        urlUser = (String) getIntent().getExtras().get("url");
        username = (String) getIntent().getExtras().get("username");

        Button friendBtn = (Button) findViewById(R.id.friendBtn);
        Button rejectFriendBtn = (Button) findViewById(R.id.rejectFriendBtn);
        friendBtn.setVisibility(View.INVISIBLE);
        rejectFriendBtn.setVisibility(View.INVISIBLE);

        (new FetchUserTask()).execute(urlUser, username);
    }

    private void loadUser(User user) {
        TextView tvDetailUsername = (TextView) findViewById(R.id.tvDetailUsername);
        TextView tvDetailFriend = (TextView) findViewById(R.id.tvDetailFriend);
//        TextView tvDetailName = (TextView) findViewById(R.id.tvDetailName);
        TextView tvDetailEmail = (TextView) findViewById(R.id.tvDetailEmail);
        TextView tvDetailAge = (TextView) findViewById(R.id.tvDetailAge);
        TextView tvDetailPoints = (TextView) findViewById(R.id.tvDetailPoints);
        TextView tvDetailPointsMax = (TextView) findViewById(R.id.tvDetailPointsMax);

        tvDetailUsername.setText(user.getUsername() + " (" + user.getName() + ")");
        tvDetailFriend.setText("Amistad: " + friend.getState());
        tvDetailEmail.setText("Email: " + user.getEmail());
        tvDetailAge.setText("Edad: " + Integer.toString(user.getAge()));
        tvDetailPoints.setText("Puntos: " + Integer.toString(user.getPoints()));
        tvDetailPointsMax.setText("Máx Puntos: " + Integer.toString(user.getPoints_max()));

        Button friendBtn = (Button) findViewById(R.id.friendBtn);
        Button rejectFriendBtn = (Button) findViewById(R.id.rejectFriendBtn);

        if (friend.getState().equals("none")) {
            friendBtn.setVisibility(View.VISIBLE);
            friendBtn.setText("Añadir amigo");
        } else if ((friend.getState().equals("pending")) && (friend.getRequest() == false)) {
            friendBtn.setVisibility(View.VISIBLE);
            friendBtn.setText("Aceptar amigo");
            rejectFriendBtn.setVisibility(View.VISIBLE);
        }
    }

    public void doFriend(View v) {
        Button friendBtn = (Button) findViewById(R.id.friendBtn);
        TextView tvDetailUsername = (TextView) findViewById(R.id.tvDetailUsername);
        String doFriend = friendBtn.getText().toString();
        String usernameString = tvDetailUsername.getText().toString().substring(0, (tvDetailUsername.getText().toString().indexOf("(") - 1));
        (new doFriendTask()).execute(usernameString, doFriend);
    }

    public void rejectFriend(View v) {
        TextView tvDetailUsername = (TextView) findViewById(R.id.tvDetailUsername);
        String usernameString = tvDetailUsername.getText().toString().substring(0, (tvDetailUsername.getText().toString().indexOf("(") - 1));
        (new rejectFriendTask()).execute(usernameString);
    }

    private class doFriendTask extends AsyncTask<String, Void, User> {
        private ProgressDialog pd;

        @Override
        protected User doInBackground(String... params) {
            User user = null;
            friend = new FriendList();
            try {
                if (params[1].equals("Añadir amigo"))
                    uTrollAPI.getInstance(UserDetailActivity.this).addFriend(params[0]);
                else if (params[1].equals("Aceptar amigo"))
                    uTrollAPI.getInstance(UserDetailActivity.this).acceptFriend(params[0]);
            } catch (AppException e) {
                Log.d(TAG, e.getMessage(), e);
            }
            return user;
        }

        @Override
        protected void onPostExecute(User result) {
            finish();
            if (pd != null) {
                pd.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(UserDetailActivity.this);
            pd.setTitle("Loading...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }

    }

    private class rejectFriendTask extends AsyncTask<String, Void, User> {
        private ProgressDialog pd;

        @Override
        protected User doInBackground(String... params) {
            User user = null;
            friend = new FriendList();
            try {
                uTrollAPI.getInstance(UserDetailActivity.this).rejectFriend(params[0]);
            } catch (AppException e) {
                Log.d(TAG, e.getMessage(), e);
            }
            return user;
        }

        @Override
        protected void onPostExecute(User result) {
            finish();
            if (pd != null) {
                pd.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(UserDetailActivity.this);
            pd.setTitle("Loading...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }

    }

    private class FetchUserTask extends AsyncTask<String, Void, User> {
        private ProgressDialog pd;

        @Override
        protected User doInBackground(String... params) {
            User user = null;
            friend = new FriendList();
            try {
                user = uTrollAPI.getInstance(UserDetailActivity.this)
                        .getUser(params[0]);
                if (user.getUsername().equals(params[1])) {
                    friend.setState("me");
                } else {
                    friend = uTrollAPI.getInstance(UserDetailActivity.this)
                            .getFriend(user.getLinks().get("friend").getTarget());
                }
            } catch (AppException e) {
                Log.d(TAG, e.getMessage(), e);
            }
            return user;
        }

        @Override
        protected void onPostExecute(User result) {
            loadUser(result);
            if (pd != null) {
                pd.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(UserDetailActivity.this);
            pd.setTitle("Loading...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }

    }
}
