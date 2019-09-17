package com.example.loginregistration;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    MaterialEditText email,password;
    Button login, register;
    CheckBox loginState;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginState = findViewById(R.id.loginState);
        login = findViewById(R.id.login);
        register = findViewById(R.id.register);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,RegistrationActivity.class));
                finish();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textEmail = email.getText().toString();
                String textPassword = password.getText().toString();

                if(TextUtils.isEmpty(textEmail) || TextUtils.isEmpty(textPassword)){
                    Toast.makeText(MainActivity.this,"All fields required",Toast.LENGTH_SHORT).show();
                }else{
                    login(textEmail,textPassword);
                }
            }
        });

        String loginStatus = sharedPreferences.getString(getResources().getString(R.string.prefLoginState),"");
        if(loginStatus.equals("loggedin")){
            startActivity(new Intent(MainActivity.this,AppStartActivity.class));
        }
    }

    private void login(final String email, final String password){
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.setTitle("");
        progressDialog.show();
        String uRl = "http://localhost/Login/login.php";
        StringRequest request = new StringRequest(Request.Method.POST, uRl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equals("Login Success")){
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this,response,Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    if(loginState.isChecked()){
                        editor.putString(getResources().getString(R.string.prefLoginState),"loggedin");
                    }
                    else{
                        editor.putString(getResources().getString(R.string.prefLoginState),"loggedout");
                    }

                    startActivity(new Intent(MainActivity.this,AppStartActivity.class));
                }else{

                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this,response,Toast.LENGTH_SHORT).show();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this,error.toString(),Toast.LENGTH_SHORT).show();
            }
        }){
            protected Map<String,String> getParams() throws AuthFailureError{
                HashMap<String,String> params = new HashMap<>();
                params.put("email",email);
                params.put("password",password);

                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(MainActivity.this).addToRequestQueue(request);
    }
}
