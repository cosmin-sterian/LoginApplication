package ro.stery.loginapplication;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ro.stery.loginapplication.database.DbContract;
import ro.stery.loginapplication.database.GithubContentProvider;
import ro.stery.loginapplication.database.MySqlHelper;
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

    private SQLiteDatabase mDbConnection;

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

        // Establish the link to the database
        MySqlHelper mySqlHelper = new MySqlHelper(this);
        mDbConnection = mySqlHelper.getWritableDatabase();

        // Populate the UI with whatever data with have in the local database (so we don't have
        // an empty screen when fetching the profile from the network). Also, in case of no
        // internet connection, we still have something to show in the UI.
        updateUIFromDb();
        //  Finally attempt to fetch the repositories
        fetchProfile();

    }

    private void handleNetworkResponse(GithubProfile profile) {
        // Serialize the profile in a DB-relatable format
        ContentValues values = new ContentValues();
        values.put(DbContract.Profile.ID, profile.getId());
        values.put(DbContract.Profile.LOGIN, profile.getLogin());
        values.put(DbContract.Profile.NAME, profile.getName());
        values.put(DbContract.Profile.COMPANY, profile.getCompany());
        values.put(DbContract.Profile.AVATAR_URL, profile.getAvatarUrl());
        values.put(DbContract.Profile.BIO, profile.getBio());
        values.put(DbContract.Profile.EMAIL, profile.getEmail());
        values.put(DbContract.Profile.LOCATION, profile.getLocation());
        values.put(DbContract.Profile.CREATED_AT, profile.getCreatedAt());
        values.put(DbContract.Profile.UPDATED_AT, profile.getUpdatedAt());
        values.put(DbContract.Profile.PUBLIC_REPOS, profile.getPublicRepos());
        values.put(DbContract.Profile.OWNED_PRIVATE_REPOS, profile.getOwnedPrivateRepos());

        try {
            getContentResolver().insert(GithubContentProvider.PROFILE_URI, values);
        } catch (SQLException ignored) {
            String selection = DbContract.Profile.ID + "=?";
            String[] selectionArgs = new String[] {
                    String.valueOf(profile.getId())
            };
            getContentResolver().update(GithubContentProvider.PROFILE_URI, values, selection, selectionArgs);
        }

    }

    private void updateUIFromDb() {
        // Fetch all of the repositories from the local database
        Cursor cursor = getContentResolver().query(GithubContentProvider.PROFILE_URI, null, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) { // Move to the first position in the cursor
                // Extract all of the column indexes based on the column names
                int idIndex = cursor.getColumnIndex(DbContract.Profile.ID);
                int loginIndex = cursor.getColumnIndex(DbContract.Profile.LOGIN);
                int nameIndex = cursor.getColumnIndex(DbContract.Profile.NAME);
                int companyIndex = cursor.getColumnIndex(DbContract.Profile.COMPANY);
                int avatarIndex = cursor.getColumnIndex(DbContract.Profile.AVATAR_URL);
                int bioIndex = cursor.getColumnIndex(DbContract.Profile.BIO);
                int emailIndex = cursor.getColumnIndex(DbContract.Profile.EMAIL);
                int locationIndex = cursor.getColumnIndex(DbContract.Profile.LOCATION);
                int createdAtIndex = cursor.getColumnIndex(DbContract.Profile.CREATED_AT);
                int updatedAtIndex = cursor.getColumnIndex(DbContract.Profile.UPDATED_AT);
                int publicReposIndex = cursor.getColumnIndex(DbContract.Profile.PUBLIC_REPOS);
                int ownedPrivateReposIndex = cursor.getColumnIndex(DbContract.Profile.OWNED_PRIVATE_REPOS);

                // And extract the data for the profile
                GithubProfile profile = new GithubProfile();
                profile.setId(cursor.getInt(idIndex));
                profile.setLogin(cursor.getString(loginIndex));
                profile.setName(cursor.getString(nameIndex));
                profile.setCompany(cursor.getString(companyIndex));
                profile.setAvatarUrl(cursor.getString(avatarIndex));
                profile.setBio(cursor.getString(bioIndex));
                profile.setEmail(cursor.getString(emailIndex));
                profile.setLocation(cursor.getString(locationIndex));
                profile.setCreatedAt(cursor.getString(createdAtIndex));
                profile.setUpdatedAt(cursor.getString(updatedAtIndex));
                profile.setPublicRepos(cursor.getInt(publicReposIndex));
                profile.setOwnedPrivateRepos(cursor.getInt(ownedPrivateReposIndex));

                // And show it in the UI
                updateUI(profile);
            }
            // Don't forget to free the cursor
            cursor.close();
        }
    }


    private void updateUI(GithubProfile profile) {
        mDisplayedProfile = profile;

        Picasso.with(this).load(Uri.parse(profile.getAvatarUrl())).into(mProfilePicture);
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
            case R.id.clear_profile:
                getContentResolver().delete(GithubContentProvider.PROFILE_URI, null, null);
                updateUIFromDb();
                return true;
        }

        return false;
    }

    public void logOut() {
        Toast.makeText(ProfileActivity.this, "Log out successful", Toast.LENGTH_SHORT).show();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.edit().remove(Contract.Preferences.AUTH_HASH).apply();

        getContentResolver().delete(GithubContentProvider.PROFILE_URI, null, null);
        getContentResolver().delete(GithubContentProvider.REPOSITORY_URI, null, null);

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
                    handleNetworkResponse(profile);
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
