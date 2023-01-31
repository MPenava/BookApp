package ba.sum.fpmoz.bookapp.adapter;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import ba.sum.fpmoz.bookapp.MyApplication;
import ba.sum.fpmoz.bookapp.PdfEditActivity;
import ba.sum.fpmoz.bookapp.R;
//import ba.sum.fpmoz.bookapp.databinding.BookViewBinding;
import ba.sum.fpmoz.bookapp.model.Book;


public class BookAdapter extends FirebaseRecyclerAdapter<Book, BookAdapter.BookViewHolder>{

    Context context;
    public static final String TAG = "BOOK_ADAPTER";
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://bookapp-a9588-default-rtdb.europe-west1.firebasedatabase.app/");

    private ProgressDialog progressDialog;


    public BookAdapter(@NonNull FirebaseRecyclerOptions <Book> options) {
        super(options);
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_view, parent, false);
        return new BookAdapter.BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position, @NonNull Book model) {
        String title = model.getTitle();
        String author = model.getAuthor();
        String description = model.getDescription();
        String date = model.getTimestamp();
        String timestamp = model.getTimestamp();
        String image = model.getImage();


        String formattedDate = MyApplication.formatTimestamp(timestamp);

        holder.titleTv.setText(title);
        holder.authorTv.setText(author);
        holder.descriptionTv.setText(description);
        holder.dateTv.setText(formattedDate);
        Picasso
                .get()
                .load(image)
                .into(holder.imageIv);



        loadPdfSize(model, holder);

        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moreOptionsDialog(model, holder);
            }
        });
    }

    private void moreOptionsDialog(Book model, BookViewHolder holder) {
        String bookAuthor = model.getAuthor();
        String bookURL = model.getUrl();
        String bookTitle = model.getTitle();

        // Opcije koje će se prikazivati u dijalogu
        String[] options = {"Uredi", "Izbriši"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Odaberite opciju").setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if(which==0){
                    Intent intent = new Intent(context, PdfEditActivity.class);
                    intent.putExtra("bookAuthor", bookAuthor);
                    context.startActivity(intent);

                }else if(which==1){
                    deleteBook(model, holder);
                }

            }
        }).show();
    }

    private void deleteBook(Book model, BookViewHolder holder) {
        Log.d(TAG, "onDelete:uspješno učitavanje");
        String bookURL = model.getUrl();
        String bookTimetamp = model.getTimestamp();
        String bookImage = model.getImage();
        String bookTitle = model.getTitle();

        Log.d(TAG, "deleteBook: Deleting...");

        StorageReference storageReferencePdf = FirebaseStorage.getInstance().getReferenceFromUrl(bookURL);
        StorageReference storageReferenceImage = FirebaseStorage.getInstance().getReferenceFromUrl(bookImage);
        storageReferencePdf.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "onSuccess: Deleted from storage");
                Log.d(TAG, "onSuccess: Now deleting from db");

                DatabaseReference reference = mDatabase.getReference("Books");
                Log.d(TAG, "reference:" + reference.child("gg").getParent());
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

    private void loadPdfSize(Book model, BookViewHolder holder) {

        String pdfUrl = model.getUrl();

        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
        ref.getMetadata()
                .addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                    @Override
                    public void onSuccess(StorageMetadata storageMetadata) {
                        double bytes = storageMetadata.getSizeBytes();
                        Log.d(TAG, "onSuccess: " + model.getTitle() + " " + bytes);

                        double kb = bytes/1024;
                        double mb = kb/1024;

                        if(mb >=1){
                            holder.sizeTv.setText(String.format("%.2f", mb)+" MB");
                        }
                        else if(kb >=1){
                            holder.sizeTv.setText(String.format("%.2f", kb)+" KB");
                        }
                        else{
                            holder.sizeTv.setText(String.format("%.2f", bytes)+" bytes");
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

    class BookViewHolder extends RecyclerView.ViewHolder {

        TextView titleTv, authorTv, descriptionTv, dateTv, sizeTv;
        ImageView imageIv;
        ImageButton moreBtn;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.titleTv);
            authorTv = itemView.findViewById(R.id.authorTv);
            descriptionTv = itemView.findViewById(R.id.descriptionTv);
            imageIv = itemView.findViewById(R.id.imageIv);
            dateTv = itemView.findViewById(R.id.dateTv);
            sizeTv = itemView.findViewById(R.id.sizeTv);
            moreBtn = itemView.findViewById(R.id.moreBtn);
        }

    }

}