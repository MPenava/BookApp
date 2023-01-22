package ba.sum.fpmoz.bookapp;
import static ba.sum.fpmoz.bookapp.R.id.openLoginBtn;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Homepage extends AppCompatActivity {

    //private ActivityHomepageBinding binding;

  //  @Override
    //protected void onCreate(Bundle savedInstanceState){
      //  super.onCreate(savedInstanceState);
        //binding=ActivityHomepageBinding.inflate(getLayoutInflater());
        //setContentView(binding.getRoot());
  //  }
  @Override
  protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_homepage);

      Button openRegisterBtn = findViewById(R.id.openRegisterBtn);


      openRegisterBtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              Intent registerIntent = new Intent(Homepage.this, RegisterActivity.class);
              startActivity(registerIntent);
          }
      });

      Button OpenLoginBtn = findViewById(openLoginBtn);

      OpenLoginBtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              Intent loginIntent = new Intent(Homepage.this, LoginActivity.class);
              startActivity(loginIntent);
          }
      });

  }
}




