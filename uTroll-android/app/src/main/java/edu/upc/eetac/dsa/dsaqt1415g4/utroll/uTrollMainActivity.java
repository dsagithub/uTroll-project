package edu.upc.eetac.dsa.dsaqt1415g4.utroll;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import edu.upc.eetac.dsa.dsaqt1415g4.utroll.api.uTrollAPI;
import edu.upc.eetac.dsa.dsaqt1415g4.utroll.api.uTrollRootAPI;


public class uTrollMainActivity extends ListActivity {
    private final static String TAG = uTrollMainActivity.class.toString();
    User user = null;

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
            pd.setTitle("Searching...");
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
        protected void onPostExecute(User loginOK) {
            if (pd != null) {
                pd.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(uTrollMainActivity.this);
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
        commentsList.addAll(comments.getComments());
        adapter.notifyDataSetChanged();
    }
}
