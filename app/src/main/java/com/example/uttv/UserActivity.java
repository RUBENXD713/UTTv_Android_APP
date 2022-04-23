package com.example.uttv;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class UserActivity extends AppCompatActivity {
    TextView tvName, tvEmail, tvTipo;
    Button btnLogout, btnPermiso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvTipo = findViewById(R.id.tvTipo);
        btnLogout = findViewById(R.id.btnLogout);
        btnPermiso = findViewById(R.id.btnPermiso);


        getUser();

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });




        btnPermiso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPermiso();
            }
        });
    }

    /*private void logout() {
        Intent intent = new Intent(UserActivity.this, activity_login.class);
        startActivity(intent);
        finish();
    }*/

    private void getUser() {
        String url = getString(R.string.api_server)+"us/perfilAndroid";
        new Thread(new Runnable() {
            @Override
            public void run(){
                Http http = new Http(UserActivity.this,url);
                http.setToken(true);
                http.send();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Integer code = http.getStatusCode();
                        if (code == 200){
                            try {
                                JSONObject response = new JSONObject(http.getResponse());
                                String name = response.getString("name");
                                String email = response.getString("email");
                                String tipo = response.getString("tipo");
                                tvName.setText(name);
                                tvEmail.setText(email);
                                switch(tipo){
                                    case "0":
                                        tvTipo.setText("Usuario comun");
                                        break;
                                    case "1":
                                        tvTipo.setText("SuperUsuario");
                                        break;
                                    case "2":
                                        tvTipo.setText("Administrador");
                                        break;
                                    default:
                                        tvTipo.setText("No se puede setear el valor.");
                                        break;
                                }
                            }catch(JSONException e){
                                e.printStackTrace();
                            }
                        }else{
                            Toast.makeText(UserActivity.this,"Error "+code,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }


    private void logout(){
        String url = getString(R.string.api_server)+"us/LogOut";
        new Thread(new Runnable() {
            @Override
            public void run() {
                Http http = new Http(UserActivity.this,url);
                http.setMethod("delete");
                http.setToken(true);
                http.send();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run(){
                        Integer code = http.getStatusCode();
                        if (code == 200){
                            Intent intent = new Intent( UserActivity.this,activity_login.class);
                            startActivity(intent);
                            finish();
                        }else{
                            Toast.makeText(UserActivity.this,"Error "+code,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }





    private void setPermiso(){
        String url = getString(R.string.api_server)+"us/login3";
        new Thread(new Runnable() {
            @Override
            public void run() {
                Http http = new Http(UserActivity.this,url);
                http.setMethod("post");
                http.setToken(true);
                http.send();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run(){
                        Integer code = http.getStatusCode();
                        if (code == 200){
                            Toast.makeText(UserActivity.this,"Permisos actualizados",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(UserActivity.this,"Error "+code,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }



}