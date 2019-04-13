package com.example.android.alzmate.Model;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.alzmate.PersonDetail.UnKnownPersonHolder;
import com.example.android.alzmate.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import edmt.dev.edmtdevcognitiveface.Contract.Face;
import edmt.dev.edmtdevcognitiveface.Contract.IdentifyResult;
import edmt.dev.edmtdevcognitiveface.Contract.Person;
import edmt.dev.edmtdevcognitiveface.FaceServiceClient;
import edmt.dev.edmtdevcognitiveface.FaceServiceRestClient;

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
    public String name = "unknown";
    public String relationship;
    public String imageURL;
    public String bio;
    private FaceServiceClient faceServiceClient = new FaceServiceRestClient("https://westcentralus.api.cognitive.microsoft.com/face/v1.0", "18d5469e5db94a2f83503c440f3ef98c");
    private final String personGroupId = "relatives";
    private final String personGroupName = "close relatives";
    Face[] facesDetected;
    public ProgressDialog progressDialog;
    private Button detectFace;
    private DatabaseReference identifyDatabase;
    private StorageReference unKnownStorageRef;
    private FirebaseAuth mAuth;
    private String unknownImageURL;
    private String currentDate;
    private String currentActualTime;
    private String latitude;
    private String longitude;
    private DatabaseReference unKnownPeopleReference;
    private GoogleApiClient mGoogleApiClient;
    private FusedLocationProviderClient fusedLocationClient;

    class matchPeople extends AsyncTask {

        @Override
        protected Void doInBackground(Object... objects) {
            ValueEventListener valueEventListener = identifyDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot findedPerson : dataSnapshot.getChildren()) {
                        if (findedPerson.getKey().toString().equals(name)) {

                            relationship = findedPerson.child("relation").getValue().toString();
                            imageURL = findedPerson.child("ImgLocation").getValue().toString();
                            bio = findedPerson.child("bio").getValue().toString();
                            ShowPopup();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });
            return null;
        }

        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Object o) {
            progressDialog.dismiss();

        }
    }

    class detectTask extends AsyncTask<InputStream, String, Face[]> {

        @Override
        protected Face[] doInBackground(InputStream... params) {
            try {
                progressDialog.show();
                publishProgress("Detecting...");
                Face[] results = faceServiceClient.detect(params[0], true, false, null);
                if (results == null) {
                    publishProgress("Detection Finished. Nothing detected");
                    return null;
                } else {
                    publishProgress(String.format("Detection Finished. %d face(s) detected", results.length));
                    return results;
                }
            } catch (Exception ex) {
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Face[] faces) {
            progressDialog.dismiss();

            facesDetected = faces;
            final UUID[] faceIds = new UUID[facesDetected.length];
            for (int i = 0; i < facesDetected.length; i++) {
                faceIds[i] = facesDetected[i].faceId;
            }
            new IdentificationTask(personGroupId).execute(faceIds);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            progressDialog.setMessage(values[0]);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View retView = inflater.inflate(R.layout.fragment_camera, container, false);
        return retView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        camerabtn = (Button) view.findViewById(R.id.camera_btn);
        identifyfacebtn = (Button) view.findViewById(R.id.identify_face_btn);
        photoView = (ImageView) view.findViewById(R.id.image_detect_view);
        myDialog = new Dialog(this.getContext());
        mAuth = FirebaseAuth.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                latitude=Double.toString(location.getLatitude());
                                longitude=Double.toString(location.getLongitude());
                            }
                        }
                    });
        }
        FirebaseUser user=mAuth.getCurrentUser();
        progressDialog=(ProgressDialog) new ProgressDialog(this.getContext());
        identifyDatabase= FirebaseDatabase.getInstance().getReference().child("PersonAlz").child(user.getUid()).child("known-people");
        unKnownPeopleReference=FirebaseDatabase.getInstance().getReference().child("PersonAlz").child(user.getUid()).child("unknown-people");
        unKnownStorageRef = FirebaseStorage.getInstance().getReference();
        camerabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name="unknown";
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, RESULT_CAPTURE_IMAGE);
            }
        });

        identifyfacebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageBitmap!=null && name=="unknown") {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
                    new detectTask().execute(inputStream);
                }
                else
                {
                    photoView.setImageBitmap(imageBitmap);
                    ShowPopup();
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
    public void UnknownShowPopup()
    {

        myDialog.setContentView(R.layout.unknownface);
        ImageView imageView=(ImageView)myDialog.findViewById(R.id.profile_image);
        imageView.setImageBitmap(imageBitmap);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
        progressDialog.show();
        ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos1);
        //byte[] data = baos.toByteArray();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(baos1.toByteArray());
        unKnownStorageRef.child("unknown/"+UUID.randomUUID().toString()).putStream(inputStream).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                unknownImageURL=taskSnapshot.getDownloadUrl().toString();
                Date currentTime = Calendar.getInstance().getTime();
                String currentClockTime=currentTime.toString();
                currentDate=currentClockTime.split(" ")[2]+"-April-2019";
                currentActualTime=currentClockTime.split(" ")[3];
                UnKnownPersonHolder unKnownPersonHolder=new UnKnownPersonHolder(unknownImageURL,currentDate,currentActualTime,longitude,latitude);
                unKnownPeopleReference.child(UUID.randomUUID().toString()).setValue(unKnownPersonHolder).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.setMessage("Notifying the careTaker");
            }
        });
    }
    public void NoShowPopup()
    {
        myDialog.setContentView(R.layout.nofacedetected);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();

    }
    public void ShowPopup() {
        myDialog.setContentView(R.layout.activity_identifyface);
        ImageView imageView=(ImageView)myDialog.findViewById(R.id.profile_image);
        TextView  nameView=(TextView)myDialog.findViewById(R.id.name_get_text);
        TextView relationshipView=(TextView)myDialog.findViewById(R.id.relationship_get_text);
        TextView bioView=(TextView)myDialog.findViewById(R.id.bio_get_text);

        nameView.setText(name);
        relationshipView.setText(relationship);
        bioView.setText(bio);
        Glide.with(getContext()).load(imageURL).into(imageView);
        TextView txtclose =(TextView) myDialog.findViewById(R.id.txtclose);
        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }
    private class IdentificationTask extends AsyncTask<UUID,String,IdentifyResult[]> {
        String personGroupId;


        public IdentificationTask(String personGroupId) {
            this.personGroupId = personGroupId;
        }

        @Override
        protected IdentifyResult[] doInBackground(UUID... params) {
                progressDialog.show();
            try{
                publishProgress("First Sex Report");
                /**TrainingStatus trainingStatus  = faceServiceClient.getPersonGroupTrainingStatus(this.personGroupId);
                if(trainingStatus.status != TrainingStatus.Status.Succeeded)
                {
                    publishProgress("Person group training status is "+trainingStatus.status);
                    return null;
                }**/
                publishProgress("Identifying...");

                IdentifyResult[] results = faceServiceClient.identity(personGroupId, // person group id
                        params // face ids
                        ,1); // max number of candidates returned

                return results;

            } catch (Exception e)
            {
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(IdentifyResult[] identifyResults) {
            progressDialog.dismiss();
            if(identifyResults!=null) {
                try {
                    for (IdentifyResult identifyResult : identifyResults) {
                        new PersonDetectionTask(personGroupId).execute(identifyResult.candidates.get(0).personId);
                    }
                }
                catch (Exception e)
                {
                    UnknownShowPopup();
                }
            }
            else
            {
                NoShowPopup();
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            progressDialog.setMessage(values[0]);
        }
    }

    private class PersonDetectionTask extends AsyncTask<UUID,String,Person> {
        private String personGroupId;

        public PersonDetectionTask(String personGroupId) {
            this.personGroupId = personGroupId;
        }

        @Override
        protected Person doInBackground(UUID... params) {
            try{
                publishProgress("Searching the People");

                return faceServiceClient.getPerson(personGroupId,params[0]);
            } catch (Exception e)
            {
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Person person) {
                name = person.name;
                progressDialog.dismiss();
                new matchPeople().execute();

        }

        @Override
        protected void onProgressUpdate(String... values) {
            progressDialog.setMessage(values[0]);
        }
    }
}

