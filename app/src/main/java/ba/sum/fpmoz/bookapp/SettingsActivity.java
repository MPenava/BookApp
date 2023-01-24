package ba.sum.fpmoz.bookapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingsActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        bottomNavigationView = findViewById(R.id.bottom_navigator);
        bottomNavigationView.setSelectedItemId(R.id.navigation_settings);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.navigation_home:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.navigation_books:
                        startActivity(new Intent(getApplicationContext(), BooksActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.navigation_shop:
                        startActivity(new Intent(getApplicationContext(), ShopActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.navigation_notifications:
                        startActivity(new Intent(getApplicationContext(), NotificationsActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.navigation_settings:
                        return true;
                }
                return false;
            }
        });
    }
}