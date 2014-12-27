package edu.upc.eetac.dsa.dsaqt1415g4.utroll;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

import edu.upc.eetac.dsa.dsaqt1415g4.utroll.api.AppException;
import edu.upc.eetac.dsa.dsaqt1415g4.utroll.api.CommentCollection;
import edu.upc.eetac.dsa.dsaqt1415g4.utroll.api.uTrollAPI;

public class LoginActivity extends Activity {
    private final static String TAG = LoginActivity.class.getName();
    private Boolean correctLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        requestWindowFeature(Window.FEATURE_NO_TITLE); //No enseña la barra superior

        SharedPreferences prefs = getSharedPreferences("uTroll-profile",
                Context.MODE_PRIVATE); //Sólo la aplicación puede recuperar las preferences
        String username = prefs.getString("username", null); //Recupera el usuario y contraseña almacenados
        String password = prefs.getString("password", null);

        // Uncomment the next two lines to test the application without login
        // each time
        //username = "david";
        //password = "david";

        if ((username != null) && (password != null)) { //Si usuario y contraseña no son nulos, inicia la actividad
            Intent intent = new Intent(this, uTrollMainActivity.class);
            startActivity(intent);
            finish();
        }
        setContentView(R.layout.login_layout);
    }

    public void signIn(View v) throws AppException {
        EditText etUsername = (EditText) findViewById(R.id.etUsername); //Obtener campos de texto de usuario y contraseña
        EditText etPassword = (EditText) findViewById(R.id.etPassword);

        final String username = etUsername.getText().toString(); //Obtener usuario y contraseña
        final String password = etPassword.getText().toString();

        //Se debería acceder a la API y comprobar que las credenciales son correctas

// Launch a background task to check if credentials are correct
// If correct, store username and password and start uTroll activity
// else, handle error

//        correctLogin = false;
//        correctLogin = uTrollAPI.getInstance(LoginActivity.this).isLoginOK(); //ESTO HAY QUE ARREGLARLO
//
//        if (!correctLogin)
//            throw new AppException("Login is not correct");

// I'll suppose that u/p are correct:
        SharedPreferences prefs = getSharedPreferences("uTroll-profile",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit(); //Esto siempre se hace así -> obtener editor + clear
        editor.clear();
        editor.putString("username", username);
        editor.putString("password", password);
        boolean done = editor.commit();
        if (done)
            Log.d(TAG, "preferences set");
        else
            Log.d(TAG, "preferences not set. THIS A SEVERE PROBLEM");

        startuTrollActivity();
    }

    private void startuTrollActivity() {
        Intent intent = new Intent(this, uTrollMainActivity.class);
        startActivity(intent);
        finish(); //Si no acabamos la actividad de Login, al darle al botón "back" en el móvil volvería a ella
    }

//    private class checkLoginTask extends AsyncTask<Void, Void, Boolean> {
//        private ProgressDialog pd;
//
//        @Override
//        protected Boolean doInBackground(Void... params) {
////            try {
////                correctLogin = uTrollAPI.getInstance(LoginActivity.this)
////                        .checkLogin();
////            } catch (AppException e) {
////                e.printStackTrace();
////            }
//            return correctLogin;
//        }
//
//        @Override
//        protected void onPostExecute(boolean correctLogin) {
//            if (pd != null) {
//                pd.dismiss();
//            }
//        }
//
//        @Override
//        protected void onPreExecute() {
//            pd = new ProgressDialog(LoginActivity.this);
//            pd.setTitle("Searching...");
//            pd.setCancelable(false);
//            pd.setIndeterminate(true);
//            pd.show();
//        }
//
//    }
}