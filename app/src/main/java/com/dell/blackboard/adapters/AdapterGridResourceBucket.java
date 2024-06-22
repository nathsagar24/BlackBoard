package com.dell.blackboard.adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.dell.blackboard.PhotoFullPopupWindow;
import com.dell.blackboard.R;
import com.dell.blackboard.objects.ResourceBucketObject;

import java.io.File;
import java.util.ArrayList;

import static com.dell.blackboard.Common.CURRENT_USER;
import static com.dell.blackboard.Common.mREF_RESOURCE_BUCKET;

public class AdapterGridResourceBucket extends BaseAdapter {

    private LayoutInflater layoutinflater;
    private ArrayList<ResourceBucketObject> listStorage;
    private Context context;
    ProgressDialog progressDialog;
    public AdapterGridResourceBucket(Context context, ArrayList<ResourceBucketObject> customizedListView) {
        this.context = context;
        layoutinflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listStorage = customizedListView;
    }

    @Override
    public int getCount() {
        return listStorage.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder listViewHolder;
        if(convertView == null){
            listViewHolder = new ViewHolder();
            convertView = layoutinflater.inflate(R.layout.resource_bucket_grid_row, parent, false);
            listViewHolder.textInListView = convertView.findViewById(R.id.tv_grid_view_bucket);
            listViewHolder.imageInListView = convertView.findViewById(R.id.img_grid_view_bucket);
            convertView.setTag(listViewHolder);
        }else{
            listViewHolder = (ViewHolder)convertView.getTag();
        }
        listViewHolder.textInListView.setText(listStorage.get(position).file_name);
        if (listStorage.get(position).file_type.equals("photo")){
            Glide.with(context).load(listStorage.get(position).file_link).into(listViewHolder.imageInListView);
            listViewHolder.imageInListView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = listStorage.get(position).file_link;
                    new PhotoFullPopupWindow(context, R.layout.popup_photo_full, v, url, null);
                }
            });
        }
        if (listStorage.get(position).file_type.equals("pdf")){
            listViewHolder.imageInListView.setImageResource(R.drawable.pdf_icon);
            listViewHolder.imageInListView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                    browserIntent.setDataAndType(Uri.parse(listStorage.get(position).file_link),"application/pdf");

                    Intent chooser = Intent.createChooser(browserIntent, "Pdf Viewer");
                    chooser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // optional

                    context.startActivity(chooser);
                }
            });
        }
        listViewHolder.imageInListView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (CURRENT_USER.AdminLevel.equals("admin")){
                    facultyLongClickOption(position);
                }
                if (CURRENT_USER.AdminLevel.equals("user")){
                    studentLongClickOption(position);
                }
                return true;
            }
        });

        return convertView;
    }

    static class ViewHolder{
        TextView textInListView;
        ImageView imageInListView;
    }

    private void facultyLongClickOption(int position){
        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Delete or Download?")
                .setMessage("Deletion cannot be undone !\nDownloaded File can be found in folder 'BlackBoard'.")
                .setPositiveButton("Download", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "BlackBoard");
                        if (!mediaStorageDir.exists()) {
                            if (!mediaStorageDir.mkdirs()) {
                                Toast.makeText(context, "Failed to Create Folder", Toast.LENGTH_SHORT).show();
                            }
                        }
                        StorageReference ref = FirebaseStorage.getInstance()
                                .getReferenceFromUrl(listStorage.get(position).file_link);
                        final File localFile = new File(mediaStorageDir,listStorage.get(position).file_name);
                        ref.getFile(localFile);
                        Toast.makeText(context, "Downloaded", Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        StorageReference ref = FirebaseStorage.getInstance()
                                .getReferenceFromUrl(listStorage.get(position).file_link);
                        ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                                mREF_RESOURCE_BUCKET.child(listStorage.get(position).file_UID).removeValue();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).show();
    }

    private void studentLongClickOption(int position){
        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Download File?")
                .setMessage("Files can be found in the folder 'BlackBoard'.")
                .setPositiveButton("Download", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "BlackBoard");
                        if (!mediaStorageDir.exists()) {
                            if (!mediaStorageDir.mkdirs()) {
                                Toast.makeText(context, "Failed to Create Folder", Toast.LENGTH_SHORT).show();
                            }
                        }
                        StorageReference ref = FirebaseStorage.getInstance()
                                .getReferenceFromUrl(listStorage.get(position).file_link);
                        final File localFile = new File(mediaStorageDir,listStorage.get(position).file_name);
                        ref.getFile(localFile);
                        Toast.makeText(context, "Downloaded", Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("Cancel",null).show();
    }


}