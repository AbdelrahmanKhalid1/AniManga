package com.example.animanga;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.animanga.data.FirebaseField;
import com.example.animanga.data.Item;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

public class AddAnimangaActivity extends AppCompatActivity {

    private final int REQUEST_GALLERY = 5;
    private int addCounter = 0;
    private Item item;
    private Uri imageUri;
    private ProgressBar mProgressBar;

    private StorageReference mStorageRef;
    private StorageTask mUploadTask;
    boolean isAdded;
//    private DatabaseReference mDatabaseRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_animanga);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("ADD NEW");
        item = new Item();
        isAdded = false;
        mProgressBar = findViewById(R.id.progress_bar);

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");

    }


    public void done(View view) {
        if (!validateCat() || !validateName() | !validateStatus() | !validateLink() | !validateStatus() | !validateDesc() | !validatePoster()) {
            return;
        }
        if (mUploadTask != null && mUploadTask.isInProgress()) {
            Toast.makeText(AddAnimangaActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
            return;
        }
        upLoadItem();

    }

    private void isDone(){
        if (isAdded) {
            Toast.makeText(this, "Added Successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, item.getItemId(), Toast.LENGTH_SHORT).show();
            if (++addCounter == 3) {
                finish();
            }
        }
    }

    private void upLoadItem() {
        if (imageUri != null) {
            final StorageReference fileReference = mStorageRef.child(item.getName().trim()
                    + "." + getFileExtension());

            mUploadTask = fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(0);
                                }
                            }, 500);

                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    item.setPic(uri.toString());
                                    isAdded = FirebaseField.addItem(FirebaseField.isAnime(item.getCategory()), item);
                                    isDone();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mProgressBar.setProgress((int) progress);
                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateCat() {
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.radio_anime:
                item.setCategory(true);
                return true;
            case R.id.radio_manga:
                item.setCategory(false);
                return true;
            default:
                Toast.makeText(this, "Chose Category", Toast.LENGTH_SHORT).show();
                return false;
        }
    }

    private boolean validateName() {
        EditText editText = findViewById(R.id.edit_text_name);
        item.setName(editText.getText().toString());
        if (item.getName().isEmpty()) {
            editText.setError("Enter Name");
            return false;
        }
        return true;
    }

    private boolean validateLink() {
        EditText editText = findViewById(R.id.edit_text_link);
        item.setLink(editText.getText().toString().trim());
        if (item.getLink().isEmpty()) {
            editText.setError("Enter Link");
            return false;
        }
        return true;
    }

    private boolean validateDesc() {
        EditText editText = findViewById(R.id.edit_text_desc);
        item.setDesc(editText.getText().toString());
        if (item.getDesc().isEmpty()) {
            editText.setError("Enter Desc");
            return false;
        }
        return true;
    }

    private boolean validatePoster() {
        if (imageUri == null) {
            Toast.makeText(this, "Upload Pic", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validateStatus() {
        RadioGroup radioGroup = findViewById(R.id.radio_group_status);
        int radioId = radioGroup.getCheckedRadioButtonId();
        switch (radioId) {
            case R.id.radio_done:
                item.setStatus(true);
                return true;
            case R.id.radio_undone:
                item.setStatus(false);
                return true;
            default:
                Toast.makeText(this, "Check Status", Toast.LENGTH_SHORT).show();
                return false;
        }
    }

    public void uploadPic(View view) {
        if (isStorageAllowed()) {
            Intent intent1 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent1.setType("image/*");
            startActivityForResult(Intent.createChooser(intent1, "Select Image"), REQUEST_GALLERY);
        }
    }

    private boolean isStorageAllowed() {
        boolean isGranted;
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            isGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_GALLERY);
            isGranted = false;
        }
        String TAG = "AddAnimangaActivity";
        Log.d(TAG, "isPermissionAllowed: " + isGranted);
        return isGranted;
    }

    //a3ml search 3liha yacta
    private String getFileExtension() {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(imageUri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK &&
                requestCode == REQUEST_GALLERY &&
                data != null && data.getData() != null) {

            imageUri = data.getData();
            ImageButton imageButton = findViewById(R.id.image_btn);
            imageButton.setVisibility(View.INVISIBLE);
            ImageView imageView = findViewById(R.id.add_poster);
            imageView.setImageURI(imageUri);
        }
    }

}

//msh fakir bt3ml eh h3ml search 3liha
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//            Intent intent1;
//            switch (requestCode) {
//                case REQUEST_GALLERY:
//                    intent1 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                    intent1.setType("image/*");
//                    startActivityForResult(intent1.createChooser(intent1, "Select Image"), REQUEST_GALLERY);
//                    return;
//            }
//        }
//    }

