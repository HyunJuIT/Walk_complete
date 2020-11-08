package com.example.walk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PasswordEdit extends AppCompatActivity {

    EditText edit_password, edit_password_check;
    Button changePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_edit);

        edit_password = (EditText) findViewById(R.id.edit_password);
        edit_password_check = (EditText) findViewById(R.id.edit_passwordcheck);
        changePassword = (Button) findViewById(R.id.btn_change_password);

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (edit_password.getText().toString().equals(edit_password_check.getText().toString())) {
                    String newPassword = edit_password.getText().toString();

                    user.updatePassword(newPassword)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(PasswordEdit.this, "성공적으로 변경하였습니다.", Toast.LENGTH_LONG).show();

                                        FirebaseAuth.getInstance().signOut();

                                        SharedPreferences pref = getApplicationContext().getSharedPreferences("mine", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = pref.edit();
                                        editor.remove("id");
                                        editor.remove("pw");
                                        editor.commit();

                                        Intent intent = new Intent(PasswordEdit.this, Login.class);
                                        startActivity(intent);
                                        finish();
                                        MainActivity.activity.finish();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(PasswordEdit.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}