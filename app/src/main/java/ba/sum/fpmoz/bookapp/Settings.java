package ba.sum.fpmoz.bookapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Settings extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    ImageView imgLogoutView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        imgLogoutView = findViewById(R.id.imgLogout);
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
                        startActivity(new Intent(getApplicationContext(), Shop.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.navigation_notifications:
                        startActivity(new Intent(getApplicationContext(), Notifications.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.navigation_settings:
                        return true;
                }
                return false;
            }
        });

        imgLogoutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Homepage.class);
                startActivity(intent);
            }
        });
    }
}