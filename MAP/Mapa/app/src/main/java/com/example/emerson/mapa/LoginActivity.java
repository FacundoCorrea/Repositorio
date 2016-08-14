package com.example.emerson.mapa;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity {

    int Usuario;
    int result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button botonLogin = (Button)findViewById(R.id.button2);
        final EditText editText = (EditText)findViewById(R.id.editText);
        botonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Usuario = Integer.parseInt(editText.getText().toString());
                Login();


            }
        });
    }

    private void Login()
    {
        String URL = "http://10.0.2.2:3000/api/users?id="+Usuario+"/existe";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                URL, null, new Response.Listener<JSONObject>() {


            @Override
            public void onResponse(JSONObject response) {
                try {
                    Toast.makeText(getApplicationContext(),"EL USUARIO NO EXISTE", Toast.LENGTH_SHORT).show();
                    result = Integer.parseInt(response.getString("count"));
                    Check();
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void Check()
    {
        if(result == 1)
        {
            startActivity(new Intent(LoginActivity.this,MapsActivity.class));

        }
        else{Toast.makeText(getApplicationContext(),"EL USUARIO NO EXISTE", Toast.LENGTH_SHORT).show();}
    }


}
