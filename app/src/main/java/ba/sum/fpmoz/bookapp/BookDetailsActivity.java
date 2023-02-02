package ba.sum.fpmoz.bookapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.PackageManagerCompat;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;

import ba.sum.fpmoz.bookapp.adapter.BookAdapter;
import ba.sum.fpmoz.bookapp.model.Book;

public class BookDetailsActivity extends AppCompatActivity {
    String bookId, urlPdf, titleBook;
    ImageButton backBtn;
    TextView titleTv, descriptionTv, authorTv, dateTv, sizeTv;
    ImageView imageIv;
    Button downloadBookBtn;

    public static final String TAG = "BOOK_DETAILS";

    private static final String TAG_DOWNLOAD = "TAG_DOWNLOAD";

    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://bookapp-a9588-default-rtdb.europe-west1.firebasedatabase.app/");
    FirebaseStorage mStorage = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_book_details);

        downloadBookBtn = findViewById(R.id.downloadBookBtn);
        downloadBookBtn.setVisibility(View.GONE);

        titleTv = findViewById(R.id.titleTv);
        descriptionTv = findViewById(R.id.descriptionTv);
        authorTv = findViewById(R.id.authorTv);
        dateTv = findViewById(R.id.dateTv);
        sizeTv = findViewById(R.id.sizeTv);
        imageIv = findViewById(R.id.imageIv);



        Intent intent=getIntent();
        bookId= intent.getStringExtra("bookId");

        loadBookDetails();

        downloadBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG_DOWNLOAD, "onclick za preuziamnje:");

                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(urlPdf));
                request.setTitle(titleBook);
                request.setDescription("Prezimanje datoteke");
                String cookie = CookieManager.getInstance().getCookie(urlPdf);
                request.addRequestHeader("cookie", cookie);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, titleBook);

                DownloadManager downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
                downloadManager.enqueue(request);

                Toast.makeText(BookDetailsActivity.this, "Preuzimanje je počelo...", Toast.LENGTH_SHORT).show();
            }
        });

        backBtn =  findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Ne radi ispravno
                //onBackPressed();
                Intent i = new Intent(BookDetailsActivity.this, BooksActivity.class);
                startActivity(i);
            }
        });
    }

    private void loadBookDetails() {
        DatabaseReference ref= mDatabase.getReference("Books");
        ref.child(bookId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                titleBook=""+snapshot.child("title").getValue();
                String author=""+snapshot.child("author").getValue();
                String description=""+snapshot.child("description").getValue();
                urlPdf=""+snapshot.child("url").getValue();
                String image=""+snapshot.child("image").getValue();
                String timestamp=""+snapshot.child("timestamp").getValue();
                downloadBookBtn.setVisibility(View.VISIBLE);

                String date = MyApplication.formatTimestamp(timestamp);
                loadPdfSize(urlPdf, sizeTv);
                titleTv.setText(titleBook);
                descriptionTv.setText(description);
                authorTv.setText(author);
                dateTv.setText(date);
                Picasso.get()
                        .load(image)
                        .into(imageIv);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadPdfSize(String pdfUrl, TextView sizeTv) {
        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
        ref.getMetadata()
                .addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                    @Override
                    public void onSuccess(StorageMetadata storageMetadata) {
                        double bytes = storageMetadata.getSizeBytes();

                        double kb = bytes/1024;
                        double mb = kb/1024;

                        if(mb >=1){
                            sizeTv.setText(String.format("%.2f", mb)+" MB");
                        }
                        else if(kb >=1){
                            sizeTv.setText(String.format("%.2f", kb)+" KB");
                        }
                        else{
                            sizeTv.setText(String.format("%.2f", bytes)+" bytes");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }
}