package ba.sum.fpmoz.bookapp.adapter;


import android.content.Context;
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

import ba.sum.fpmoz.bookapp.MyApplication;
import ba.sum.fpmoz.bookapp.R;
import ba.sum.fpmoz.bookapp.databinding.BookViewBinding;
import ba.sum.fpmoz.bookapp.model.Book;


public class BookAdapter extends FirebaseRecyclerAdapter<Book, BookAdapter.BookViewHolder>{

    Context context;

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
        holder.titleTv.setText(model.getTitle());
        holder.authorTv.setText(model.getAuthor());
        holder.descriptionTv.setText(model.getDescription());

    }

    class BookViewHolder extends RecyclerView.ViewHolder{

        TextView titleTv, authorTv, descriptionTv;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.titleTv);
            authorTv = itemView.findViewById(R.id.authorTv);
            descriptionTv = itemView.findViewById(R.id.descriptionTv);
        }

    }

}
