package ro.stery.loginapplication;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ro.stery.loginapplication.model.GitHub;
import ro.stery.loginapplication.model.GithubProfile;

public class ProfileActivity extends AppCompatActivity {

    private ImageView mProfilePicture;
    private TextView mName;
    private TextView mOrganization;
    private TextView mBio;
    private TextView mLocation;
    private TextView mEmail;
    private TextView mCreated;
    private TextView mUpdated;
    private TextView mPublicRepos;
    private TextView mPrivateRepos;
    private GithubProfile mDisplayedProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setTitle("GitHub - " + getIntent().getStringExtra("username"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_git_logo);

        setDate(R.id.date1);
        setDate(R.id.date2);

        mProfilePicture = (ImageView) findViewById(R.id.avatar);
        mName = (TextView) findViewById(R.id.user);
        mOrganization = (TextView) findViewById(R.id.organization);
        mBio = (TextView) findViewById(R.id.bio);
        mLocation = (TextView) findViewById(R.id.location_bucharest);
        mEmail = (TextView) findViewById(R.id.email_address);
        mCreated = (TextView) findViewById(R.id.date1);
        mUpdated = (TextView) findViewById(R.id.date2);
        mPublicRepos = (TextView) findViewById(R.id.public_repos_no);
        mPrivateRepos = (TextView) findViewById(R.id.private_repos_no);
        findViewById(R.id.btn_create_repo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProfileActivity.this, "Oops, not yet implemented..", Toast.LENGTH_SHORT).show();
            }
        });

        fetchProfile();

    }

    private void updateUI(GithubProfile profile) {
        mDisplayedProfile = profile;

        mProfilePicture.setImageResource(R.drawable.octocat);
        mName.setText(profile.getName());
        mOrganization.setText(profile.getCompany());
        mBio.setText(profile.getBio());
        setTextUnderlined(mLocation, profile.getLocation());
        setTextUnderlined(mEmail, profile.getEmail());
        setTextUnderlined(mCreated, profile.getCreatedAt());
        setTextUnderlined(mUpdated, profile.getUpdatedAt());
        setTextUnderlined(mPublicRepos, String.valueOf(profile.getPublicRepos()));
        setTextUnderlined(mPrivateRepos, String.valueOf(profile.getOwnedPrivateRepos()));
    }

    private void setTextUnderlined(TextView textView, String text) {
        if(!TextUtils.isEmpty(text)) {
            SpannableString content = new SpannableString(text);
            content.setSpan(new UnderlineSpan(), 0, content.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            textView.setText(content);
        }
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
        result.setSpan(new UnderlineSpan(), 0, result.length(), 0);
        textView.setText(result);
    }

    static class LogoutDialogFragment extends DialogFragment {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ((ProfileActivity)getActivity()).logOut();
                        }
                    })
                    .setNegativeButton("No", null)
                    .create();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        LogoutDialogFragment logoutDialogFragment = new LogoutDialogFragment();

        switch(item.getItemId())
        {
            case R.id.logout:
                logoutDialogFragment.show(fragmentManager, "logout_dialog");
                return true;
            case R.id.repos:
                startActivity(new Intent(ProfileActivity.this, RepositoriesActivity.class));
                return true;
        }

        return false;
    }

    public void logOut() {
        Toast.makeText(ProfileActivity.this, "Log out successful", Toast.LENGTH_SHORT).show();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.edit().remove(Contract.Preferences.AUTH_HASH).apply();

        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void fetchProfile() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Call<GithubProfile> profileCall = GitHub.Service.get().getUserProfile(preferences.getString(Contract.Preferences.AUTH_HASH, null));
        profileCall.enqueue(new Callback<GithubProfile>() {
            @Override
            public void onResponse(Call<GithubProfile> call, Response<GithubProfile> response) {
                if(response.isSuccessful()) {
                    GithubProfile profile = response.body();
                    updateUI(profile);
                } else {
                    switch(response.code()) {
                        case 401:
                            Toast.makeText(ProfileActivity.this, "Invalid username or password.", Toast.LENGTH_SHORT).show();
                            break;
                        case 403:
                            Toast.makeText(ProfileActivity.this, "Forbidden/Login limit reached.", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(ProfileActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<GithubProfile> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(ProfileActivity.this, "No Internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
