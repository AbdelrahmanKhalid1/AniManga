package com.example.animanga;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.animanga.data.FirebaseField;
import com.example.animanga.data.Item;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static boolean MENU_CHOICE = true; // A anime, M manga
    private static boolean NAVIGATION_CHOICE = true; //W watched, L later
    private ProgressBar progressBar;
    private ArrayList<Item> itemArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomView = findViewById(R.id.bottom_navigation);
        bottomView.setOnNavigationItemSelectedListener(navListener);
        itemArrayList = new ArrayList<>();
        progressBar = findViewById(R.id.progress_bar_circular);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getItems();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.watched:
                            NAVIGATION_CHOICE = true;
                            break;
                        case R.id.watch_later:
                            NAVIGATION_CHOICE = false;
                            break;
                        case R.id.add:
                            startActivity(new Intent(getApplicationContext(), AddAnimangaActivity.class));
                            return false;
                        default:
                            return false;
                    }
                    getItems();
                    return true;
                }
            };


    private void fillGrid() {
        if (itemArrayList.isEmpty()) {
            Toast.makeText(this, "Empty", Toast.LENGTH_SHORT).show();
        }
        GridView gridView = findViewById(R.id.grid_view);
        GridAdapter adapter = new GridAdapter(itemArrayList);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ItemActivity.class);
                intent.putExtra("item", itemArrayList.get(position));
                startActivity(intent);
            }
        });
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.choice_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.anime:
                MENU_CHOICE = true;
                break;
            case R.id.manga:
                MENU_CHOICE = false;
                break;
            default:
                return false;
        }
        getItems();
        return true;
    }

    private void getItems() {
        progressBar.setVisibility(View.VISIBLE);
        CollectionReference reference = FirebaseFirestore.getInstance().collection(FirebaseField.isAnime(MENU_CHOICE));
        Log.e(TAG, "getItems: " + NAVIGATION_CHOICE);
        reference.whereEqualTo("status", NAVIGATION_CHOICE)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        itemArrayList.clear();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Item item = documentSnapshot.toObject(Item.class);
                            item.setItemId(documentSnapshot.getId());
                            item.setCategory(MENU_CHOICE);
                            itemArrayList.add(item);
                        }
                        fillGrid();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: " + e.getMessage());
            }
        });
    }
}
