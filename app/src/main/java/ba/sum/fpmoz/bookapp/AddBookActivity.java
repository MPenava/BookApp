package ba.sum.fpmoz.bookapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import ba.sum.fpmoz.bookapp.databinding.ActivityAddBookBinding;


public class AddBookActivity extends AppCompatActivity {

    private ActivityAddBookBinding binding;

    ImageButton backBtn, attachPdf;
    Button submitBtn;

    private String title = "", description = "", author = "";

    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;

    private Uri pdfUri = null;

    private static final int PDF_PICK_CODE = 1000;

    private static final String TAG ="ADD_PDF_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();

        binding = ActivityAddBookBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Molimo pričekajte");
        progressDialog.setCanceledOnTouchOutside(false);

        backBtn = (ImageButton) findViewById(R.id.backBtn);
        //Povrat na prošlu stranicu
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        
        //Dohvat pdf dokumenta
        attachPdf = (ImageButton) findViewById(R.id.attachPdf);
        attachPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pdfPickIntent();
            }
        });

        submitBtn =  findViewById(R.id.submitBtn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
    }

    private void validateData(){
        Log.d(TAG, "validateData: validating data...");

        title = binding.titleInputText.getText().toString().trim();
        description = binding.descriptionInputText.getText().toString().trim();
        author = binding.authorInputText.getText().toString().trim();

        if(TextUtils.isEmpty(title)){
            Toast.makeText(this, "Unesite naslov...", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "validateData: title prazan");
        }else if(TextUtils.isEmpty(description)){
            Toast.makeText(this, "Unesite opis...", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(author)){
            Toast.makeText(this, "Unesite autora...", Toast.LENGTH_SHORT).show();
        }else if(pdfUri == null){
            Toast.makeText(this, "Odaberite pdf...", Toast.LENGTH_SHORT).show();
        }else{
           uploadPdfToStorage(); 
        }
    }

    private void uploadPdfToStorage() {
        Log.d(TAG, "uploadPdfToStorage: dodavanje u pohranu");

        progressDialog.setMessage("Dodavanje PDFa..");
        progressDialog.show();

        long timestamp = System.currentTimeMillis();

        String filePathAndName = "Books/" + timestamp;

        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
        storageReference.putFile(pdfUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "onSuccess: uspješno dodano");
                Log.d(TAG, "onSuccess: dohvat pdf urla");

                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while(!uriTask.isSuccessful());
                String uploadedPdfUrl = ""+uriTask.getResult();
                
                uploadPdfInfoToDb(uploadedPdfUrl, timestamp);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Log.d(TAG, "onFailture: greška pri dodavanju pdf " + e.getMessage());
                Toast.makeText(AddBookActivity.this, "Dodavanje PDFa greška " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void uploadPdfInfoToDb(String uploadedPdfUrl, long timestamp) {
        Log.d(TAG, "uploadPdfToStorage: dodavanje u bazu");

        progressDialog.setMessage("Dodavanje informacija o pdfu");

        String uid = firebaseAuth.getUid();


        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", ""+uid);
        hashMap.put("id", ""+timestamp);
        hashMap.put("title", ""+title);
        hashMap.put("description", ""+description);
        hashMap.put("author", ""+author);
        hashMap.put("url", ""+uploadedPdfUrl);
        hashMap.put("timestamp", ""+timestamp);

        DatabaseReference ref = FirebaseDatabase.getInstance("https://bookapp-a9588-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Books");
        ref.child(""+timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onSuccess: Uspješno dodavanje u bazu ");
                        Toast.makeText(AddBookActivity.this, "Uspješno dodavanje u bazu ", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onFailture: Neuspješno dodavanje u bazu " + e.getMessage());
                        Toast.makeText(AddBookActivity.this, "Neuspješno dodavanje u bazu " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void pdfPickIntent() {
        Log.d(TAG, "pdfPickIntent: početak odabira pdf dokumta");
        Intent i = new Intent();
        i.setType("application/pdf");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Odaberite PDF"), PDF_PICK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == PDF_PICK_CODE){
                Log.d(TAG, "onActivityResult: PDF odabran");

                pdfUri = data.getData();

                Log.d(TAG, "onActivityResult: URI: " +pdfUri);
            }
        } else{
            Log.d(TAG, "onActivityResult: zatvaranje odabira");
            Toast.makeText(this, "zatvaranje odabira pdf", Toast.LENGTH_SHORT).show();
        }
    }
}