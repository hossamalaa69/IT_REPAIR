package com.example.fixtech;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ItemsAdapter itemsAdapter;
    ProgressBar progressBar;
    MainActivityViewModel mainActivityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initRecycler();

        setupViewModel();

        setupSwipe();

        getPermissions();
    }

    private void getPermissions() {
        ActivityCompat.requestPermissions(MainActivity.this
                , new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
    }

    private void initRecycler(){
        progressBar = (ProgressBar) findViewById(R.id.items_progress);
        recyclerView = findViewById(R.id.items_recycler);
        itemsAdapter = new ItemsAdapter(this, new ArrayList<Item>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(itemsAdapter);
    }

    private void setupViewModel(){
        mainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        mainActivityViewModel.loadAllItems();

        mainActivityViewModel.loadAllItems().observe(this, new Observer<List<Item>>() {
            @Override
            public void onChanged(List<Item> items) {
                itemsAdapter.setItemList(items);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void setupSwipe(){

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                //Setting message manually and performing action on button click
                builder.setMessage("Pick witch action to be applied on Item !")
                        .setCancelable(true)
                        .setPositiveButton("Delete", (dialog, id) -> {
                            dialog.cancel();
                            progressBar.setVisibility(View.VISIBLE);
                            int position = viewHolder.getAdapterPosition();
                            List<Item> items = itemsAdapter.getItems();
                            mainActivityViewModel.deleteProduct(items.get(position));
                        })
                        .setNegativeButton("Edit", (dialog, id) -> {
                            dialog.cancel();
                            int position = viewHolder.getAdapterPosition();
                            List<Item> items = itemsAdapter.getItems();
                            Item item = items.get(position);
                            updateItem(item);
                        });
                AlertDialog alert = builder.create();
                alert.setTitle("Product Options");
                alert.show();
                alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        itemsAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void updateItem(Item item) {
        Intent intent = new Intent(MainActivity.this,ItemActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("item_serialized",item);
        intent.putExtras(bundle);
        startActivity(intent);
    }


    public void addItem(View view) {
        Intent i = new Intent(this, ItemActivity.class);
        startActivity(i);
    }
}