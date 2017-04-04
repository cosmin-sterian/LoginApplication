package ro.stery.loginapplication;

import android.os.PersistableBundle;
import android.support.annotation.Nullable;
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

import java.util.List;

import ro.stery.loginapplication.model.Repository;

public class RepositoriesActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repositories);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        Adapter adapter = new Adapter();
        adapter.setmData(Repository.getsMockRepository());
        mRecyclerView.setAdapter(adapter);
    }

    static class Adapter extends RecyclerView.Adapter {

        List<Repository> mData;

        public void setmData(List<Repository> mData) {
            this.mData = mData;
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_repository, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((ViewHolder) holder).bind(mData.get(position));
        }

        @Override
        public int getItemCount() {
            return mData!=null ? mData.size() : 0;
        }

        static class ViewHolder extends RecyclerView.ViewHolder {

            private final TextView mCountWatchers;
            private final TextView mNameAndOwner;
            private final TextView mDescription;
            private final LinearLayout mTopics;
            private final CheckBox mIsPublic;

            public ViewHolder(View itemView) {
                super(itemView);

                mCountWatchers = (TextView) itemView.findViewById(R.id.CountRepos);
                mNameAndOwner = (TextView) itemView.findViewById(R.id.NameAndOwner);
                mDescription = (TextView) itemView.findViewById(R.id.Description);
                mTopics = (LinearLayout) itemView.findViewById(R.id.Topics);
                mIsPublic = (CheckBox) itemView.findViewById(R.id.is_public);
            }

            public void bind(Repository repository) {
                mCountWatchers.setText(String.valueOf(repository.getmWatchersCount()));
                mNameAndOwner.setText(itemView.getContext().getString(R.string.repo_name_owner, repository.getmName(), repository.getmOwner()));
                mDescription.setText(repository.getmDescription());
                mIsPublic.setChecked(!repository.ismIsPrivate());
            }
        }

    }

}
