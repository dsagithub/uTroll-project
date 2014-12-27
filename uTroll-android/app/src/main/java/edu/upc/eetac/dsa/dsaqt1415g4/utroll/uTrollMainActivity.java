package edu.upc.eetac.dsa.dsaqt1415g4.utroll;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ListView;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.ArrayList;

import edu.upc.eetac.dsa.dsaqt1415g4.utroll.api.AppException;
import edu.upc.eetac.dsa.dsaqt1415g4.utroll.api.Comment;
import edu.upc.eetac.dsa.dsaqt1415g4.utroll.api.CommentCollection;
import edu.upc.eetac.dsa.dsaqt1415g4.utroll.api.uTrollAPI;


public class uTrollMainActivity extends ListActivity {
    private final static String TAG = uTrollMainActivity.class.toString();

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

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
//        Game game = gamesList.get(position);
//        Log.d(TAG, game.getLinks().get("self").getTarget());
//
//        Intent intent = new Intent(this, GameDetailActivity.class);
//        intent.putExtra("url", game.getLinks().get("self").getTarget());
//        startActivity(intent);
    }

    private void addComments(CommentCollection comments){
        commentsList.addAll(comments.getComments());
        adapter.notifyDataSetChanged();
    }
}
