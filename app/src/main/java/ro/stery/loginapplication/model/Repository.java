package ro.stery.loginapplication.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Stery on 04.04.2017.
 */

public class Repository {

    String mName;
    String mOwner;
    String mDescription;
    boolean mIsPrivate;
    String mDefaultBranch;
    List<String> mTopics;
    int mWatchersCount;
    Date mCreatedAt;
    Date mUpdatedAt;

    public void setmName(String mName) {
        this.mName = mName;
    }

    public void setmOwner(String mOwner) {
        this.mOwner = mOwner;
    }

    public String getmDescription() {
        return mDescription;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public boolean ismIsPrivate() {
        return mIsPrivate;
    }

    public void setmIsPrivate(boolean mIsPrivate) {
        this.mIsPrivate = mIsPrivate;
    }

    public String getmDefaultBranch() {
        return mDefaultBranch;
    }

    public void setmDefaultBranch(String mDefaultBranch) {
        this.mDefaultBranch = mDefaultBranch;
    }

    public List<String> getmTopics() {
        return mTopics;
    }

    public void setmTopics(List<String> mTopics) {
        this.mTopics = mTopics;
    }

    public int getmWatchersCount() {
        return mWatchersCount;
    }

    public void setmWatchersCount(int mWatchersCount) {
        this.mWatchersCount = mWatchersCount;
    }

    public Date getmCreatedAt() {
        return mCreatedAt;
    }

    public void setmCreatedAt(Date mCreatedAt) {
        this.mCreatedAt = mCreatedAt;
    }

    public Date getmUpdatedAt() {
        return mUpdatedAt;
    }

    public void setmUpdatedAt(Date mUpdatedAt) {
        this.mUpdatedAt = mUpdatedAt;
    }

    public static List<Repository> getsMockRepository() {
        return sMockRepository;
    }

    public String getmName() {
        return mName;
    }

    public String getmOwner() {
        return mOwner;
    }

    static final List<Repository> sMockRepository;

    public Repository(String mName, String mOwner, String mDescription, boolean mIsPrivate, String mDefaultBranch, List<String> mTopics, int mWatchersCount, Date mCreatedAt, Date mUpdatedAt) {
        this.mName = mName;
        this.mOwner = mOwner;
        this.mDescription = mDescription;
        this.mIsPrivate = mIsPrivate;
        this.mDefaultBranch = mDefaultBranch;
        this.mTopics = mTopics;
        this.mWatchersCount = mWatchersCount;
        this.mCreatedAt = mCreatedAt;
        this.mUpdatedAt = mUpdatedAt;
    }

    static {
        sMockRepository = new ArrayList<>();

        for(int i = 0; i < 100; i++) {
            sMockRepository.add(new Repository(
                    "Repo " + i,
                    "Octocat",
                    "Some repo",
                    false,
                    "master",
                    new ArrayList<String>() {{
                        add("Android");
                        add("iOS");
                    }},
                    i,
                    new Date(),
                    new Date()
            ));


        }
    }

}
