package com.example.fixtech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ItemActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener  {

    private static final int READ_MEDIA_PERMISSION_CODE = 1000;
    private static final int REQUEST_IMAGE_PICTURE = 1;
    private static final int GET_GALLERY_PICTURE = 2;
    private Calendar calendar;

    private ImageView item_img;
    private EditText cust_name_edit;
    private EditText cust_phone_edit;
    private EditText cust_email_edit;
    private EditText device_name_edit;
    private EditText device_id_edit;
    private EditText device_issue_edit;
    private EditText device_password_edit;
    private EditText device_price_edit;
    private RadioGroup radioGroup;
    private RadioButton fixed, not_fixed;
    private RadioGroup paid_radio_group;
    private RadioButton paid, not_paid;

    private TextView book_date_txt;
    private TextView delivery_date_txt;
    private ProgressBar progressBar;

    private Uri cameraURI = null;
    private Uri currentUri = null;

    private Item updated_item = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        initViews();

        checkReceived();

        getPermissions();
    }

    private void getPermissions() {
        ActivityCompat.requestPermissions(ItemActivity.this
                , new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
    }

    private void checkReceived() {
        updated_item = (Item) getIntent().getSerializableExtra("item_serialized");
        if(updated_item != null){
            SimpleDateFormat sdf = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss");
            String status = updated_item.getIssue_status();
            boolean paid = updated_item.isPaid();

            if(!updated_item.getImage_url().isEmpty()) {
                Glide.with(this)
                        .load(updated_item.getImage_url())
                        .placeholder(R.drawable.ic_image_placeholder)
                        .into(item_img);
            }
            cust_name_edit.setText(updated_item.getCustomer_name());
            cust_phone_edit.setText(updated_item.getCustomer_phone());
            cust_email_edit.setText(updated_item.getCustomer_email());
            device_name_edit.setText(updated_item.getDevice_name());
            device_id_edit.setText(updated_item.getDevice_ID());
            device_issue_edit.setText(updated_item.getDevice_issue());
            device_password_edit.setText(updated_item.getDevice_password());
            book_date_txt.setText(sdf.format(new Date(updated_item.getBook_date())).toString());
            delivery_date_txt.setText(sdf.format(new Date(updated_item.getDelivery_date())).toString());
            device_price_edit.setText("" + updated_item.getPrice());
            if(status.equals("Fixed"))
                radioGroup.check(R.id.radio_fixed);
            else
                radioGroup.check(R.id.radio_not_fixed);
            if(paid)
                paid_radio_group.check(R.id.radio_paid);
            else
                paid_radio_group.check(R.id.radio_not_paid);

        }
    }

    private void initViews() {
        item_img = findViewById(R.id.item_img);
        cust_name_edit = findViewById(R.id.cust_name_edit);
        cust_phone_edit = findViewById(R.id.cust_phone_edit);
        cust_email_edit = findViewById(R.id.cust_email_edit);
        device_name_edit = findViewById(R.id.device_name_edit);
        device_id_edit = findViewById(R.id.device_id_edit);
        device_issue_edit = findViewById(R.id.device_issue_edit);
        device_password_edit = findViewById(R.id.device_password_edit);
        radioGroup = findViewById(R.id.myRadioGroup);
        fixed = findViewById(R.id.radio_fixed);
        not_fixed = findViewById(R.id.radio_not_fixed);
        paid_radio_group = findViewById(R.id.paid_radio_group);
        paid = findViewById(R.id.radio_paid);
        not_paid = findViewById(R.id.radio_not_paid);
        book_date_txt = findViewById(R.id.book_date_txt);
        delivery_date_txt = findViewById(R.id.delivery_date_txt);
        progressBar = findViewById(R.id.upload_image_progress_item);
        device_price_edit = findViewById(R.id.device_price_edit);


        book_date_txt.setText(new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss")
                .format(new Date()));
    }


    public void selectImage(View view) {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(ItemActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo"))
                {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // Ensure that there's a camera activity to handle the intent
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        // Create the File where the photo should go
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {
                            Toast.makeText(ItemActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                        // Continue only if the File was successfully created
                        if (photoFile != null) {
                            cameraURI = FileProvider.getUriForFile(ItemActivity.this,
                                    "com.example.fixtech",
                                    photoFile);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraURI);
                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_PICTURE);
                        }
                    }
                }
                else if (options[item].equals("Choose from Gallery"))
                {
                    Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, GET_GALLERY_PICTURE);
                }
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_PICTURE) {
                currentUri = cameraURI;
                item_img.setImageURI(currentUri);
            } else if (requestCode == GET_GALLERY_PICTURE) {
                currentUri = data.getData();
                item_img.setImageURI(currentUri);
            }
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }

    public void saveItem(View view) {

        //case new item
        if(!cust_name_edit.getText().toString().isEmpty() && !cust_phone_edit.getText().toString().isEmpty() && !device_price_edit.getText().toString().isEmpty()
                && !cust_email_edit.getText().toString().isEmpty() && !device_name_edit.getText().toString().isEmpty()
                && !device_id_edit.getText().toString().isEmpty() && !device_issue_edit.getText().toString().isEmpty()
                && !device_password_edit.getText().toString().isEmpty() && !delivery_date_txt.getText().toString().isEmpty() && updated_item == null){

            ScrollView main_scroll_view = (ScrollView) findViewById(R.id.main_scroll_view);
            main_scroll_view.fullScroll(ScrollView.FOCUS_UP);
            progressBar.setVisibility(View.VISIBLE);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss");
            Date book_date = new Date();
            Date delivery_date = new Date();
            try {
                book_date = simpleDateFormat.parse(book_date_txt.getText().toString());
                delivery_date = simpleDateFormat.parse(delivery_date_txt.getText().toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            int selected_status = radioGroup.getCheckedRadioButtonId();
            RadioButton current_status = findViewById(selected_status);
            String status = current_status.getText().toString();
            int selected_paid = paid_radio_group.getCheckedRadioButtonId();
            RadioButton current_payment = findViewById(selected_paid);
            String isPaidd = current_payment.getText().toString();
            boolean isPaid = isPaidd.equals("Paid");
            Item item = new Item("000" ,cust_name_edit.getText().toString(), cust_phone_edit.getText().toString(), cust_email_edit.getText().toString()
                    ,device_name_edit.getText().toString(), device_id_edit.getText().toString(), device_issue_edit.getText().toString()
                    ,device_password_edit.getText().toString(), status, book_date.getTime(), delivery_date.getTime(), "", isPaid, Float.parseFloat(device_price_edit.getText().toString()));
            if(currentUri != null)
                insertIntoDb(item);
            else
                insertWithImageUrl(item);
        }

        //update item
        else if(updated_item != null && !cust_name_edit.getText().toString().isEmpty() && !cust_phone_edit.getText().toString().isEmpty() && !device_price_edit.getText().toString().isEmpty()
                && !cust_email_edit.getText().toString().isEmpty() && !device_name_edit.getText().toString().isEmpty()
                && !device_id_edit.getText().toString().isEmpty() && !device_issue_edit.getText().toString().isEmpty()
                && !device_password_edit.getText().toString().isEmpty() && !delivery_date_txt.getText().toString().isEmpty()){

            ScrollView main_scroll_view = (ScrollView) findViewById(R.id.main_scroll_view);
            main_scroll_view.fullScroll(ScrollView.FOCUS_UP);
            progressBar.setVisibility(View.VISIBLE);

            //updated without new image
            if(currentUri == null){
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss");
                Date book_date = new Date();
                Date delivery_date = new Date();
                try {
                    book_date = simpleDateFormat.parse(book_date_txt.getText().toString());
                    delivery_date = simpleDateFormat.parse(delivery_date_txt.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                int selected_status = radioGroup.getCheckedRadioButtonId();
                RadioButton current_status = findViewById(selected_status);
                String status = current_status.getText().toString();
                int selected_paid = paid_radio_group.getCheckedRadioButtonId();
                RadioButton current_payment = findViewById(selected_paid);
                String isPaidd = current_payment.getText().toString();
                boolean isPaid = isPaidd.equals("Paid");
                Item item = new Item( updated_item.getID(), cust_name_edit.getText().toString(), cust_phone_edit.getText().toString(), cust_email_edit.getText().toString()
                        ,device_name_edit.getText().toString(), device_id_edit.getText().toString(), device_issue_edit.getText().toString()
                        ,device_password_edit.getText().toString(), status, book_date.getTime(), delivery_date.getTime(), updated_item.getImage_url(), isPaid, Float.parseFloat(device_price_edit.getText().toString()));
                updateWithoutImage(item);
            }
            //updated with new image
            else{
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss");
                Date book_date = new Date();
                Date delivery_date = new Date();
                try {
                    book_date = simpleDateFormat.parse(book_date_txt.getText().toString());
                    delivery_date = simpleDateFormat.parse(delivery_date_txt.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                int selected_status = radioGroup.getCheckedRadioButtonId();
                RadioButton current_status = findViewById(selected_status);
                String status = current_status.getText().toString();
                int selected_paid = paid_radio_group.getCheckedRadioButtonId();
                RadioButton current_payment = findViewById(selected_paid);
                String isPaidd = current_payment.getText().toString();
                boolean isPaid = isPaidd.equals("Paid");
                Item item = new Item( updated_item.getID(), cust_name_edit.getText().toString(), cust_phone_edit.getText().toString(), cust_email_edit.getText().toString()
                        ,device_name_edit.getText().toString(), device_id_edit.getText().toString(), device_issue_edit.getText().toString()
                        ,device_password_edit.getText().toString(), status, book_date.getTime(), delivery_date.getTime(), currentUri.toString(), isPaid, Float.parseFloat(device_price_edit.getText().toString()));
                updateWithImage(item);
            }

        }
        //not full arguments
        else{
            Toast.makeText(this, "Please enter full data", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateWithImage(Item item) {
        if(updated_item.getImage_url().isEmpty()){
            final StorageReference photoRef = FirebaseStorage.getInstance().getReference().child(Item.class.getSimpleName()).child(currentUri.getLastPathSegment());
            UploadTask uploadTask = photoRef.putFile(currentUri);
            Task<Uri> urlTask = uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return photoRef.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    String imageUrl = downloadUri.toString();
                    item.setImage_url(imageUrl);
                    updateWithoutImage(item);
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ItemActivity.this, "Failed update", Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(updated_item.getImage_url());
            imageRef.delete().addOnSuccessListener(aVoid -> {
                final StorageReference photoRef = FirebaseStorage.getInstance().getReference().child(Item.class.getSimpleName()).child(currentUri.getLastPathSegment());
                UploadTask uploadTask = photoRef.putFile(currentUri);
                Task<Uri> urlTask = uploadTask.continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return photoRef.getDownloadUrl();
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String imageUrl = downloadUri.toString();
                        item.setImage_url(imageUrl);
                        updateWithoutImage(item);
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(ItemActivity.this, "Failed update", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        }
    }

    private void updateWithoutImage(Item item) {
        DatabaseReference itemDbRef = FirebaseDatabase.getInstance().getReference(Item.class.getSimpleName());
        String id = item.getID();
        itemDbRef.child(id).setValue(item).addOnSuccessListener(aVoid -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(ItemActivity.this, "Updated successfully", Toast.LENGTH_SHORT).show();
            checkPrinting(item);
        });
    }

    private void insertIntoDb(final Item item) {
        StorageReference mStorageReference = FirebaseStorage.getInstance().getReference().child(Item.class.getSimpleName());
        final StorageReference photoRef = mStorageReference.child(currentUri.getLastPathSegment());
        UploadTask uploadTask = photoRef.putFile(currentUri);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return photoRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()){
                    Uri downloadUri = task.getResult();
                    String imageUrl = downloadUri.toString();
                    Toast.makeText(ItemActivity.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                    item.setImage_url(imageUrl);
                    insertWithImageUrl(item);
                }else{
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ItemActivity.this, "Failed Uploading", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void insertWithImageUrl(Item item) {
        DatabaseReference itemDbReference = FirebaseDatabase.getInstance().getReference(Item.class.getSimpleName());
        String id = itemDbReference.push().getKey();
        item.setID(id);
        itemDbReference.child(id).setValue(item).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ItemActivity.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                checkPrinting(item);
            }
        });
    }

    public void openDateDelivery(View view) {
        DatePickerDialog dateDialog = new DatePickerDialog(this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        dateDialog.show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        openTimePicker();
    }

    private void openTimePicker() {
        TimePickerDialog timeDialog = new TimePickerDialog(this, this,
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE), false);
        timeDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        delivery_date_txt.setText(new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss")
                .format(calendar.getTime()));
    }


    private void checkPrinting(Item item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ItemActivity.this);
        //Setting message manually and performing action on button click
        builder.setMessage("Do You Want To Create PDF file ?")
                .setCancelable(true)
                .setPositiveButton("Yes", (dialog, id) -> {
                    printPDF(item);
                    dialog.cancel();
                })
                .setNegativeButton("No", (dialog, id) -> {
                    dialog.cancel();
                });
        AlertDialog alert = builder.create();
        alert.setTitle("Printing PDF");
        alert.show();
        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                finish();
            }
        });
    }

    private void printPDF(Item item) {
        Intent intent = new Intent(ItemActivity.this, ItemPrintActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("item_serialized",item);
        intent.putExtras(bundle);
        startActivity(intent);
    }

}