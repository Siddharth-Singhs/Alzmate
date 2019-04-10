package com.example.android.alzmate.Model;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.alzmate.R;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class CameraFragment extends android.support.v4.app.Fragment {

    private Button camerabtn;
    private Button identifyfacebtn;
    private ImageView photoView;
    private static int RESULT_CAPTURE_IMAGE = 0;
    public static Bitmap imageBitmap;
    private static int RESULT_LOAD_IMAGE = 1;
    public Uri selectedImage;
    Dialog myDialog;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View retView = inflater.inflate(R.layout.fragment_camera, container, false);
        return retView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        camerabtn=(Button)view.findViewById(R.id.camera_btn);
        identifyfacebtn=(Button)view.findViewById(R.id.identify_face_btn);
        photoView=(ImageView)view.findViewById(R.id.image_detect_view);
        myDialog = new Dialog(this.getContext());

        camerabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, RESULT_CAPTURE_IMAGE);
            }
        });
        identifyfacebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageBitmap!=null) {
                    ShowPopup(view);
                }
            }
        });

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==RESULT_CAPTURE_IMAGE && resultCode==RESULT_OK && null!=data)
        {
            imageBitmap = (Bitmap) data.getExtras().get("data");
            photoView.setImageBitmap(imageBitmap);
        }
    }
    public void ShowPopup(View v) {
        TextView txtclose;
        Button btnFollow;
        myDialog.setContentView(R.layout.activity_identifyface);
        txtclose =(TextView) myDialog.findViewById(R.id.txtclose);
        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }
}
