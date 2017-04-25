package ro.stery.loginapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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
        if(pref.getString(Contract.Preferences.AUTH_HASH, null) != null)
        {
            goToProfileScreen();
            return;
        }

        password = (EditText) findViewById(R.id.password);
        username = (EditText) findViewById(R.id.username);
        findViewById(R.id.layout).setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                closeKeyboard(v);
                return false;
            }
        });

        Button loginButton = (Button) findViewById(R.id.login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard(v);
                performLogin(username.getText().toString(), password.getText().toString());
            }
        });

    }

    private void closeKeyboard(View view) {
        if(view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void goToProfileScreen() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        String username = preferences.getString(Contract.Preferences.USERNAME, null);
        Toast.makeText(LoginActivity.this, "Welcome, " + username, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
        finish();
    }

    private void performLogin(final String username, String password) {
        final String authHash = Credentials.basic(username, password);

        Call<LoginData> callable = GitHub.Service.get().checkAuth(Credentials.basic(username, password));

        callable.enqueue(new Callback<LoginData>() {
            @Override
            public void onResponse(Call<LoginData> call, Response<LoginData> response) {
                if(response.isSuccessful()) {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                    preferences.edit()
                            .putString(Contract.Preferences.AUTH_HASH, authHash)
                            .putString(Contract.Preferences.USERNAME, username)
                            .apply();
                    goToProfileScreen();
                } else {
                    switch(response.code()) {
                        case 401:
                            Toast.makeText(LoginActivity.this, "Invalid username or password.", Toast.LENGTH_SHORT).show();
                            break;
                        case 403:
                            Toast.makeText(LoginActivity.this, "Login limit reached.", Toast.LENGTH_SHORT).show();
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
