package com.example.walk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Splash extends AppCompatActivity {

    private FirebaseAuth mAuth; //파이어베이스 로그인 인증 객체
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();
        pref = getApplicationContext().getSharedPreferences("mine", MODE_PRIVATE);
        editor = pref.edit();
        handler = new Handler();

        if (pref.getString("id", null) != null) {
            auto_login();
        } else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(Splash.this, Login.class);
                    startActivity(intent);
                    finish();
                }
            }, 3000);
        }
    }

    public void auto_login() {
        String email = pref.getString("id", "guswndhfl@naver.com");
        String password = pref.getString("pw", "1148aa");

        Log.d("로그인체크", email + "/" + password);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(Splash.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Login", "signInWithEmail:failure", task.getException());
                            Toast.makeText(Splash.this, "로그인에 실패했습니다.",
                                    Toast.LENGTH_SHORT).show();
                            // ...
                        }

                        // ...
                    }
                });
    }

}