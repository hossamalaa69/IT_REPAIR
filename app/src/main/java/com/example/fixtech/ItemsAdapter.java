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
import android.os.Bundle;
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
        Intent intent = new Intent(context, ItemPrintActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("item_serialized",item);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemsAdapter.ItemsVH holder, int position) {

        Item item = itemList.get(position);

        GradientDrawable priorityCircle = (GradientDrawable) holder.status_txt.getBackground();
        // Get the appropriate background color based on the priority
        int priorityColor = getPaymentColor(item.isPaid());
        priorityCircle.setColor(priorityColor);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss");

        holder.item_name.setText(item.getDevice_name());
        holder.customer_name_txt.setText(item.getCustomer_name());
        holder.user_email.setText(item.getCustomer_email());
        holder.user_phone.setText(item.getCustomer_phone());
        holder.payment_txt.setText(item.getIssue_status());
        holder.device_id_txt2.setText(item.getDevice_ID());
        holder.device_issue_txt2.setText(item.getDevice_issue());
        holder.device_password_txt2.setText(item.getDevice_password());
        holder.booking_txt.setText(simpleDateFormat.format(new Date(item.getBook_date())).toString());
        holder.deliver_txt.setText(simpleDateFormat.format(new Date(item.getDelivery_date())).toString());
        holder.price_txt.setText("" + item.getPrice() + "£ ,");

        if(item.isPaid())
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

    private int getPaymentColor(boolean isPaid) {
        int priorityColor = 0;
        if(isPaid)
            priorityColor = ContextCompat.getColor(context, R.color.colorGreen);
        else
            priorityColor = ContextCompat.getColor(context, R.color.colorRed);

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