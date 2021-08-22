package com.project.retrofitapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<MainData> dataArrayList = new ArrayList<MainData>();
    private MainAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);

        adapter = new MainAdapter(dataArrayList, MainActivity.this);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        recyclerView.setAdapter(adapter);

        getData();
    }

    private void getData() {

        ProgressDialog progressDialog = new ProgressDialog(this);

        progressDialog.setMessage("Please wait ....");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://picsum.photos/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        // create interface
        MainInterface mainInterface = retrofit.create(MainInterface.class);

        // initialize call
        Call<String> callString = mainInterface.STRING_CALL();

        callString.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                // Check condition
                if (response.isSuccessful() && response.body() != null) {
                    // When response is successful and not null
                    // dismiss dialog
                    progressDialog.dismiss();
                    try {
                        // initialise response JSON Array
                        JSONArray jsonArray = new JSONArray(response.body());
                        // parse json array
                        parseArray(jsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    private void parseArray(JSONArray jsonArray) {
        // clear array list
        dataArrayList.clear();

        // user for loop
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                // initialise json object
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                // initialise Main data
                MainData mainData = new MainData();

                mainData.setImage(jsonObject.getString("download_url"));
                mainData.setName(jsonObject.getString("author"));
                // add data to the array list
                dataArrayList.add(mainData);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            adapter = new MainAdapter(dataArrayList, MainActivity.this);

            recyclerView.setAdapter(adapter);
        }
    }
}