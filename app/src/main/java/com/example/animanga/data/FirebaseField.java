package com.example.animanga.data;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FirebaseField {
    private static final String TAG = "FirebaseField";
    private static final ArrayList<Item> itemArrayList = new ArrayList<>();

    public static ArrayList<Item> getItems(String collectionName, boolean status) {
        CollectionReference reference = FirebaseFirestore.getInstance().collection(collectionName);
        reference.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        itemArrayList.clear();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Item item = documentSnapshot.toObject(Item.class);
                            item.setItemId(documentSnapshot.getId());
                            itemArrayList.add(item);
                        }
                    }
                });
        Log.e(TAG, "getItems: " +itemArrayList.get(0).getItemId() );
        return itemArrayList;
    }

    public static boolean addItem(String collectionName, final Item item) {
        CollectionReference reference = FirebaseFirestore.getInstance().collection(collectionName);
        reference.add(item)
                .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                item.setItemId(e.getMessage());
                Log.d(TAG, "addItem: " + e.getMessage());
            }
        });
        return (item.getItemId() == null);
    }

    public static void updateItem(String collectionName, final Item item){
        DocumentReference document = FirebaseFirestore.getInstance().collection(collectionName).document(item.getItemId());
        Map<String, Object> newItem = new HashMap<>();
        newItem.put("desc", item.getDesc());
        newItem.put("link", item.getLink());
        newItem.put("name", item.getName());
        //newItem.put("pic",null);
        newItem.put("status", item.getStatus());
        document.set(newItem, SetOptions.merge());
    }

    //hb'a ast5dmha b3den l snapshot dy
    public static ArrayList<Item> getDataSnapshot(String collectionName, boolean status) {
        CollectionReference reference = FirebaseFirestore.getInstance().collection(collectionName);
        final ArrayList<Item> itemArrayList = new ArrayList<>();
        reference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Item item = documentSnapshot.toObject(Item.class);
                    itemArrayList.add(item);
                }
            }
        });
        return itemArrayList;
    }

    public static String isAnime(boolean t) {
        if (t) {
            return "ANIME";
        }
        return "MANGA";
    }
}
