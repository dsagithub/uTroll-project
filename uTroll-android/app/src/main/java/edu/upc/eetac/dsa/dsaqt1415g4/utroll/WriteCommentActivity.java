package edu.upc.eetac.dsa.dsaqt1415g4.utroll;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;

import edu.upc.eetac.dsa.dsaqt1415g4.utroll.api.AppException;
import edu.upc.eetac.dsa.dsaqt1415g4.utroll.api.Comment;
import edu.upc.eetac.dsa.dsaqt1415g4.utroll.api.uTrollAPI;

public class WriteCommentActivity extends Activity {
    private final static String TAG = WriteCommentActivity.class.getName();
    String urlBook = null;

    private class PostCommentTask extends AsyncTask<String, Void, Comment> {
        private ProgressDialog pd;

        @Override
        protected Comment doInBackground(String... params) {
            Comment comment = null;
            try {
                comment = uTrollAPI.getInstance(WriteCommentActivity.this).createComment(params[0]);

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
            pd = new ProgressDialog(WriteCommentActivity.this);

            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_comment_layout);
    }

    public void cancel(View v) { //Como se espera un resultado, al cancelar se devuelve CANCELED
        setResult(RESULT_CANCELED);
        finish();
    }

    public void post(View v) {
        EditText etContent = (EditText) findViewById(R.id.etContent);

        String content = etContent.getText().toString();

        (new PostCommentTask()).execute(content);
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

}