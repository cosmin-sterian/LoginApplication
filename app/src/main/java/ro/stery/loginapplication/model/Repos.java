package ro.stery.loginapplication.model;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;

/**
 * Created by Stery on 11.04.2017.
 */

public interface Repos {

    @GET("/")
    Call<LoginData> checkAuth(@Header("Authorization") String auth);
    @GET("/user")
    Call<GithubProfile> getUserProfile(@Header("Authorization") String auth);
    @GET("/repos")
    Call<List<GithubRepo>> getUserRepos(@Header("Authorization") String auth);

    class Service {

        private static Repos sInstance;

        public synchronized static Repos get() {
            if(sInstance == null) {
                sInstance = new Retrofit.Builder().baseUrl("https://api.github.com")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build().create(Repos.class);
            }
            return sInstance;
        }

    }

}