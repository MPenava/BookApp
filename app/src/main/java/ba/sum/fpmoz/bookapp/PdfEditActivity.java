package ba.sum.fpmoz.bookapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import ba.sum.fpmoz.bookapp.databinding.ActivityPdfEditBinding;

public class PdfEditActivity extends AppCompatActivity {

    private ActivityPdfEditBinding binding;

    private String bookAuthor;

    private ProgressDialog progressDialog;

    private static final String TAG = "BOOK_EDIT_TAG";
    private Log log;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdfEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bookAuthor = getIntent().getStringExtra("bookAuthor");

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Molimo pričekajte");
        progressDialog.setCanceledOnTouchOutside(false);

        loadBookInfo();


        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
                onBackPressed();
            }
        });


    }

    private void loadBookInfo() {
        log.d(TAG,"loadBookInfo:Pričekajte..");

        DatabaseReference refBooks= FirebaseDatabase.getInstance().getReference("Books");
        refBooks.child(bookAuthor)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String description=""+snapshot.child("description").getValue();
                        String title=""+snapshot.child("title").getValue();
                        String author=""+snapshot.child("author").getValue();

                        binding.titleEt.setText(title);
                        binding.descriptionEt.setText(description);
                        binding.authorTv.setText(author);
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private String title="",description="",author="";
    private void validateData(){
        title = binding.titleEt.getText().toString().trim();
        description = binding.descriptionEt.getText().toString().trim();
        author = binding.authorTv.getText().toString().trim();

        if(TextUtils.isEmpty(title)){
            Toast.makeText(this, "Unesite naslov..", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(description)){
            Toast.makeText(this, "Unesite opis..", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(author)){
            Toast.makeText(this, "Unesite autora", Toast.LENGTH_SHORT).show();
        }
        else{
            updatePdf();
        }

    }

    private void updatePdf() {
        Log.d(TAG, "updatePdf: Ažuriranje pdf u bazu...");
        progressDialog.setMessage("Ažuriranje knjige...");
        progressDialog.show();

        HashMap<String,Object >hashMap=new HashMap<>();
        hashMap.put("title",""+title);
        hashMap.put("description",""+description);
        hashMap.put("author",""+author);

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookAuthor)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG,"onSuccess: Ažuriranje knjige..");
                        progressDialog.dismiss();
                        Toast.makeText(PdfEditActivity.this, "Ažuriranje..", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure:Ažuriranje nije uspješno"+e.getMessage());
                        progressDialog.dismiss();
                        Toast.makeText(PdfEditActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
