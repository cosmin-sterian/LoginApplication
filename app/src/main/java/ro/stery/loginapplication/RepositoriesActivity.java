package ro.stery.loginapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ro.stery.loginapplication.model.GitHub;
import ro.stery.loginapplication.model.Repository;

public class RepositoriesActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private Adapter mAdapter;
    private boolean mCanShowDetails = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repositories);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mCanShowDetails = (findViewById(R.id.container) != null);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new Adapter(new Adapter.Callback() {

            @Override
            public void show(Repository repository) {
                if(mCanShowDetails) {
                    Fragment details = RepositoryDetailsFragment.New(repository);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container, details)
                            .commit();
                } else {
                    Intent intent = new Intent(RepositoriesActivity.this, RepositoryDetailsActivity.class);
                    intent.putExtra("name", repository.getName());
                    intent.putExtra("description", repository.getDescription());
                    intent.putExtra("is_public", repository.getPrivate());
                    intent.putExtra("url", repository.getUrl());
                    intent.putExtra("html_url", repository.getHtmlUrl());
                    startActivity(intent);
                }
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        fetchRepos();
    }

    private void fetchRepos() {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Call<List<Repository>> repositoriesCall = GitHub.Service.get().getUserRepositories(preferences.getString(Contract.Preferences.AUTH_HASH, null), "owner");

        repositoriesCall.enqueue(new Callback<List<Repository>>() {
            @Override
            public void onResponse(Call<List<Repository>> call, Response<List<Repository>> response) {
                if(response.isSuccessful()) {

                    List<Repository> repos = response.body();
                    updateUI(repos);

                }
            }

            @Override
            public void onFailure(Call<List<Repository>> call, Throwable t) {

            }
        });
    }

    private void updateUI(List<Repository> repos) {

        mAdapter.setmData(repos);
        mAdapter.notifyDataSetChanged();

    }

    static class Adapter extends RecyclerView.Adapter {

        List<Repository> mData;
        Callback mCallback;

        public Adapter(Callback callback) {
            mCallback = callback;
        }

        public interface Callback {
            void show(Repository repository);
        }

        public void setmData(List<Repository> mData) {
            this.mData = mData;
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_repository, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            ((ViewHolder) holder).bind(mData.get(position), new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mCallback.show(mData.get(position));
                }
            });
        }

        @Override
        public int getItemCount() {
            return mData!=null ? mData.size() : 0;
        }

        static class ViewHolder extends RecyclerView.ViewHolder {

            private final TextView mName;

            public ViewHolder(View itemView) {
                super(itemView);

                mName = (TextView) itemView.findViewById(R.id.name);
            }

            public void bind(Repository repository, View.OnClickListener onClickListener) {
                mName.setText(repository.getName());
                itemView.setOnClickListener(onClickListener);
            }
        }

    }

}
