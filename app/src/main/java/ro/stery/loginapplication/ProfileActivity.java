package ro.stery.loginapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setTitle("GitHub - " + getIntent().getStringExtra("username"));
        //getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        //getSupportActionBar().setIcon(R.drawable.github_logo); //Centreaza logo
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_git_logo);
//        getSupportActionBar().setLogo(R.drawable.github_logo);
//        getSupportActionBar().setDisplayUseLogoEnabled(true);
//        getSupportActionBar().


        //Toast.makeText(ProfileActivity.this, "Welcome, " + getIntent().getStringExtra("username"), Toast.LENGTH_SHORT).show();
        TextView username = (TextView) findViewById(R.id.user);
        username.setText(getIntent().getStringExtra("username"));

        setDate(R.string.date1);
        setDate(R.string.date2);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.account, menu);

        return true;
    }

    public void setDate(int resID) {
        TextView textView = (TextView) findViewById(resID);
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy");
        SpannableString result = new SpannableString(dateFormat.format(new Date()));
        textView.setText(result);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId())
        {
            case R.id.logout:
                Toast.makeText(ProfileActivity.this, "Log out succesful", Toast.LENGTH_SHORT).show();
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
                pref.edit().putBoolean("logged_in", false).apply();
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }

        return true;
    }
}
