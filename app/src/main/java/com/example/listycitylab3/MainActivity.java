package com.example.listycitylab3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements AddCityFragment.AddCityDialogListener {

    private FirebaseFirestore db;
    private CollectionReference citiesRef;
    private ArrayList<City> dataList;
    private ListView cityList;
    private CityArrayAdapter cityAdapter;

    @Override
    public void addCity(City city) {
        citiesRef.document(city.getName()).set(city);
    }

    @Override
    public void editCity(City city, int position) {
        dataList.set(position, city);
        cityAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        setSupportActionBar(findViewById(R.id.toolbar));

//        String[] cities = { "Edmonton", "Vancouver", "Toronto" };
//        String[] provinces = { "AB", "BC", "ON" };

        dataList = new ArrayList<>();
        cityList = findViewById(R.id.city_list);
        cityAdapter = new CityArrayAdapter(this, dataList);
        cityList.setAdapter(cityAdapter);
        FloatingActionButton fab = findViewById(R.id.button_add_city);
        fab.setOnClickListener(v -> {
            AddCityFragment fragment = new AddCityFragment();
            fragment.show(getSupportFragmentManager(), "Add City");
        });

        cityList.setOnItemClickListener((parent, view, position, id) -> {
            City city = dataList.get(position);
            AddCityFragment fragment = AddCityFragment.newInstance(city, position);
            fragment.show(getSupportFragmentManager(), "Edit City");
        });
        cityList.setOnItemLongClickListener((parent, view, position, id) -> {
            City city = dataList.get(position);
            citiesRef.document(city.getName()).delete();
            return true;
        });

        db = FirebaseFirestore.getInstance();
        citiesRef = db.collection("cities");

        citiesRef.addSnapshotListener((value, error) -> {
            if (error != null) { Log.e("Firestore", error.toString()); return; }
            dataList.clear();
            if (value != null) {
                for (QueryDocumentSnapshot snapshot : value) {
                    dataList.add(new City(snapshot.getString("name"), snapshot.getString("province")));
                }
            }
            cityAdapter.notifyDataSetChanged();
        });


    }
}
