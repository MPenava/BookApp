package ba.sum.fpmoz.bookapp;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;


public class MyApplication extends Application {
    static FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://bookapp-a9588-default-rtdb.europe-west1.firebasedatabase.app/");

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static final String formatTimestamp(String timestamp){
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        long lg = Long.parseLong(timestamp);
        cal.setTimeInMillis(lg);

        String date = DateFormat.format("dd/MM/yyyy", cal).toString();

        return date;
    }
    public static void deleteBook(Context context, String bookURL, String bookImage, String bookTitle) {
        String TAG = "DELETE_BOOK_TAG";
        Log.d(TAG, "onDelete:uspješno učitavanje");


        Log.d(TAG, "deleteBook: Deleting...");
        ProgressDialog progressDialog=new ProgressDialog(context);

        StorageReference storageReferencePdf = FirebaseStorage.getInstance().getReferenceFromUrl(bookURL);
        StorageReference storageReferenceImage = FirebaseStorage.getInstance().getReferenceFromUrl(bookImage);
        storageReferencePdf.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "onSuccess: Deleted from storage");
                Log.d(TAG, "onSuccess: Now deleting from db");

                DatabaseReference reference = mDatabase.getReference("Books");
                Log.d(TAG, "reference:" + reference.child("gg").getParent());
                //napomena
                String bookTimetamp = new String();
                reference.child(bookTimetamp).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: Deleted from db too");
                        Toast.makeText(context, "Knjiga uspješno izbrisana", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Failed to delete from db due to"+e.getMessage());
                        progressDialog.dismiss();
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Failed to delete pdf file from storage due to"+e.getMessage());
                progressDialog.dismiss();
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        storageReferenceImage.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "onSuccess: Deleted from storage");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Failed to delete image from storage due to"+e.getMessage());
                progressDialog.dismiss();
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public static void loadPdfSize(String pdfUrl, String pdfTitle, TextView sizeTv) {
        String TAG="PDF_SIZE_TAG";

        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
        ref.getMetadata()
                .addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                    @Override
                    public void onSuccess(StorageMetadata storageMetadata) {
                        double bytes = storageMetadata.getSizeBytes();
                        Log.d(TAG, "onSuccess: " +pdfTitle+ " " + bytes);

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
                        Log.d(TAG, "onFailure: " + e.getMessage());
                    }
                });
    }

    public static void incrementBookViewCount(String bookAuthor){
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookAuthor).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String viewsCount = ""+snapshot.child("viewsCount").getValue();
                        if(viewsCount.equals("") || viewsCount.equals("null")){
                            viewsCount="0";
                        }
                        String newViewsCount= viewsCount + 1;
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("viewsCount", newViewsCount);

                        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Books");
                        reference.child(bookAuthor)
                                .updateChildren(hashMap);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}
