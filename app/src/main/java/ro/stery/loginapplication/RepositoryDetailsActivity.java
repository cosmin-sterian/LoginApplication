package ro.stery.loginapplication;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ro.stery.loginapplication.model.Repository;

public class RepositoryDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repository_details);


        Repository repository = new Repository(){{
            setName(getIntent().getStringExtra("name"));
            setDescription(getIntent().getStringExtra("description"));
            setPrivate(getIntent().getBooleanExtra("is_public", false));
            setUrl(getIntent().getStringExtra("url"));
            setHtmlUrl(getIntent().getStringExtra("html_url"));
        }};

        getSupportActionBar().setTitle("Repo Details - " + repository.getName());

        Fragment details = RepositoryDetailsFragment.New(repository);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container_portrait, details)
                .commit();
    }
}
