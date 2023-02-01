package ba.sum.fpmoz.bookapp;

import android.app.Application;
import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Locale;

public class MyApplication extends Application {
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
}
