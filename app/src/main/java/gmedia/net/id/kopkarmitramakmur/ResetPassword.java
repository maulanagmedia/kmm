package gmedia.net.id.kopkarmitramakmur;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import gmedia.net.id.kopkarmitramakmur.Util.ApiVolley;
import gmedia.net.id.kopkarmitramakmur.Util.ItemValidation;
import gmedia.net.id.kopkarmitramakmur.Util.WebServiceURL;

public class ResetPassword extends AppCompatActivity {

    private EditText edtNik;
    private EditText edtLahir;
    private Button btnReset;
    private ItemValidation iv = new ItemValidation();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        getSupportActionBar().hide();

        initUI();
    }

    private void initUI() {

        edtNik = (EditText) findViewById(R.id.edt_nik);
        edtLahir = (EditText) findViewById(R.id.edt_tanggal);
        iv.datePickerEventKlick(ResetPassword.this, edtLahir, getResources().getString(R.string.format_date));
        btnReset = (Button) findViewById(R.id.btn_reset);

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                validate();
            }
        });
    }

    private void validate(){

        if(edtNik.getText().length() == 0){
            edtNik.requestFocus();
            edtNik.setError("NIK tidak boleh kosong");
            return;
        }else{
            edtNik.setError(null);
        }

        if(edtLahir.getText().length() == 0){
            edtLahir.requestFocus();
            edtLahir.setError("Tanggal Lahir tidak boleh kosong");
            return;
        }else{
            edtLahir.setError(null);
        }

        resetPassword();
    }

    private void resetPassword() {

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("nik", edtNik.getText().toString());
            jBody.put("tanggal_lahir", edtLahir.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(ResetPassword.this, jBody, "PUT", WebServiceURL.resetPassword, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                JSONObject responseAPI;
                try {
                    responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");

                    if(iv.parseNullInteger(status) == 200){

                        showDialog();
                    }else{
                        Toast.makeText(ResetPassword.this, "Tidak dapat mereset password, silahkan ulangai beberapa saat lagi",Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {
                Toast.makeText(ResetPassword.this, "Tidak dapat mereset password, mohon cek koneksi anda dan coba kembali",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(ResetPassword.this)
                .setTitle("Reset Password Berhasil")
                .setIcon(R.mipmap.ic_kopkar_logo)
                .setMessage("Silahkan tunggu sms password terbaru dari admin KMM")
                .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
