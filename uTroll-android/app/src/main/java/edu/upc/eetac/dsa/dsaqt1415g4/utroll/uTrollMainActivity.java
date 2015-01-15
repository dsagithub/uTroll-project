package edu.upc.eetac.dsa.dsaqt1415g4.utroll;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayList;

import edu.upc.eetac.dsa.dsaqt1415g4.utroll.api.AppException;
import edu.upc.eetac.dsa.dsaqt1415g4.utroll.api.Comment;
import edu.upc.eetac.dsa.dsaqt1415g4.utroll.api.CommentCollection;
import edu.upc.eetac.dsa.dsaqt1415g4.utroll.api.User;
import edu.upc.eetac.dsa.dsaqt1415g4.utroll.api.UserCollection;
import edu.upc.eetac.dsa.dsaqt1415g4.utroll.api.uTrollAPI;
import edu.upc.eetac.dsa.dsaqt1415g4.utroll.api.uTrollRootAPI;


public class uTrollMainActivity extends ListActivity {
    private final static String TAG = uTrollMainActivity.class.toString();
    User user = null;
    String urlnext;
    String urlprev;

    private CommentAdapter adapter;
    private ArrayList<Comment> commentsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_utroll_main);

        commentsList = new ArrayList<Comment>();
        adapter = new CommentAdapter(this, commentsList);
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
        (new GetUserTask()).execute(username, password);
        (new FetchCommentsTask()).execute();
        (new CheckPendingFriendsTask()).execute();
    }

    private class FetchCommentsTask extends
            AsyncTask<Void, Void, CommentCollection> {
        private ProgressDialog pd;

        @Override
        protected CommentCollection doInBackground(Void... params) {
            CommentCollection comments = null;
            try {
                comments = uTrollAPI.getInstance(uTrollMainActivity.this)
                        .getComments();
                urlnext = comments.getLinks().get("next").getTarget();
                urlprev = comments.getLinks().get("previous").getTarget();
            } catch (AppException e) {
                e.printStackTrace();
            }
            return comments;
        }

        @Override
        protected void onPostExecute(CommentCollection result) {
            addComments(result);
            if (pd != null) {
                pd.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(uTrollMainActivity.this);
            pd.setTitle("Buscando...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }

    }

    private class FetchNextCommentsTask extends
            AsyncTask<Void, Void, CommentCollection> {
        private ProgressDialog pd;

        @Override
        protected CommentCollection doInBackground(Void... params) {
            CommentCollection comments = null;
            try {
                comments = uTrollAPI.getInstance(uTrollMainActivity.this).getPrevNextComments(urlnext);

                urlnext = comments.getLinks().get("next").getTarget();
                urlprev = comments.getLinks().get("previous").getTarget();
            } catch (AppException e) {
                e.printStackTrace();
            }
            return comments;
        }

        @Override
        protected void onPostExecute(CommentCollection result) {
            addComments(result);
            if (pd != null) {
                pd.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(uTrollMainActivity.this);
            pd.setTitle("Buscando...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }

    }

    private class FetchPreviousCommentsTask extends
            AsyncTask<Void, Void, CommentCollection> {
        private ProgressDialog pd;

        @Override
        protected CommentCollection doInBackground(Void... params) {
            CommentCollection comments = null;
            try {
                comments = uTrollAPI.getInstance(uTrollMainActivity.this).getPrevNextComments(urlprev);

                urlnext = comments.getLinks().get("next").getTarget();
                urlprev = comments.getLinks().get("previous").getTarget();
            } catch (AppException e) {
                e.printStackTrace();
            }
            return comments;
        }

        @Override
        protected void onPostExecute(CommentCollection result) {
            addComments(result);
            if (pd != null) {
                pd.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(uTrollMainActivity.this);
            pd.setTitle("Buscando...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }

    }

    private class CheckPendingFriendsTask extends
            AsyncTask<Void, Void, Boolean> {
        private ProgressDialog pd;

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean pending = false;
            UserCollection users = new UserCollection();
            try {
                users = uTrollAPI.getInstance(uTrollMainActivity.this).getPendingFriends();
                if (users.getUsers().size() > 0)
                    pending = true;
            } catch (AppException e) {
                e.printStackTrace();
            }
            return pending;
        }

        @Override
        protected void onPostExecute(Boolean pending) {
            if (pending) {
                Drawable mDrawable = getResources().getDrawable(R.drawable.ic_action_person);
                mDrawable.setColorFilter(new PorterDuffColorFilter(Color.rgb(255, 0, 0), PorterDuff.Mode.MULTIPLY));
                invalidateOptionsMenu();
            } else {
                Drawable mDrawable = getResources().getDrawable(R.drawable.ic_action_person);
                mDrawable.setColorFilter(new PorterDuffColorFilter(Color.rgb(255, 255, 255), PorterDuff.Mode.MULTIPLY));
                invalidateOptionsMenu();
            }

            if (pd != null) {
                pd.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(uTrollMainActivity.this);
            pd.setTitle("Buscando...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }

    }

    private class GetUserTask extends AsyncTask<String, Void, User> {
        private ProgressDialog pd;

        @Override
        protected User doInBackground(String... params) {
            try {
                user = uTrollAPI.getInstance(uTrollMainActivity.this)
                        .checkLogin(params[0], params[1]);
                user = uTrollAPI.getInstance(uTrollMainActivity.this).getUser(user.getLinks().get("self").getTarget());
            } catch (AppException e) {
                e.printStackTrace();
            }
            return user;
        }

        @Override
        protected void onPostExecute(User user) {
            TextView tvMainGroup = (TextView) findViewById(R.id.tvMainGroup);
            TextView tvMainTroll = (TextView) findViewById(R.id.tvMainTroll);

            if (user.getGroupid() == 0)
                tvMainGroup.setText("No estás en ninǵun grupo");
            else
                tvMainGroup.setText("Estás en el grupo: " + user.getGroupid());

            if (user.isTroll())
                tvMainTroll.setText("Eres Troll");
            else
                tvMainTroll.setText("No eres Troll");

            if (pd != null) {
                pd.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(uTrollMainActivity.this);
            pd.setTitle("Buscando...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }

    }

    public void previousComments(View v) {
        (new FetchPreviousCommentsTask()).execute();
    }

    public void nextComments(View v) {
        (new FetchNextCommentsTask()).execute();
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
            case R.id.action_logout:
                SharedPreferences prefs = getSharedPreferences("uTroll-profile",
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit(); //Esto siempre se hace así -> obtener editor + clear
                editor.clear();
                editor.commit();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.action_joinGroup:
                Intent intent_joinGroup = new Intent(this, GroupListActivity.class);
                intent_joinGroup.putExtra("user", user.getLinks().get("self").getTarget());
                startActivity(intent_joinGroup);
                return true;
            case R.id.refreshMenuItem:
                Bundle tempBundle = new Bundle();
                onCreate(tempBundle);
                return true;
            case R.id.writeCommentMenuItem:
                try {
                    userIsTroll();
                } catch (AppException e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.action_searchUsers:
                Intent intent_searchUsers = new Intent(this, UserSearchActivity.class);
                startActivity(intent_searchUsers);
                return true;
            case R.id.friendshipNotificationsMenuItem:
                Intent intent_friendshipNotifications = new Intent(this, PendingFriendsActivity.class);
                startActivity(intent_friendshipNotifications);
                return true;
            case R.id.action_updateUser:
                Intent intent_updateUser = new Intent(this, UpdateUserActivity.class);
                startActivity(intent_updateUser);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void userIsTroll() throws AppException {
        if (user.isTroll()) {
            uTrollRootAPI rootAPI = null;
            String urlGroup = user.getLinks().get("group-users").getTarget();

            Intent intent = new Intent(this, WriteCommentTrollActivity.class);
            intent.putExtra("url", urlGroup);
            startActivityForResult(intent, WRITE_ACTIVITY);
        } else {
            Intent intent = new Intent(this, WriteCommentActivity.class);
            startActivityForResult(intent, WRITE_ACTIVITY);
        }
    }

    //Método para que se visualice la nueva review
    private final static int WRITE_ACTIVITY = 0; //Porque solo hay una actividad, si se lanzan varias se asignan números sucesivos
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case WRITE_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    Bundle res = data.getExtras();
                    String jsonReview = res.getString("json-comment");
                    Comment comment = new Gson().fromJson(jsonReview, Comment.class);
                    commentsList.add(0, comment);
                    adapter.notifyDataSetChanged();
                }
                break;
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Comment comment = commentsList.get(position);

        Intent intent = new Intent(this, CommentDetailActivity.class);
        intent.putExtra("url", comment.getLinks().get("self").getTarget());
        intent.putExtra("url-like", comment.getLinks().get("like").getTarget());
        intent.putExtra("url-dislike", comment.getLinks().get("dislike").getTarget());
        intent.putExtra("type", comment.getLinks().get("like").getParameters().get("type"));
        startActivity(intent);
    }

    private void addComments(CommentCollection comments) {
        commentsList.clear();
        commentsList.addAll(comments.getComments());
        adapter.notifyDataSetChanged();
    }
}
