package gmedia.net.id.kopkarmitramakmur.NavPengajuanPinjaman;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;

import gmedia.net.id.kopkarmitramakmur.R;
import gmedia.net.id.kopkarmitramakmur.ActivityDetailPengajuan.DetailPengajuan;
import gmedia.net.id.kopkarmitramakmur.SignatureContainer;
import gmedia.net.id.kopkarmitramakmur.Util.ApiVolley;
import gmedia.net.id.kopkarmitramakmur.Util.GlobalFunction;
import gmedia.net.id.kopkarmitramakmur.Util.ImageUtils;
import gmedia.net.id.kopkarmitramakmur.Util.ItemValidation;
import gmedia.net.id.kopkarmitramakmur.Util.SessionManager;
import gmedia.net.id.kopkarmitramakmur.Util.Utils;
import gmedia.net.id.kopkarmitramakmur.Util.WebServiceURL;

public class MainPengajuanPinjaman extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private View layout;
    public String jumlahPinjaman;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ItemValidation iv = new ItemValidation();
    private String TAG = "Pinjaman";
    private EditText edtJumlah;
    private LinearLayout llDaftar;
    private LinearLayout llSimpan;

    public static int UPLOAD_REQUEST_CODE = 21;
    public static int LOAD_REQUEST_CODE = 22;
    private static int RESULT_OK = -1;
    private LinearLayout llLoadImage, llDraw;
    private ImageView ivSignature;
    private static Bitmap bitmap;
    private EditText edtLamaAngsuran;
    private EditText edtJumlahAngsuran;

    private Double jumlahAngsuran = 0.00;
    private String jumlahAngsuranString = "";
    private ProgressDialog progressDialog;

    public MainPengajuanPinjaman() {
        // Required empty public constructor
    }

    public static MainPengajuanPinjaman newInstance(String param1, String param2) {
        MainPengajuanPinjaman fragment = new MainPengajuanPinjaman();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.fragment_main_pengajuan_pinjaman, container, false);

        initUI();
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        return layout;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == UPLOAD_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null){

            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                ivSignature.setImageBitmap(bitmap);
                getContext().getContentResolver().delete(filePath, null, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(requestCode == LOAD_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null){

            Uri filePath = data.getData();
            InputStream imageStream = null;
            try {
                imageStream = getContext().getContentResolver().openInputStream(
                        filePath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            Bitmap bmp = BitmapFactory.decodeStream(imageStream);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 70, stream);
            byte[] byteArray = stream.toByteArray();
            bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            bitmap = GlobalFunction.scaleDown(bitmap, 380, true);

            try {
                stream.close();
                stream = null;
            } catch (IOException e) {

                e.printStackTrace();
            }

            ivSignature.setImageBitmap(bitmap);
        }
    }

    private void initUI() {

        edtJumlah = (EditText) layout.findViewById(R.id.edt_jumlah);
        edtJumlah.addTextChangedListener(iv.editTextCurrency(edtJumlah));
        edtLamaAngsuran = (EditText) layout.findViewById(R.id.edt_lama_angsuran);
        edtJumlahAngsuran = (EditText) layout.findViewById(R.id.edt_jumlah_angsuran);
        llDaftar = (LinearLayout) layout.findViewById(R.id.ll_daftar_pinjaman);
        llSimpan = (LinearLayout) layout.findViewById(R.id.ll_simpan_pinjaman);
        llLoadImage = (LinearLayout) layout.findViewById(R.id.ll_load_image);
        llDraw = (LinearLayout) layout.findViewById(R.id.ll_draw);
        ivSignature = (ImageView) layout.findViewById(R.id.iv_signature);

        initEvent();
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), LOAD_REQUEST_CODE);
    }

    //region initEvent()
    private void initEvent(){

        edtJumlah.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                hitungJumlahAngsuran();
            }
        });

        edtLamaAngsuran.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                hitungJumlahAngsuran();
            }
        });

        llLoadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showFileChooser();
            }
        });

        llDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), SignatureContainer.class);
                startActivityForResult(intent, UPLOAD_REQUEST_CODE);
            }
        });

        llDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), DetailPengajuan.class);
                intent.putExtra("jenis", Utils.Pinjaman);
                startActivity(intent);
            }
        });

        llSimpan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                jumlahPinjaman = edtJumlah.getText().toString().replace(",", "");
                jumlahPinjaman = jumlahPinjaman.replace(".", "");
                if(iv.parseNullInteger(jumlahPinjaman) <= 0){

                    edtJumlah.setError("Jumlah Pinjaman harus lebih dari dari 0");
                    return;
                }

                int jumlah = iv.parseNullInteger(jumlahPinjaman);
                int lama = iv.parseNullInteger(edtLamaAngsuran.getText().toString());
                Double bunga = 0.00;

                if(jumlah > 80000000){

                    edtJumlah.requestFocus();
                    edtJumlah.setError("Maksimal 80 Juta");
                    return;
                }

                if(lama <= 0){
                    edtLamaAngsuran.requestFocus();
                    edtLamaAngsuran.setError("Lama Angsuran harus lebih dari 0");
                    return;
                }

                if(jumlah <= 5000000){

                    if(lama > 20){
                        edtLamaAngsuran.requestFocus();
                        edtLamaAngsuran.setError("Maksimal 20 Bulan");
                        return;
                    }
                }else {
                    if(lama > 80){
                        edtLamaAngsuran.requestFocus();
                        edtLamaAngsuran.setError("Maksimal 80 Bulan");
                        return;
                    }
                }

                if(jumlahAngsuran <= 0 ){
                    edtJumlahAngsuran.requestFocus();
                    edtJumlahAngsuran.setError("Jumlah angsuran harus lebih dari 0");
                    return;
                }

                if(bitmap == null){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Silahkan masukkan tanda tangan anda")
                            .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                    return;
                }

                NumberFormat nf = NumberFormat.getInstance();
                nf.setMaximumFractionDigits(0);

                jumlahAngsuranString = nf.format(jumlahAngsuran);
                jumlahAngsuranString = jumlahAngsuranString.replace(",", "");
                jumlahAngsuranString = jumlahAngsuranString.replace(".", "");

                simpanPinjaman();
            }
        });
    }

    private void hitungJumlahAngsuran(){

        jumlahPinjaman = edtJumlah.getText().toString().replace(",", "");
        jumlahPinjaman = jumlahPinjaman.replace(".", "");
        int jumlah = iv.parseNullInteger(jumlahPinjaman);
        int lama = iv.parseNullInteger(edtLamaAngsuran.getText().toString());
        Double bunga = 0.00;
        jumlahAngsuran = 0.00;

        if(jumlah > 80000000){

            edtJumlah.setError("Maksimal 80 Juta");
        }

        if(jumlah <= 5000000){

            if(lama > 20){
                edtLamaAngsuran.setError("Maksimal 20 Bulan");
            }
        }else {
            if(lama > 80){
                edtLamaAngsuran.setError("Maksimal 80 Bulan");
            }
        }

        try {
            bunga = 0.8 / 100 * jumlah;
            if(lama > 0 && jumlah > 0){
                jumlahAngsuran = bunga + (Double.parseDouble(String.valueOf(jumlah))/Double.parseDouble(String.valueOf(lama)));
            }else{
                jumlahAngsuran = 0.00;
            }
        }catch (Exception e){
            e.printStackTrace();
            jumlahAngsuran = 0.00;
        }
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(2);
        edtJumlahAngsuran.setText(String.valueOf(nf.format(jumlahAngsuran)));
    }

    private void simpanPinjaman(){

        showProgressDialog();
        JSONObject jBody = new JSONObject();
        SessionManager session = new SessionManager(getContext());
        try {
            jBody.put("signature", ImageUtils.convert(bitmap));
            jBody.put("nik", session.getNik());
            jBody.put("nilai", jumlahPinjaman);
            jBody.put("lama_angsuran", edtLamaAngsuran.getText().toString());
            jBody.put("jumlah_angsuran", jumlahAngsuranString);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(getContext(), jBody, "POST", WebServiceURL.insertPinjaman, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                JSONObject responseAPI;
                try {
                    responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");

                    if(iv.parseNullInteger(status) == 200){

                        Toast.makeText(getContext(), "Data Pinjaman berhasil disimpan", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getContext(), DetailPengajuan.class);
                        intent.putExtra("jenis", Utils.Pinjaman);
                        startActivity(intent);
                        edtJumlah.setText("");
                        edtLamaAngsuran.setText("");
                        ivSignature.setImageResource(0);
                        bitmap = null;
                        dismissProgressDialog();
                    }else{
                        Toast.makeText(getContext(), "Data tidak tersimpan, silahkan coba beberapa saat lagi", Toast.LENGTH_LONG).show();
                        dismissProgressDialog();
                    }
                    dismissProgressDialog();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Data tidak tersimpan, silahkan coba beberapa saat lagi", Toast.LENGTH_LONG).show();
                    dismissProgressDialog();
                }

                dismissProgressDialog();
            }

            @Override
            public void onError(String result) {
                Toast.makeText(getContext(), "Data tidak tersimpan, silahkan coba beberapa saat lagi", Toast.LENGTH_LONG).show();
                dismissProgressDialog();
            }
        });
    }
    private void showProgressDialog(){
        progressDialog = new ProgressDialog(getContext(),
                R.style.CustomLoginDialogBar);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Mengirim...");
        progressDialog.show();
    }

    private void dismissProgressDialog(){
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }
}
