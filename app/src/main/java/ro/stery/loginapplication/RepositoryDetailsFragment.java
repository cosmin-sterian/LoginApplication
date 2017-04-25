package ro.stery.loginapplication;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ro.stery.loginapplication.model.Repository;

public class RepositoryDetailsFragment extends Fragment {

    private TextView mDescription, mPublic, mUrl, mHtmlUrl;

    public static Fragment New(Repository repository) {
        Fragment f = new RepositoryDetailsFragment();
        Bundle args = new Bundle();

        args.putString("description", repository.getDescription());
        args.putBoolean("is_public", repository.getPrivate());
        args.putString("url", repository.getUrl());
        args.putString("html_url", repository.getHtmlUrl());

        f.setArguments(args);
        return f;
    }

    public RepositoryDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_repository_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDescription = (TextView) view.findViewById(R.id.description);
        mPublic = (TextView) view.findViewById(R.id.is_public);
        mUrl = (TextView) view.findViewById(R.id.url);
        mHtmlUrl = (TextView) view.findViewById(R.id.html_url);

        mDescription.setText(getArguments().getString("description"));
        mPublic.setText(getArguments().getBoolean("is_public") ? "Public" : "Private");
        mUrl.setText(getArguments().getString("url"));
        mHtmlUrl.setText(getArguments().getString("html_url"));

    }
}
