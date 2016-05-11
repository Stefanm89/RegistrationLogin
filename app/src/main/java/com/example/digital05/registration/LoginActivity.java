package com.example.digital05.registration;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String LOGIN_URL = "http://nekoeime.esy.es/volleyLogin.php";

    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    //If server response is equal to this, means login is successful
    public static final String LOGIN_SUCCESS = "success";
    //Name for my shared preferences
    public static final String SHARED_PREF_NAME = "myloginapp";
    //To store the email of current logged in user
    public static final String USERNAME_SHARED_PREF = "username";
    //To store the boolean in sharedpreferences to track if the user is loggedin or not
    public static final String LOGGEDIN_SHARED_PREF = "loggedin";

    private EditText editTextUsername;
    private EditText editTextPassword;
    private TextView createUserTextView;
    private CheckBox showPassword;

    private Button buttonLogin;

    private String username;
    private String password;
    private boolean loggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        createUserTextView = (TextView) findViewById(R.id.createUserTextView);
        createUserTextView.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        createUserTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(this);

        showPassword = (CheckBox) findViewById(R.id.show_password);
        showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked){
                    editTextPassword.setInputType(129);
                } else {
                    editTextPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        loggedIn = sharedPreferences.getBoolean(LOGGEDIN_SHARED_PREF, false);

        if (loggedIn) {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
        }
    }

    private void userLogin() {
        username = editTextUsername.getText().toString().trim();
        password = editTextPassword.getText().toString().trim();

        if (!validate()){
            onLoginFailed();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.trim().equals(LOGIN_SUCCESS)) {
                    //openProfile();
                    SharedPreferences sharedPreferences = LoginActivity.this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(LOGGEDIN_SHARED_PREF, true);
                    editor.putString(USERNAME_SHARED_PREF, username);
                    editor.commit();

                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    intent.putExtra(KEY_USERNAME, username);
                    startActivity(intent);

                } else {
                    Toast.makeText(LoginActivity.this, response, Toast.LENGTH_LONG).show();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put(KEY_USERNAME, username);
                map.put(KEY_PASSWORD, password);
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

   /* private void openProfile() {
        Intent i = new Intent(LoginActivity.this, HomeActivity.class);
        i.putExtra(KEY_USERNAME, username);
        startActivity(i);
    }*/

    @Override
    public void onClick(View v) {
        userLogin();
    }

    public boolean validate(){
        boolean valid = true;

        String password = editTextPassword.getText().toString();

        if (password.isEmpty() || password.length() < 4 || password.length() > 10){
            editTextPassword.setError("Password should be between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            editTextPassword.setError(null);
        }
        return valid;
    }

    public void onLoginFailed(){
        Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_LONG).show();
        buttonLogin.setEnabled(true);
    }
}
