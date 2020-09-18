package com.example.fixtech;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ItemPrintActivity extends AppCompatActivity {

    Item item;
    String dirpath = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_print);

        fillViews();
    }

    private void copyData(){
        item = (Item) getIntent().getSerializableExtra("item_serialized");


        TextView device_name_print = findViewById(R.id.deivce_name_print);
        device_name_print.setText("Device Name: "+ item.getDevice_name());
        String name_dev = device_name_print.getText().toString();

        TextView deivce_status_print = findViewById(R.id.deivce_status_print);
        if(item.isPaid())
            deivce_status_print.setText(item.getIssue_status()+" , " + item.getPrice() + "£ , Paid");
        else
            deivce_status_print.setText(item.getIssue_status()+" , " + item.getPrice() + "£ , Not Paid");
        String device_status = device_name_print.getText().toString();

        TextView cust_name_print = findViewById(R.id.cust_name_print);
        cust_name_print.setText("Customer Name: " + item.getCustomer_name());
        String customer_name = cust_name_print.getText().toString();

        TextView cust_phone_print = findViewById(R.id.cust_phone_print);
        cust_phone_print.setText("Customer Phone: " + item.getCustomer_phone());
        String cust_phone = cust_phone_print.getText().toString();

        TextView device_id_print = findViewById(R.id.device_id_print);
        device_id_print.setText("Device ID: " + item.getDevice_ID());
        String device_id = device_id_print.getText().toString();

        TextView device_issue_print = findViewById(R.id.device_issue_print);
        device_issue_print.setText("Device Issue: " + item.getDevice_issue());
        String device_issue = device_issue_print.getText().toString();

        TextView device_password_print = findViewById(R.id.device_password_print);
        device_password_print.setText("Device Password: " + item.getDevice_password());
        String password = device_password_print.getText().toString();

        SimpleDateFormat sdf = new SimpleDateFormat("E, dd MMM yyyy HH:mm");
        TextView device_book_date_print = findViewById(R.id.device_book_date_print);
        device_book_date_print.setText("Booking Date: " + sdf.format(new Date(item.getBook_date())).toString());
        String book_date = device_book_date_print.getText().toString();

        TextView device_deliver_date_print = findViewById(R.id.device_deliver_date_print);
        device_deliver_date_print.setText("Delivery Date: " + sdf.format(new Date(item.getDelivery_date())).toString());
        String deliver_date = device_deliver_date_print.getText().toString();

        String Final_Str = "                   IT REPAIR          \n";
              Final_Str += "  61 HIGH STREET, PAISLEY, PA1 2AS    \n";
              Final_Str += "                  01413282049          \n\n";
              Final_Str += name_dev + "\n";
              Final_Str += device_status + "\n";
              Final_Str += customer_name + "\n";
              Final_Str += cust_phone + "\n";
              Final_Str += device_id + "\n";
              Final_Str += device_issue + "\n";
              Final_Str += password + "\n";
              Final_Str += book_date + "\n";
              Final_Str += deliver_date + "\n";

        ClipboardManager clipboard = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            clipboard = (ClipboardManager) getSystemService(ItemPrintActivity.this.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("receipt", Final_Str);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Added to clipboard", Toast.LENGTH_SHORT).show();

            String app_package = "com.brother.ptouch.iprintandlabel";
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage(app_package);
            if (launchIntent != null) {
                startActivity(launchIntent);
            }
        }

    }
    private void fillViews() {

        item = (Item) getIntent().getSerializableExtra("item_serialized");


        TextView device_name_print = findViewById(R.id.deivce_name_print);
        device_name_print.setText("Device Name: "+ item.getDevice_name());

        TextView deivce_status_print = findViewById(R.id.deivce_status_print);
        if(item.isPaid())
            deivce_status_print.setText(item.getIssue_status()+" , " + item.getPrice() + "£ , Paid");
        else
            deivce_status_print.setText(item.getIssue_status()+" , " + item.getPrice() + "£ , Not Paid");

        TextView cust_name_print = findViewById(R.id.cust_name_print);
        cust_name_print.setText("Customer Name: " + item.getCustomer_name());

        TextView cust_phone_print = findViewById(R.id.cust_phone_print);
        cust_phone_print.setText("Customer Phone: " + item.getCustomer_phone());

        TextView device_id_print = findViewById(R.id.device_id_print);
        device_id_print.setText("Device ID: " + item.getDevice_ID());

        TextView device_issue_print = findViewById(R.id.device_issue_print);
        device_issue_print.setText("Device Issue: " + item.getDevice_issue());

        TextView device_password_print = findViewById(R.id.device_password_print);
        device_password_print.setText("Device Password: " + item.getDevice_password());

        SimpleDateFormat sdf = new SimpleDateFormat("E, dd MMM yyyy HH:mm");
        TextView device_book_date_print = findViewById(R.id.device_book_date_print);
        device_book_date_print.setText("Booking Date: " + sdf.format(new Date(item.getBook_date())).toString());

        TextView device_deliver_date_print = findViewById(R.id.device_deliver_date_print);
        device_deliver_date_print.setText("Delivery Date: " + sdf.format(new Date(item.getDelivery_date())).toString());
    }


    public void createPdf(View view) {
        copyData();

        /*
        layoutToImage();
        try {
            imageToPDF();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/
    }

    public void layoutToImage() {
        // get view group using reference
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.main_relative);
        // convert view group to bitmap
        relativeLayout.setDrawingCacheEnabled(true);
        relativeLayout.buildDrawingCache();
        Bitmap bm = relativeLayout.getDrawingCache();
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File f = new File(Environment.getExternalStorageDirectory() + File.separator + "img.jpg");
        try {
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void imageToPDF() throws FileNotFoundException {
        try {
            Document document = new Document();

            dirpath = android.os.Environment.getExternalStorageDirectory().toString();
            dirpath +=  "/" + item.getDevice_ID() + ".pdf";
            PdfWriter.getInstance(document, new FileOutputStream(dirpath)); //  Change pdf's name.
            document.open();
            Image img = Image.getInstance(Environment.getExternalStorageDirectory() + File.separator + "img.jpg");
            float scalar = ((document.getPageSize().getWidth() - document.leftMargin()
                    - document.rightMargin() - 0) / img.getWidth()) * 100;
            img.scalePercent(scalar);
            img.setAlignment(Image.ALIGN_CENTER | Image.ALIGN_TOP);
            document.add(img);
            document.close();
            Toast.makeText(this, "PDF Generated successfully!..", Toast.LENGTH_SHORT).show();
            File file = new File(dirpath);
            openPDF(file);
        } catch (Exception e) {
            Toast.makeText(this, "PDF Failed!..", Toast.LENGTH_SHORT).show();
        }
    }

    private void openPDF(File file){
        Uri pdf = FileProvider.getUriForFile(ItemPrintActivity.this, "com.example.fixtech", file);
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pdfIntent.setDataAndType(pdf, "application/pdf");
        try {
            startActivity(pdfIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(ItemPrintActivity.this, "Error opening file", Toast.LENGTH_SHORT).show();
        }
    }

}