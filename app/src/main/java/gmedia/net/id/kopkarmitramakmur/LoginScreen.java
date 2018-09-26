package gmedia.net.id.kopkarmitramakmur;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import gmedia.net.id.kopkarmitramakmur.NotificationUtil.InitFirebaseSetting;
import gmedia.net.id.kopkarmitramakmur.Util.ApiVolley;
import gmedia.net.id.kopkarmitramakmur.Util.ItemValidation;
import gmedia.net.id.kopkarmitramakmur.Util.SessionManager;
import gmedia.net.id.kopkarmitramakmur.Util.WebServiceURL;

public class LoginScreen extends Activity {

    private EditText edtUsername;
    private EditText edtPassword;
    private Switch sPreview;
    private Button btnLogin;
    private SessionManager session;
    private boolean doubleBackToExitPressedOnce = false;
    private ItemValidation iv = new ItemValidation();
    private CheckBox cbSave;
    private ProgressDialog progressDialog;
    private String refreshToken;
    private TextView tvLupaPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        if( getIntent().getBooleanExtra("Exit me", false)){
            finish();
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            return;
        }


        refreshToken = FirebaseInstanceId.getInstance().getToken();
        initUI();
        InitFirebaseSetting.getFirebaseSetting(LoginScreen.this);
    }

    private void initUI() {

        edtUsername = (EditText) findViewById(R.id.edt_username);
        edtPassword = (EditText) findViewById(R.id.edt_password);
        sPreview = (Switch) findViewById(R.id.s_preview);
        btnLogin = (Button) findViewById(R.id.btn_login);
        cbSave = (CheckBox) findViewById(R.id.cb_save);
        tvLupaPassword = (TextView) findViewById(R.id.tv_lupa_password);
        session = new SessionManager(LoginScreen.this);

        sPreview.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    edtPassword.setTransformationMethod(null);
                    edtPassword.setSelection(edtPassword.getText().length());
                }else{
                    edtPassword.setTransformationMethod(new PasswordTransformationMethod());
                    edtPassword.setSelection(edtPassword.getText().length());
                }
            }
        });

        //checklogin
        if(session.saveLogin()){

            String nik = session.getNik();
            cbSave.setChecked(true);
            RedirrectToMain(nik);
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(edtUsername.getText().length() <= 0){
                    edtUsername.setError("Username tidak boleh kosong");
                    return;
                }else{
                    edtUsername.setError(null);
                }

                if(edtPassword.getText().length() <= 0){
                    edtPassword.setError("Password tidak boleh kosong");
                    return;
                }else{
                    edtPassword.setError(null);
                }

                doLogin();
            }
        });

        tvLupaPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginScreen.this, ResetPassword.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }

    private void doLogin() {

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("username", edtUsername.getText().toString());
            jBody.put("password", edtPassword.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(LoginScreen.this, jBody, "POST", WebServiceURL.login, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                JSONObject responseAPI;
                try {
                    responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");

                    if(iv.parseNullInteger(status) == 200){
                        RedirrectToMain(edtUsername.getText().toString());
                    }else{
                        Toast.makeText(LoginScreen.this, "Username atau Password tidak benar",Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {
                Toast.makeText(LoginScreen.this, "Tidak dapat login, mohon cek koneksi anda dan coba kembali",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void RedirrectToMain(String nik) {

        showProgressDialog();

        JSONObject jBody = new JSONObject();

        ApiVolley request = new ApiVolley(LoginScreen.this, jBody, "GET", WebServiceURL.getPeserta + nik, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                JSONObject responseAPI;
                try {
                    responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray jsonArray = responseAPI.getJSONArray("response");

                        for(int i = 0; i < 1;i++){

                            JSONObject item = jsonArray.getJSONObject(i);
                            login(item.getString("id_pst"), item.getString("nm_pst"),item.getString("nik_pst"),item.getString("email"), item.getString("departemen"),cbSave.isChecked());
                        }
                    }

                    dismissProgressDialog();
                } catch (JSONException e) {
                    e.printStackTrace();
                    dismissProgressDialog();
                }
            }

            @Override
            public void onError(String result) {
                dismissProgressDialog();
            }
        });
    }

    private void login(String id, String nama, String nik, String email, String dept, boolean saveFlag){

        dismissProgressDialog();
        if(saveFlag) {
            session.createLoginSession(id, nama, nik, email, dept,"1");
        }else{
            session.createLoginSession(id, nama, nik, email, dept,"1");
        }

        updateFCMID(nik);
        Intent intent = new Intent(LoginScreen.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    private void updateFCMID(String nik) {

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("nik", nik);
            jBody.put("fcm_id", refreshToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(LoginScreen.this, jBody, "PUT", WebServiceURL.updateFCMID, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

            }

            @Override
            public void onError(String result) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Intent intent = new Intent(this, LoginScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("Exit me", true);
            startActivity(intent);
            finish();
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Tekan lagi untuk keluar", Toast.LENGTH_SHORT).show();

        // take 2 second before the doubleBackToExitPressedOnce become false again
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    private void showProgressDialog(){
        progressDialog = new ProgressDialog(LoginScreen.this,
                R.style.CustomLoginDialogBar);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();
    }

    private void dismissProgressDialog(){
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }
}
