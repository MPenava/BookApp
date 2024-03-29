package ba.sum.fpmoz.bookapp;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Locale;

public class MyApplication extends Application {
    private static final String TAG_DOWNLOAD = "TAG_DOWNLOAD";

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

    public static void downloadBook(Context context, String bookUrl, String bookTitle){
        String nameWithExtension = bookTitle + ".pdf";

        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(bookUrl);
        final long THREE_MEGABYTE = 1024 * 1024 * 3;
        storageReference.getBytes(THREE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Log.d(TAG_DOWNLOAD, "downloadBook: onSuccess");
                Toast.makeText(context, "Datoteka je spremna za preuzimanje", Toast.LENGTH_SHORT).show();
                saveDownloadedBook(context, bytes, nameWithExtension);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG_DOWNLOAD, "downloadBook: failed! " + e.getMessage());
                Toast.makeText(context, "Greška prilikom preuzimanja: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void saveDownloadedBook(Context context, byte[] bytes, String nameWithExtension){
        Log.d(TAG_DOWNLOAD, "savingBook");
        try{
            File downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            downloadFolder.mkdirs();

            String filePath = downloadFolder.getPath() + "/" + nameWithExtension;

            FileOutputStream out = new FileOutputStream(filePath);
            out.write(bytes);
            out.close();

            Log.d(TAG_DOWNLOAD, "saveDownloadedBookError:success");
            Toast.makeText(context, "Uspješno preuzimanje", Toast.LENGTH_SHORT).show();
        }catch(Exception e){
            Log.d(TAG_DOWNLOAD, "saveDownloadedBookError: " + e.getMessage());
            Toast.makeText(context, "Greška prilikom spremanja" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
