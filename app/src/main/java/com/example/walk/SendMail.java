package com.example.walk;

import android.content.Context;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

public class SendMail extends AppCompatActivity {

    private String user = "a01091860209@gmail.com"; // 보내는 계정의 id
    private String password = "a01091860209"; // 보내는 계정의 pw
    private String title_mail = "[Walk] 이메일 등록 확인 메일";
    private String code_mail = "인증번호 : ";

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTitle_mail() {
        return title_mail;
    }

    public void setTitle_mail(String title_mail) {
        this.title_mail = title_mail;
    }

    public String getCode_mail() {
        return code_mail;
    }

    public void setCode_mail(String code_mail) {
        this.code_mail = code_mail;
    }

    public void sendSecurityCode(Context context, String sendTo) {
        try {
            GMailSender gMailSender = new GMailSender(user, password);
            gMailSender.sendMail(title_mail, code_mail+gMailSender.getEmailCode(), sendTo);
            Toast.makeText(context, "이메일을 성공적으로 보냈습니다.", Toast.LENGTH_SHORT).show();
        } catch (SendFailedException e) {
            Toast.makeText(context, "이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
        } catch (MessagingException e) {
            Toast.makeText(context, "인터넷 연결을 확인해주십시오", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
