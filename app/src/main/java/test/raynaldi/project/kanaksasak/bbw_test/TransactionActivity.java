package test.raynaldi.project.kanaksasak.bbw_test;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import test.raynaldi.project.kanaksasak.bbw_test.Model.OperatorModel;
import test.raynaldi.project.kanaksasak.bbw_test.Model.VoucherModel;

public class TransactionActivity extends AppCompatActivity {

    private static String URL = "http://202.158.48.243:2018/soal/middle/pulsa.jsp?";
    private ProgressDialog mProgressDialog;
    private Spinner operator, pulsa;
    private TextView harga;
    private List<OperatorModel> mDataOpr;
    private List<VoucherModel> mDataPls;
    private String userid;
    private Button submit;
    private EditText phone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        operator = findViewById(R.id.operator);
        pulsa = findViewById(R.id.pulsa);
        harga = findViewById(R.id.harga);
        submit = findViewById(R.id.submitbtn);
        phone = findViewById(R.id.phone);

        mDataOpr = new ArrayList<>();
        mDataPls = new ArrayList<>();

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading ...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        GetUsereId();
        GetDataOperator();

        operator.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String opr = operator.getSelectedItem().toString();
                mProgressDialog.show();
                GetDataPulsa(opr);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        pulsa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int x, long l) {
                String pls = pulsa.getSelectedItem().toString();
                for (int i = 0; i < mDataPls.size(); i++) {

                    if (mDataPls.get(i).getPulsa().equals(pls)) {
                        harga.setText(mDataPls.get(i).getHarga().toString());
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Transaction(operator.getSelectedItem().toString(), harga.getText().toString());
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.logout) {
            Logout();
            startActivity(new Intent(TransactionActivity.this, MainActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void GetDataOperator() {
        String operatorurl = "type=operator";

        final StringRequest request = new StringRequest(Request.Method.GET, URL + operatorurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                getDataOpr(response);
                mProgressDialog.dismiss();
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }

        );

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);

    }

    private void GetDataPulsa(String param) {
        String operatorurl = "type=voucher&operator=" + param.trim() + "";

        final StringRequest request = new StringRequest(Request.Method.GET, URL + operatorurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                getDataPls(response);
                mProgressDialog.dismiss();
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }

        );

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);

    }

    private void getDataOpr(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String status = jsonObject.getString("status");
            String msg = jsonObject.getString("msg");


            if (status.equals("0")) {
                JSONArray jsonArray = jsonObject.getJSONArray("operator");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject objectMakanan = jsonArray.getJSONObject(i);

                    OperatorModel operatorModel = new OperatorModel();

                    String nama = objectMakanan.getString("nama");
                    System.out.println("snap ->" + nama);
                    operatorModel.setNama(nama);
                    mDataOpr.add(operatorModel);
                }

                String[] parse = new String[mDataOpr.size()];
                for (int i = 0; i < mDataOpr.size(); i++) {
                    parse[i] = mDataOpr.get(i).getNama();
                }

                SetSpinnerAdapterOpr(parse);

            } else {
                Toast.makeText(TransactionActivity.this, msg, Toast.LENGTH_LONG).show();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getDataPls(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String status = jsonObject.getString("status");
            String msg = jsonObject.getString("msg");


            if (status.equals("1")) {
                JSONArray jsonArray = jsonObject.getJSONArray("voucher");

                mDataPls.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject objectMakanan = jsonArray.getJSONObject(i);

                    VoucherModel voucherModel = new VoucherModel();

                    String pulsa = objectMakanan.getString("pulsa");
                    String harga = objectMakanan.getString("harga");
                    System.out.println("snap ->" + pulsa);
                    voucherModel.setHarga(harga);
                    voucherModel.setPulsa(pulsa);
                    mDataPls.add(voucherModel);
                }

                String[] parse = new String[mDataPls.size()];
                for (int i = 0; i < mDataPls.size(); i++) {
                    parse[i] = mDataPls.get(i).getPulsa();
                }

                SetSpinnerAdapterPlsa(parse);

            } else {
                Toast.makeText(TransactionActivity.this, msg, Toast.LENGTH_LONG).show();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void SetSpinnerAdapterOpr(String[] param) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, param);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        operator.setAdapter(dataAdapter);
    }

    private void SetSpinnerAdapterPlsa(String[] param) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, param);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pulsa.setAdapter(dataAdapter);
    }

    private void Transaction(String opr, String harga) {

        boolean validate = validateInputFileName(phone.getText().toString());
        String operatorurl = "type=transaction&userid=" + userid + "&operator=" + opr + "&harga=" + harga + "";

        if (validate == true) {

            final StringRequest request = new StringRequest(Request.Method.GET, URL + operatorurl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    mProgressDialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        String status = jsonObject.getString("status");
                        String msg = jsonObject.getString("msg");


                        if (status.equals("1")) {
                            Toast.makeText(TransactionActivity.this, msg, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(TransactionActivity.this, msg, Toast.LENGTH_LONG).show();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }

            );

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(request);

        }

    }

    private boolean validateInputFileName(String hp) {

        if (TextUtils.isEmpty(hp)) {
            Toast.makeText(TransactionActivity.this, "Enter Phone Number!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void Logout() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.LOGIN, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();

    }

    private void GetUsereId() {

        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.USERID, 0);
        userid = pref.getString("USERID", "0");


    }
}
