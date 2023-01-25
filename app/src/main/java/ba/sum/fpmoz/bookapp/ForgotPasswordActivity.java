package ba.sum.fpmoz.bookapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ProgressDialog progressDialog;

    EditText emailEt = findViewById(R.id.emailEt);
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);


        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Pričekajte...");
        progressDialog.setCanceledOnTouchOutside(false);

        ImageButton backBtn1 = findViewById(R.id.backBtn1);

        backBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Button submitBtn = findViewById(R.id.submitBtn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });




    }

    private String email="";
    private void validateData() {
        email = emailEt.getText().toString().trim();
        Patterns Patters = null;
        if(email.isEmpty()){
            Toast.makeText(this, "Potvrdi email..", Toast.LENGTH_SHORT).show();
        }
        else if(!Patters.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this,"Netočan email",Toast.LENGTH_SHORT).show();
        }
        else{
            recoverPassword();
        }
    }

    private void recoverPassword() {
        progressDialog.setMessage("Slanje šifre na email"+email);
        progressDialog.show();

        firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(new OnSuccessListener<Void>(){
                    @Override
                    public void onSuccess(Void unused){
                        progressDialog.dismiss();
                        Toast.makeText(ForgotPasswordActivity.this, "Resetiraj šifru"+email, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener(){
                    @Override
                    public void onFailure(@NonNull Exception e){

                        progressDialog.dismiss();
                        Toast.makeText(ForgotPasswordActivity.this,"Pogrešno slanje"+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    };
}