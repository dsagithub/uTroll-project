package edu.upc.eetac.dsa.dsaqt1415g4.utroll;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import edu.upc.eetac.dsa.dsaqt1415g4.utroll.api.AppException;
import edu.upc.eetac.dsa.dsaqt1415g4.utroll.api.Comment;
import edu.upc.eetac.dsa.dsaqt1415g4.utroll.api.User;
import edu.upc.eetac.dsa.dsaqt1415g4.utroll.api.UserCollection;
import edu.upc.eetac.dsa.dsaqt1415g4.utroll.api.uTrollAPI;
import edu.upc.eetac.dsa.dsaqt1415g4.utroll.api.uTrollRootAPI;

public class WriteCommentTrollActivity extends Activity {
    private final static String TAG = WriteCommentTrollActivity.class.getName();
    String urlBook = null;

    private class PostCommentTask extends AsyncTask<String, Void, Comment> {
        private ProgressDialog pd;

        @Override
        protected Comment doInBackground(String... params) {
            Comment comment = null;
            try {
                comment = uTrollAPI.getInstance(WriteCommentTrollActivity.this).createComment(params[0], params[1]);

            } catch (AppException e) {
                Log.e(TAG, e.getMessage(), e);
            }
            return comment;
        }

        @Override
        protected void onPostExecute(Comment result) {
            showComments(result);
            if (pd != null) {
                pd.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(WriteCommentTrollActivity.this);

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
                users = uTrollAPI.getInstance(WriteCommentTrollActivity.this)
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
            pd = new ProgressDialog(WriteCommentTrollActivity.this);
            pd.setTitle("Searching...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_comment_troll_layout);

        String urlGroup = (String) getIntent().getExtras().get("url");
        (new FillSpinnerTask()).execute(urlGroup);
    }

    public void cancel(View v) { //Como se espera un resultado, al cancelar se devuelve CANCELED
        setResult(RESULT_CANCELED);
        finish();
    }

    public void post(View v) {
        EditText etTrollContent = (EditText) findViewById(R.id.etTrollContent);
        String content = etTrollContent.getText().toString();

        Spinner spinner = (Spinner) findViewById(R.id.SpinnerUsers);
        int n = (spinner.getSelectedItem().toString().indexOf("(") - 1);
        String username = spinner.getSelectedItem().toString().substring(0, n);

        (new PostCommentTask()).execute(content, username);
    }

    private void showComments(Comment result) {
        String json = new Gson().toJson(result); //Para que entienda el Gson hay que añadir una dependencia: com.google.code
        Bundle data = new Bundle();
        data.putString("json-comment", json);
        Intent intent = new Intent();
        intent.putExtras(data);
        setResult(RESULT_OK, intent);
        finish();
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
        _EmpSpinner = (Spinner) findViewById(R.id.SpinnerUsers);

        // Attaching data adapter to spinner
        _EmpSpinner.setAdapter(dataAdapter);
    }

}