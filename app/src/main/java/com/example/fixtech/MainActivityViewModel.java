package com.example.fixtech;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class MainActivityViewModel extends AndroidViewModel {

    MutableLiveData<List<Item>> itemListMutableLiveData = new MutableLiveData<>();

    public MainActivityViewModel(@NonNull final Application application) {
        super(application);

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(Item.class.getSimpleName());
        final List<Item> itemList = new ArrayList<>();
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemList.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Item item = snap.getValue(Item.class);
                    itemList.add(item);
                }
                itemListMutableLiveData.setValue(itemList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(application.getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public MutableLiveData<List<Item>> loadAllItems(){
        return itemListMutableLiveData;
    }

    public void deleteProduct(Item item) {
        final String id = item.getID();
        String imageUrl = item.getImage_url();

        StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);

        imageRef.delete().addOnSuccessListener(aVoid -> {
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(Item.class.getSimpleName()).child(id);
            dbRef.removeValue((error, ref) ->
                    Toast.makeText(MainActivityViewModel.this.getApplication().getApplicationContext()
                            , "deleted successfully", Toast.LENGTH_SHORT).show());
        }).addOnFailureListener(exception ->
                Toast.makeText(MainActivityViewModel.this.getApplication().getApplicationContext()
                        , "Failed deletion", Toast.LENGTH_SHORT).show());
    }
}
