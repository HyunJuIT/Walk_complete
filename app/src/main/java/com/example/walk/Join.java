package com.example.walk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Join extends AppCompatActivity {

    EditText edt_email, edt_password, edt_number, edt_password_check;
    Button btn_join, btn_emailSend, btn_emailCodeCheck;
    private FirebaseAuth mAuth;
    private boolean check = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        mAuth = FirebaseAuth.getInstance();

        btn_join = (Button) findViewById(R.id.btn_join);
        btn_emailSend = (Button) findViewById(R.id.btn_email_send);
        btn_emailCodeCheck = (Button) findViewById(R.id.btn_email_code_check);
        edt_email = (EditText) findViewById(R.id.edit_email_join);
        edt_password = (EditText) findViewById(R.id.edit_password_join);
        edt_number = (EditText) findViewById(R.id.edit_number_join);
        edt_password_check = (EditText) findViewById(R.id.edit_passwordcheck_join);

        btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = edt_email.getText().toString();
                String password = edt_password.getText().toString();

                if(check){
                    if(edt_password.getText().toString().equals(edt_password_check.getText().toString())){
                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(Join.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            Log.d("Join", "createUserWithEmail:success");
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            updateUI(user);
                                            finish();
                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Log.w("Join", "createUserWithEmail:failure", task.getException());
                                            Toast.makeText(Join.this, "가입 정보를 다시 확인해주세요.",
                                                    Toast.LENGTH_SHORT).show();
                                            updateUI(null);
                                        }

                                        // ...
                                    }
                                });
                    }else{
                        Toast.makeText(Join.this,"비밀번호가 일치하지 않습니다.",Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(Join.this,"아직 인증이 완료되지 않았습니다.",Toast.LENGTH_LONG).show();
                }
            }
        });

        btn_emailSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMail mailServer = new SendMail();
                mailServer.sendSecurityCode(getApplicationContext(), edt_email.getText().toString());
            }
        });

        btn_emailCodeCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("emailcode","인증버튼 누름");
                if(edt_number.getText().toString().equals(GMailSender.getEmailCode())){
                    Log.d("emailcode","인증버튼 성공");
                    check = true;
                    Toast.makeText(Join.this,"성공적으로 인증되었습니다.",Toast.LENGTH_LONG).show();
                }else{
                    Log.d("emailcode","인증버튼 실패");
                    Toast.makeText(Join.this,"인증번호가 다릅니다.",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void updateUI(FirebaseUser user) {
    }
}