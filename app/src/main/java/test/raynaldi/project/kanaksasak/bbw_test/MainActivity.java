package test.raynaldi.project.kanaksasak.bbw_test;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private static String URL = "http://202.158.48.243:2018/soal/middle/pulsa.jsp?";
    EditText user, pass;
    Button loginbtn;
    private String IdLogin;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginbtn = findViewById(R.id.logibtn);
        user = findViewById(R.id.user);
        pass = findViewById(R.id.pass);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading ...");
        mProgressDialog.setCancelable(false);

        GetIdLogin();
        if (IdLogin.equals("1")) {
            Intent homeIntent = new Intent(MainActivity.this, TransactionActivity.class);
            startActivity(homeIntent);
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressDialog.show();
                Login();
            }
        });
    }

    private void Login() {
        String loginurl = "type=login&username=" + user.getText().toString().trim() + "&password=" + pass.getText().toString().trim() + "";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL + loginurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    String status = jsonObject.getString("status");
                    String msg = jsonObject.getString("msg");
                    String user = jsonObject.getString("userid");

                    if (status.equals("1")) {
                        StoreInPref("1", user);
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
                        startActivity(new Intent(MainActivity.this, TransactionActivity.class));
                        finish();
                    } else {
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        },
                new ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }

        );

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void StoreInPref(String token, String token2) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.LOGIN, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("LOGIN", token);
        editor.commit();

        SharedPreferences pref2 = getApplicationContext().getSharedPreferences(Config.USERID, 0);
        SharedPreferences.Editor editor2 = pref2.edit();
        editor2.putString("USERID", token);
        editor2.commit();
    }

    private void GetIdLogin() {

        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.LOGIN, 0);
        IdLogin = pref.getString("LOGIN", "0");


    }


}
