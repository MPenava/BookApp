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
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position, @NonNull Book model)   {
        String title = model.getTitle();
        String author = model.getAuthor();
        String description = model.getDescription();
        String pdfUrl= model.getUrl();
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




        MyApplication.loadPdfSize(
                ""+pdfUrl,
                ""+title,
                holder.sizeTv);

        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moreOptionsDialog(model, holder);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,PdfEditActivity.class);
                intent.putExtra("bookUrl",pdfUrl);
                context.startActivity(intent);
            }
        });
    }

    private void moreOptionsDialog(Book model, BookViewHolder holder) {
        String bookAuthor = model.getAuthor();
        String bookURL = model.getUrl();
        String bookTitle = model.getTitle();
        String bookImage= model.getImage();

        // Opcije koje će se prikazivati u dijalogu.
        String[] options = {"Uredi", "Izbriši", "Detalji"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Odaberite opciju").setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if(which==0){
                    Intent intent = new Intent(context, PdfEditActivity.class);
                    intent.putExtra("bookAuthor", bookAuthor);
                    context.startActivity(intent);

                }else if(which==1){
                    MyApplication.deleteBook(
                            context,
                            ""+bookURL,
                            ""+bookImage,
                            ""+bookTitle);
                    //deleteBook(model, holder);
                }else if(which==2){
                    detailsBook(model, holder);
                }

            }
        }).show();
    }

    private void detailsBook(Book model, BookViewHolder holder) {
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