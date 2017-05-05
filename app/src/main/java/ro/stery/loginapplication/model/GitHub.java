package ro.stery.loginapplication.model;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

/**
 * Created by Stery on 06.04.2017.
 */

public interface GitHub {

    @GET("/")
    Call<LoginData> checkAuth(@Header("Authorization") String auth);
    @GET("/user")
    Call<GithubProfile> getUserProfile(@Header("Authorization") String auth);
    @GET("/user/repos")
    Call<List<Repository>> getUserRepositories(@Header("Authorization") String auth, @Query("affiliation") String affiliation);

    class Service {

        private static GitHub sInstance;

        public synchronized static GitHub get() {
            if(sInstance == null) {
                sInstance = new Retrofit.Builder().baseUrl("https://api.github.com")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build().create(GitHub.class);
            }
            return sInstance;
        }

    }

}
