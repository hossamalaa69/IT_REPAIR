package com.example.fixtech;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.BuildConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ItemsVH> {

    private static final String TAG = "ItemsAdapter";
    List<Item> itemList;
    Context context;

    public ItemsAdapter(Context context, List<Item> itemsList) {
        this.context = context;
        this.itemList = itemsList;
    }

    @NonNull
    @Override
    public ItemsAdapter.ItemsVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_product, parent, false);
        return new ItemsAdapter.ItemsVH(view);
    }

    private void printPDF(Item item) {

        PdfDocument myPdfDocument = new PdfDocument();
        PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(62,85,1).create();
        PdfDocument.Page myPage = myPdfDocument.startPage(myPageInfo);

        int x = 4, y = 7;
        Paint myPaint = new Paint();

        //Admin Name
        myPaint.setTextSize(9.0f);
        myPaint.setTextScaleX(0.5f);

        myPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        myPaint.setTextAlign(Paint.Align.CENTER);
        myPaint.setColor(Color.RED);
        String myString = "IT  REPAIR";
        myPage.getCanvas().drawText(myString, myPageInfo.getPageWidth()/2, y, myPaint);
        y+=5;

        //Address Part
        myPaint.setTextSize(5f);
        myPaint.setTextScaleX(0.5f);
        myPaint.setColor(Color.GRAY);
        myPaint.setTextAlign(Paint.Align.CENTER);
        myString = "61 HIGH STREET, PAISLEY, PA1 2AS";
        myPage.getCanvas().drawText(myString, myPageInfo.getPageWidth()/2, y, myPaint);
        y+=5;

        //Phone part
        myString = "01413282049";
        myPage.getCanvas().drawText(myString, myPageInfo.getPageWidth()/2, y, myPaint);
        y+=7;


        myPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

        int spacing = 7;
        //Device Name
        myPaint.setTextSize(6.0f);
        myPaint.setTextAlign(Paint.Align.LEFT);
        myPaint.setColor(Color.BLACK);
        myPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        myString = "Device:  " + item.getDevice_name();
        myPage.getCanvas().drawText(myString, x, y, myPaint);
        y+=spacing;

        //Payment
        myString = item.getIssue_status() + ",  ";
        myString += item.getPrice() + "£  ";
        if(item.isPaid())
            myString += "Paid";
        else
            myString += "Not Paid";
        myPage.getCanvas().drawText(myString, x, y, myPaint);
        y+=spacing;


        //Customer Name
        myString = "Customer:  " + item.getCustomer_name();
        myPage.getCanvas().drawText(myString, x, y, myPaint);
        y+=spacing;

        //Customer Phone
        myString = "Phone:  " + item.getCustomer_phone();
        myPage.getCanvas().drawText(myString, x, y, myPaint);
        y+=spacing;

        //Device ID
        myString = "ID:  " + item.getDevice_ID();
        myPage.getCanvas().drawText(myString, x, y, myPaint);
        y+=spacing;

        //Device Issue
        myString = "Issue:  " + item.getDevice_issue();
        myPage.getCanvas().drawText(myString, x, y, myPaint);
        y+=spacing;


        //Device Password
        myString = "Password:  " + item.getDevice_password();
        myPage.getCanvas().drawText(myString, x, y, myPaint);
        y+=spacing;

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm");
        //Device booking date
        myString = "Booking:  " + sdf.format(new Date(item.getBook_date())).toString();
        myPage.getCanvas().drawText(myString, x, y, myPaint);
        y+=spacing;

        //Device booking date
        myString = "Delivery:  " + sdf.format(new Date(item.getDelivery_date())).toString();
        myPage.getCanvas().drawText(myString, x, y, myPaint);

        myPdfDocument.finishPage(myPage);

        String myFilePath = Environment.getExternalStorageDirectory().getPath() + "/" + item.getDevice_ID() + ".pdf";
        File myFile = new File(myFilePath);
        try {
            myPdfDocument.writeTo(new FileOutputStream(myFile));
            Toast.makeText(context, "PDF is created successfully", Toast.LENGTH_SHORT).show();
            openPDF(myFile);
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(context, "Error Printing PDF", Toast.LENGTH_SHORT).show();
        }
        myPdfDocument.close();
    }

    private void shareBluetooth(File myFile) {
        // generate URI, I defined authority as the application ID in the Manifest, the last param is file I want to open
        Uri uri = FileProvider.getUriForFile(context, "com.example.fixtech", myFile);

        try {
            Intent sendIntent = new Intent();
            sendIntent.setType("file/*");
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_STREAM,
                    uri);
            // startActivity(Intent.createChooser(sendIntent));
            context.startActivity(Intent.createChooser(sendIntent,
                    "Share file via:"));
        }
        catch(Exception e)
        {
            Toast.makeText(context, "Failed sending bluetooth", Toast.LENGTH_SHORT).show();
        }

    }

    private void openPDF(File file){

        Uri pdf = FileProvider.getUriForFile(context, "com.example.fixtech", file);

        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pdfIntent.setDataAndType(pdf, "application/pdf");

        try {
            context.startActivity(pdfIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "Error opening file", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ItemsAdapter.ItemsVH holder, int position) {

        Item item = itemList.get(position);

        String isPaid = "Paid";
        if(!item.isPaid())
            isPaid = "Not Paid";

        GradientDrawable priorityCircle = (GradientDrawable) holder.status_txt.getBackground();
        // Get the appropriate background color based on the priority
        int priorityColor = getIssueColor(item.getIssue_status());
        priorityCircle.setColor(priorityColor);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss");

        holder.item_name.setText(item.getDevice_name());
        holder.customer_name_txt.setText(item.getCustomer_name());
        holder.user_email.setText(item.getCustomer_email());
        holder.user_phone.setText(item.getCustomer_phone());
        holder.payment_txt.setText(isPaid);
        holder.device_id_txt2.setText(item.getDevice_ID());
        holder.device_issue_txt2.setText(item.getDevice_issue());
        holder.device_password_txt2.setText(item.getDevice_password());
        holder.booking_txt.setText(simpleDateFormat.format(new Date(item.getBook_date())).toString());
        holder.deliver_txt.setText(simpleDateFormat.format(new Date(item.getDelivery_date())).toString());
        holder.price_txt.setText("" + item.getPrice() + "£ ,");
        if(item.getIssue_status().equals("Fixed"))
            holder.status_txt.setText("✓");
        else
            holder.status_txt.setText("✘");

        if(item.getImage_url() == null){
            holder.item_image.setImageResource(R.drawable.ic_image_placeholder);
        }else{
            Glide.with(context)
                    .load(item.getImage_url())
                    .placeholder(R.drawable.ic_image_placeholder)
                    .into(holder.item_image);
        }

        boolean isExpanded = itemList.get(position).isExpanded();
        holder.expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.drop_button.setImageResource(isExpanded? R.drawable.ic_collapse : R.drawable.ic_expand);
    }

    /**
     * When data changes, this method updates the list of taskEntries
     * and notifies the adapter to use the new values on it
     */
    public void setItemList(List<Item> items) {
        itemList = items;
        notifyDataSetChanged();
    }

    public List<Item> getItems(){
        return itemList;
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }

    private int getIssueColor(String status) {
        int priorityColor = 0;
        switch (status) {
            case "Fixed":
                priorityColor = ContextCompat.getColor(context, R.color.colorGreen);
                break;
            case "Not Fixed":
                priorityColor = ContextCompat.getColor(context, R.color.colorRed);
                break;
            default:
                break;
        }
        return priorityColor;
    }


    class ItemsVH extends RecyclerView.ViewHolder {

        private static final String TAG = "ItemsVH";

        ImageView item_image, drop_button, callIcon, mailIcon, share_img;
        TextView item_name, status_txt, customer_name_txt, user_email, user_phone
                , payment_txt, device_id_txt2, device_issue_txt2, device_password_txt2
                ,booking_txt, deliver_txt, price_txt;

        ConstraintLayout expandableLayout;
        RelativeLayout mainView;

        public ItemsVH(@NonNull final View itemView) {
            super(itemView);

            item_image = (ImageView) itemView.findViewById(R.id.item_image);
            drop_button = (ImageView) itemView.findViewById(R.id.drop_button);
            callIcon = (ImageView) itemView.findViewById(R.id.image_phone);
            mailIcon = (ImageView) itemView.findViewById(R.id.image_email);
            share_img = (ImageView) itemView.findViewById(R.id.share_img);

            item_name = (TextView) itemView.findViewById(R.id.item_name);
            status_txt = (TextView) itemView.findViewById(R.id.status_txt);
            customer_name_txt = (TextView) itemView.findViewById(R.id.customer_name_txt);
            user_email = (TextView) itemView.findViewById(R.id.user_email);
            user_phone = (TextView) itemView.findViewById(R.id.user_phone);
            payment_txt = (TextView) itemView.findViewById(R.id.payment_txt);
            device_id_txt2 = (TextView) itemView.findViewById(R.id.device_id_txt2);
            device_issue_txt2 = (TextView) itemView.findViewById(R.id.device_issue_txt2);
            device_password_txt2 = (TextView) itemView.findViewById(R.id.device_password_txt2);
            booking_txt = (TextView) itemView.findViewById(R.id.booking_txt);
            deliver_txt = (TextView) itemView.findViewById(R.id.deliver_txt);
            price_txt = (TextView) itemView.findViewById(R.id.price_txt);

            expandableLayout = itemView.findViewById(R.id.expandableLayout);
            mainView = (RelativeLayout) itemView.findViewById(R.id.main_view);

            mainView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Item item = itemList.get(ItemsVH.this.getAdapterPosition());
                    item.setExpanded(!item.isExpanded());
                    notifyItemChanged(ItemsVH.this.getAdapterPosition());
                }
            });


            callIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(Intent.ACTION_DIAL);
                    i.setData(Uri.parse("tel:"+user_phone.getText().toString()));
                    context.startActivity(i);
                }
            });

            mailIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:"+user_email.getText().toString()));
                    if (intent.resolveActivity(context.getPackageManager()) != null) {
                        context.startActivity(intent);
                    }
                }
            });

            share_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Item item = itemList.get(ItemsVH.this.getAdapterPosition());
                    printPDF(item);
                }
            });
        }
    }
}