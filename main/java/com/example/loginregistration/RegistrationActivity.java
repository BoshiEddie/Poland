package com.example.loginregistration;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    MaterialEditText email,username,password;
    Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        email = findViewById(R.id.email);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        register = findViewById(R.id.register);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textEmail = email.getText().toString();
                String textUserName = username.getText().toString();
                String textpassword = password.getText().toString();

                if(TextUtils.isEmpty(textEmail) || TextUtils.isEmpty(textUserName) || TextUtils.isEmpty(textpassword)){
                    Toast.makeText(RegistrationActivity.this,"All fields required",Toast.LENGTH_SHORT).show();
                }else{
                    registerNewAccount(textEmail,textUserName,textpassword);
                }
            }
        });

    }


    private void registerNewAccount(final String email, final String username, final String password){
        final ProgressDialog progressDialog = new ProgressDialog(RegistrationActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.setTitle("Registering New Account");
        progressDialog.show();

        String uRl = "http://localhost/Login/register.php";

        StringRequest request = new StringRequest(Request.Method.POST, uRl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response.equals("successfully")){
                    progressDialog.dismiss();
                    Toast.makeText(RegistrationActivity.this, response , Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegistrationActivity.this,MainActivity.class));
                    finish();
                }
                else{
                    progressDialog.dismiss();
                    Toast.makeText(RegistrationActivity.this,response,Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(RegistrationActivity.this,error.toString(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> param = new HashMap<>();
                param.put("email",email);
                param.put("username",username);
                param.put("password",password);

                return param;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(RegistrationActivity.this).addToRequestQueue(request);

    }

}
