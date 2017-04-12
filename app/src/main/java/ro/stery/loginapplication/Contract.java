package ro.stery.loginapplication;

import ro.stery.loginapplication.ProfileActivity;

/**
 * Created by Stery on 11.04.2017.
 */

public interface Contract {

    interface Preferences {
        String AUTH_HASH = "auth_hash";
        String USERNAME = ProfileActivity.USERNAME;
    }

    interface ProfileActivity {
        String USERNAME = "username";
    }

}
