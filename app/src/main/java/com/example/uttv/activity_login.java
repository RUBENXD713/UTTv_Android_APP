package com.example.uttv;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class activity_login extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin;
    String email, password;
    LocalStorage localStorage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setTitle("Login");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        localStorage = new LocalStorage(activity_login.this);


        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLogin();
            }
        });
    }

    private void checkLogin() {
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();
        if (email.isEmpty() || password.isEmpty()){
            alertFailed("Email y Password son solicitados");
        }else {
            sendLogin();
        }
    }

    /*Toast.makeText(this,"Send",Toast.LENGTH_SHORT).show();
    Intent intent = new Intent(this, UserActivity.class);
    startActivity(intent);
    finish();*/

    private void sendLogin() {
        //Toast.makeText(this,(getString(R.string.api_server)+"us/loginAndroid"),Toast.LENGTH_SHORT).show();
        JSONObject params = new JSONObject();
        try {
            params.put("email",email);
            params.put("password",password);
        }catch (JSONException e) {
            e.printStackTrace();
        }
        String data = params.toString();
        String url = getString(R.string.api_server)+"us/loginAndorid";

        new Thread(new Runnable() {

            @Override
            public void run(){
                Http http = new Http(activity_login.this, url);
                http.setMethod("post");
                http.setData(data);
                http.send();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run(){
                        Integer code = http.getStatusCode();
                        if(code==200){
                            try {
                                JSONObject response = new JSONObject(http.getResponse());
                                String token = response.getString("token");
                                localStorage.setToken(token);
                                Intent intent = new Intent( activity_login.this,UserActivity.class);
                                startActivity(intent);
                                finish();
                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                        }
                        else if(code == 422){
                            try {
                                JSONObject response = new JSONObject(http.getResponse());
                                String msg = response.getString("message");
                                alertFailed(msg);
                            }catch (JSONException e){
                                e.printStackTrace();
                            }

                        }
                        else if(code == 401){
                            try {
                                JSONObject response = new JSONObject(http.getResponse());
                                String msg = response.getString("message");
                                alertFailed(msg);
                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                        }
                        else{
                            Toast.makeText(activity_login.this, "Error"+code, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }

    private void alertFailed(String s) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setIcon(R.drawable.ic_warning)
                .setMessage(s)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

}