package ba.sum.fpmoz.bookapp.adapter;


import android.content.Context;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import ba.sum.fpmoz.bookapp.MyApplication;
import ba.sum.fpmoz.bookapp.R;
import ba.sum.fpmoz.bookapp.databinding.BookViewBinding;
import ba.sum.fpmoz.bookapp.model.Book;


public class BookAdapter extends FirebaseRecyclerAdapter<Book, BookAdapter.BookViewHolder>{

    Context context;
    public static final String TAG = "BOOK_ADAPTER";

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

        String formattedDate = MyApplication.formatTimestamp(timestamp);

        holder.titleTv.setText(title);
        holder.authorTv.setText(author);
        holder.descriptionTv.setText(description);
        holder.dateTv.setText(formattedDate);

        Log.d(TAG, "title: " + title);
        Log.d(TAG, "timestamp: " + formattedDate);

        loadPdfSize(model, holder);
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

    class BookViewHolder extends RecyclerView.ViewHolder{

        TextView titleTv, authorTv, descriptionTv, dateTv, sizeTv;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.titleTv);
            authorTv = itemView.findViewById(R.id.authorTv);
            descriptionTv = itemView.findViewById(R.id.descriptionTv);
            dateTv = itemView.findViewById(R.id.dateTv);
            sizeTv = itemView.findViewById(R.id.sizeTv);
        }

    }

}
