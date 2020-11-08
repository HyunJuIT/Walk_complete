package com.example.walk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.List;

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth; //파이어베이스 로그인 인증 객체
    EditText edt_email, edt_password;
    TextView btn_join;
    Button btn_login;
    final int RC_SIGN_IN = 8;
    CheckBox checkBox;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pref = getApplicationContext().getSharedPreferences("mine", MODE_PRIVATE);
        editor = pref.edit();

        btn_join = (TextView) findViewById(R.id.txt_Join);
        btn_login = (Button) findViewById(R.id.btn_Login);
        edt_email = (EditText) findViewById(R.id.edit_email_login);
        edt_password = (EditText) findViewById(R.id.edit_password_login);
        checkBox = (CheckBox) findViewById(R.id.Auto_Login_check);

        mAuth = FirebaseAuth.getInstance();

        //로그인 방법들이 모두 담긴 객체
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build()/*,    //이메일 로그인
                new AuthUI.IdpConfig.PhoneBuilder().build(),    //휴대폰 인증 (번호) 로그인
                new AuthUI.IdpConfig.GoogleBuilder().build(),   //구글 로그인
                new AuthUI.IdpConfig.FacebookBuilder().build(), //페이스북 로그인
                new AuthUI.IdpConfig.TwitterBuilder().build()*/); //트위터 로그인


        if (pref.getString("id", null) != null) {
            auto_login();
        }

        btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
         /*       startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(providers)
                                .build(),
                        RC_SIGN_IN);*/

                Intent intent = new Intent(getApplicationContext(), Join.class);
                startActivity(intent);
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = edt_email.getText().toString();
                String password = edt_password.getText().toString();

                if (email != null && password != null) {
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("Login", "signInWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        updateUI(user);

                                        if (checkBox.isChecked()) {
                                            editor.putString("id", email);
                                            editor.commit();
                                            editor.putString("pw", password);
                                            editor.commit();
                                        }

                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w("Login", "signInWithEmail:failure", task.getException());
                                        Toast.makeText(Login.this, "로그인에 실패하셨습니다.",
                                                Toast.LENGTH_SHORT).show();
                                        edt_email.setText("");
                                        edt_password.setText("");
                                        updateUI(null);
                                        // ...
                                    }

                                    // ...
                                }
                            });
                } else {
                    Toast.makeText(Login.this, "이메일이나 비밀번호를 입력해주세요.",
                            Toast.LENGTH_SHORT).show();
                }
 /*               Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();*/
            }
        });
    }

    public void auto_login() {
        String email = pref.getString("id", "guswndhfl@naver.com");
        String password = pref.getString("pw", "1148aa");

        Log.d("로그인체크",email+"/"+password);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Login", "signInWithEmail:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            // ...
                        }

                        // ...
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        Log.d("firebase", String.valueOf(currentUser) + "출력");
    }

}