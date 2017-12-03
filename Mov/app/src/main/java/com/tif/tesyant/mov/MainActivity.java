package com.tif.tesyant.mov;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tif.tesyant.mov.adapter.MovieAdapter;
import com.tif.tesyant.mov.model.Results;
import com.tif.tesyant.mov.model.SearchMovie;
import com.tif.tesyant.mov.service.Client;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String API_KEY = "a1e424bc06f73c575891b2a9b4239c57";
    static final String LANG = "en-US";
    private ListView listView;
    private EditText editText;
    private Button button;
    private TextView tvTitle, tvRelease;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView)findViewById(R.id.list_item);
        editText = (EditText)findViewById(R.id.edt_search);
        button = (Button)findViewById(R.id.btn_search);
        tvTitle = (TextView)findViewById(R.id.tv_title);
        tvRelease = (TextView)findViewById(R.id.tv_release_date);

        button.setOnClickListener(this);

        editText.getText().toString();



    }

    @Override
    public void onClick(View view) {

        Log.e("try", "abc");

        OkHttpClient.Builder okhttpClientBuilder = new OkHttpClient.Builder();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        okhttpClientBuilder.addInterceptor(logging);

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okhttpClientBuilder.build());

        Retrofit retrofit = builder.build();

        String search_text = editText.getText().toString().trim();

        Client client = retrofit.create(Client.class);
        Call<SearchMovie> call = client.getList(API_KEY, LANG, search_text);

        call.enqueue(new Callback<SearchMovie>() {

            @Override
            public void onResponse(Call<SearchMovie> call, retrofit2.Response<SearchMovie> response) {
                SearchMovie mSearch = response.body();
                int result_size = mSearch.getResults().size();
                final String[] list_movie_id = new String[result_size];
                for (int i = 0; i< result_size;i++){
                    list_movie_id[i] = String.valueOf(mSearch.getResults().get(i).getId());
                }
                List<Results> mov = mSearch.getResults();
                MovieAdapter listAdapter = new MovieAdapter(mov, MainActivity.this);
                listView.setAdapter(listAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String id = list_movie_id[i];
                        Toast.makeText(MainActivity.this, "ID = " + id, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, DetailListActivity.class);
                        intent.putExtra("movieId", id);
                        startActivity(intent);
                    }
                });
//                listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                    @Override
//                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                        Log.e("gfhf", "checked");
//                        Toast.makeText(MainActivity.this, "Item Postition = " + i, Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onNothingSelected(AdapterView<?> adapterView) {
//                        Log.e("gfhf", "checked nothing");
//                    }
//                });
                listView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<SearchMovie> call, Throwable t) {
                Log.e("try", "ghj"+t);

            }
        });



    }

}
