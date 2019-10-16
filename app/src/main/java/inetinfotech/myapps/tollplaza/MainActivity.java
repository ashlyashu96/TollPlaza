package inetinfotech.myapps.tollplaza;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {
    private IntentIntegrator qrScan;
    IntentResult result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        qrScan = new IntentIntegrator(this);
        qrScan.initiateScan();
    }
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
             result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                //if qrcode has nothing in it
                if (result.getContents() == null) {
                    Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
                } else {
                    //if qr contains data

                        //to a toast
                        Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                        PayAmount();

                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }

    public void PayAmount() {
        StringRequest stringRequest;
        stringRequest = new StringRequest(Request.Method.POST, "https://full-bottomed-cushi.000webhostapp.com/Fasttag_payment.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//If we are getting success from serverStr
                        if(response.contains("success"))
                        {
                            Toast.makeText(MainActivity.this, "payment Successful", Toast.LENGTH_SHORT).show();
                        }
                        else if(response.contains("failed"))
                        {
                            Toast.makeText(MainActivity.this, "Tag id doesnot exist", Toast.LENGTH_SHORT).show();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//You can handle error here if you want
                    }

                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
//Adding parameters to request



                try {
                    params.put("tagid", decrypt(result.getContents(),"ashlyg333"));
                } catch (Exception e) {
                    e.printStackTrace();
                }


//returning parameter
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private String decrypt(String out, String anoop) throws Exception {
        SecretKeySpec k=gen(anoop);
        Cipher cipher=Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE,k);
        byte[] dv= Base64.decode(out,Base64.DEFAULT);
        byte[] deco= cipher.doFinal(dv);
        String n=new String(deco);
        return n;

    }

    private SecretKeySpec gen(String pass) throws Exception {

        final MessageDigest di= MessageDigest.getInstance("SHA-256");

        byte[] bytes=pass.getBytes("UTF-8");
        di.update(bytes,0,bytes.length);
        byte[] key=di.digest();
        SecretKeySpec secretKeySpec=new SecretKeySpec(key,"SHA");
        return secretKeySpec;

    }
    }

