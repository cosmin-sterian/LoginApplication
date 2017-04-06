package ro.stery.loginapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ro.stery.loginapplication.model.GitHub;
import ro.stery.loginapplication.model.LoginData;

public class LoginActivity extends AppCompatActivity {

    EditText password;
    EditText username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        if(pref.getBoolean("logged_in", false))
        {
            String user = pref.getString("username", null);
            Toast.makeText(LoginActivity.this, "Welcome, " + user, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
            intent.putExtra("username", user);
            startActivity(intent);
            pref.edit().putBoolean("logged_in", true).apply();
            finish();
        }

        password = (EditText) findViewById(R.id.password);
        username = (EditText) findViewById(R.id.username);

        Button loginButton = (Button) findViewById(R.id.login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin(username.getText().toString(), password.getText().toString());
                /*if(username.getText().toString().equals(""))
                {
                    Toast.makeText(LoginActivity.this, "Error: Username field can't be empty", Toast.LENGTH_SHORT).show();
                } else if(password.getText().toString().equals("Stery")) {*/
                    //Toast.makeText(LoginActivity.this, "Logging in...", Toast.LENGTH_SHORT).show();
                    /*v.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                            intent.putExtra("username", username.getText().toString());
                            startActivity(intent);
                            finish();
                        }
                    }, 3000);*/
                    /*Toast.makeText(LoginActivity.this, "Welcome, " + username.getText().toString(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                    intent.putExtra("username", username.getText().toString());
                    startActivity(intent);
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                    pref.edit().putBoolean("logged_in", true).apply();
                    pref.edit().putString("username", username.getText().toString()).apply();
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
                }*/
            }
        });

    }

    private void goToProfileScreen(String username) {
        Toast.makeText(LoginActivity.this, "Welcome, " + username, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        pref.edit().putBoolean("logged_in", true).apply();
        pref.edit().putString("username", username).apply();
        finish();
    }

    private void performLogin(final String username, String password) {
        Call<LoginData> callable = GitHub.Service.get().checkAuth(Credentials.basic(username, password));

        callable.enqueue(new Callback<LoginData>() {
            @Override
            public void onResponse(Call<LoginData> call, Response<LoginData> response) {
                if(response.isSuccessful()) {
                    goToProfileScreen(username);
                } else {
                    switch(response.code()) {
                        case 403:
                            Toast.makeText(LoginActivity.this, "Invalid username or password.", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(LoginActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginData> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(LoginActivity.this, "No Internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
