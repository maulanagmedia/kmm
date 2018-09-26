package gmedia.net.id.kopkarmitramakmur.NavPenarikanSimpanan;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.kopkarmitramakmur.R;
import gmedia.net.id.kopkarmitramakmur.ActivityDetailPengajuan.DetailPengajuan;
import gmedia.net.id.kopkarmitramakmur.SignatureContainer;
import gmedia.net.id.kopkarmitramakmur.Util.ApiVolley;
import gmedia.net.id.kopkarmitramakmur.Util.Converter;
import gmedia.net.id.kopkarmitramakmur.Util.GlobalFunction;
import gmedia.net.id.kopkarmitramakmur.Util.ImageUtils;
import gmedia.net.id.kopkarmitramakmur.Util.ItemValidation;
import gmedia.net.id.kopkarmitramakmur.Util.JenisTabunganModel;
import gmedia.net.id.kopkarmitramakmur.Util.SessionManager;
import gmedia.net.id.kopkarmitramakmur.Util.Utils;
import gmedia.net.id.kopkarmitramakmur.Util.WebServiceURL;

public class MainPenarikanSimpanan extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private View layout;

    private String mParam1;
    private String mParam2;

    private LinearLayout llNoTabungan;
    private LinearLayout llNoTabunganBaru;
    private EditText edtNoTabunganBaru;
    private EditText edtJumlah;
    private LinearLayout llDaftar;
    private LinearLayout llSimpan;
    private List<String> jenisTabunganList, noTabunganList;
    private ItemValidation iv = new ItemValidation();
    private String TAG = "Penarikan";
    private String noTabungan = "", jumlahPenarikan;
    private List<JenisTabunganModel> jenisTabunganModels;
    private String selectedJenisTabungan = "", selectedJenisTabunganString = "";

    public static int UPLOAD_REQUEST_CODE = 21;
    public static int LOAD_REQUEST_CODE = 22;
    private static int RESULT_OK = -1;
    private LinearLayout llLoadImage, llDraw;
    private ImageView ivSignature;
    private static Bitmap bitmap;
    private ProgressDialog progressDialog;
    private TextView tvJenisTabungan, tvNoTabungan;
    private LinearLayout llJenisTabunganButton, llNoTabunganButton;
    private int jenisTabunganSelected = 0, noTabunganSelected = 0;

    public MainPenarikanSimpanan() {

    }

    public static MainPenarikanSimpanan newInstance(String param1, String param2) {
        MainPenarikanSimpanan fragment = new MainPenarikanSimpanan();
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
        // Inflate the layout for this fragment
        layout = inflater.inflate(R.layout.fragment_main_penarikan_simpanan, container, false);
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

        llNoTabungan = (LinearLayout) layout.findViewById(R.id.ll_no_tabungan);
        llNoTabunganBaru = (LinearLayout) layout.findViewById(R.id.ll_no_tabungan_baru);
        edtNoTabunganBaru = (EditText) layout.findViewById(R.id.edt_no_tabungan_baru);
        edtJumlah = (EditText) layout.findViewById(R.id.edt_jumlah);
        edtJumlah.addTextChangedListener(iv.editTextCurrency(edtJumlah));
        llDaftar = (LinearLayout) layout.findViewById(R.id.ll_daftar_penarikan);
        llSimpan = (LinearLayout) layout.findViewById(R.id.ll_simpan_penarikan);
        llLoadImage = (LinearLayout) layout.findViewById(R.id.ll_load_image);
        llDraw = (LinearLayout) layout.findViewById(R.id.ll_draw);
        ivSignature = (ImageView) layout.findViewById(R.id.iv_signature);

        tvJenisTabungan = (TextView) layout.findViewById(R.id.tv_jenis_tabungan);
        tvNoTabungan = (TextView) layout.findViewById(R.id.tv_no_tabungan);
        llJenisTabunganButton = (LinearLayout) layout.findViewById(R.id.ll_jenis_tabungan_btn);
        llNoTabunganButton = (LinearLayout) layout.findViewById(R.id.ll_no_tabungan_btn);

        getJenisTabungan();
        initEvent();
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), LOAD_REQUEST_CODE);
    }

    //region getJenisTabungan()
    private void getJenisTabungan() {

        JSONObject jBody = new JSONObject();
        ApiVolley request = new ApiVolley(getContext(), jBody, "GET", WebServiceURL.getJenisTabungan, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                JSONObject responseAPI;
                try {
                    responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");
                    jenisTabunganList = new ArrayList<>();
                    jenisTabunganModels = new ArrayList<>();

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray jsonArray = responseAPI.getJSONArray("response");

                        for(int i = 0; i < jsonArray.length();i++){

                            JSONObject item = jsonArray.getJSONObject(i);
                            jenisTabunganList.add(item.getString("nama"));
                            jenisTabunganModels.add(new JenisTabunganModel(item.getString("kode"), item.getString("nama")));
                        }
                    }

                    getJenisTabunganEvent(jenisTabunganList);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {

            }
        });
    }

    private void getJenisTabunganEvent(final List<String> listItem){

        if(listItem != null && listItem.size() > 0){
            jenisTabunganSelected = 0;
            tvJenisTabungan.setText(listItem.get(jenisTabunganSelected).toString());
            getNoTabungan(tvJenisTabungan.getText().toString());

            if(Converter.convertJenisTabungan(tvJenisTabungan.getText().toString()) == Converter.convertJenisTabungan("Simpanan Sukarela")){
                //llNoTabungan.setVisibility(View.GONE);
            }else {
                llNoTabungan.setVisibility(View.VISIBLE);
            }

            llJenisTabunganButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tampilJenisTabunganChosen(listItem);
                }
            });
        }
    }

    private void tampilJenisTabunganChosen(final List<String> listItem) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialog1);

        CharSequence[] choiceList = new CharSequence[listItem.size()];
        for(int x = 0; x < listItem.size();x++){
            choiceList[x] = listItem.get(x).toString();
        }

        final CharSequence[] finalChoice = choiceList;

        final int selected = jenisTabunganSelected; // select at 0
        final int[] lastSelected = {jenisTabunganSelected}; // select at 0

        builder.setSingleChoiceItems(
                finalChoice,
                jenisTabunganSelected,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(
                            DialogInterface dialog,
                            int which) {

                        lastSelected[0] = which;
                        /*Toast.makeText(
                                DetailInfoTabungan.this,
                                "Select "+finalChoice[which],
                                Toast.LENGTH_SHORT
                        )
                                .show();*/
                    }
                });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                jenisTabunganSelected = lastSelected[0];
                tvJenisTabungan.setText(listItem.get(jenisTabunganSelected).toString());
                if(Converter.convertJenisTabungan(tvJenisTabungan.getText().toString()) == Converter.convertJenisTabungan("Simpanan Sukarela")){
                    //llNoTabungan.setVisibility(View.GONE);
                    //tvNoTabungan.setText("");
                    getNoTabungan(tvJenisTabungan.getText().toString());
                }else {
                    llNoTabungan.setVisibility(View.VISIBLE);
                    getNoTabungan(tvJenisTabungan.getText().toString());
                }
            }
        });

        builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                jenisTabunganSelected = selected;
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void getNoTabungan(String jenisTabungan){

        //if(Converter.convertJenisTabungan(jenisTabungan) != Converter.convertJenisTabungan("Simpanan Sukarela")){

            JSONObject jBody = new JSONObject();
            SessionManager session = new SessionManager(getContext());
            try {
                jBody.put("nik", session.getNik());
                jBody.put("kode_tab", jenisTabungan);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ApiVolley request = new ApiVolley(getContext(), jBody, "POST", WebServiceURL.getNoTabungan, "", "", 0, new ApiVolley.VolleyCallback() {
                @Override
                public void onSuccess(String result) {

                    JSONObject responseAPI;
                    try {
                        responseAPI = new JSONObject(result);
                        String status = responseAPI.getJSONObject("metadata").getString("status");
                        noTabunganList = new ArrayList<>();
                        //noTabunganList.add("Rekening Baru");

                        if(iv.parseNullInteger(status) == 200){

                            JSONArray jsonArray = responseAPI.getJSONArray("response");

                            for(int i = 0; i < jsonArray.length();i++){

                                JSONObject item = jsonArray.getJSONObject(i);
                                noTabunganList.add(item.getString("no_tab"));
                            }
                        }

                        getNoTabunganEvent(noTabunganList);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(String result) {

                }
            });
        //}
    }

    private void getNoTabunganEvent(final List<String> listItem){

        if(listItem != null && listItem.size() > 0){
            noTabunganSelected = 0;
            tvNoTabungan.setText(listItem.get(noTabunganSelected).toString());

            llNoTabunganButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    tampilNoTabunganChosen(listItem);
                }
            });
        }else{

            llNoTabunganButton.setOnClickListener(null);
            tvNoTabungan.setText("");
        }
    }

    private void tampilNoTabunganChosen(final List<String> listItem) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialog2);

        CharSequence[] choiceList = new CharSequence[listItem.size()];
        for(int x = 0; x < listItem.size();x++){
            choiceList[x] = listItem.get(x).toString();
        }

        final CharSequence[] finalChoice = choiceList;

        final int selected = noTabunganSelected; // select at 0
        final int[] lastSelected = {noTabunganSelected}; // select at 0

        builder.setSingleChoiceItems(
                finalChoice,
                noTabunganSelected,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(
                            DialogInterface dialog,
                            int which) {
                        lastSelected[0] = which;
                        /*Toast.makeText(
                                DetailInfoTabungan.this,
                                "Select "+finalChoice[which],
                                Toast.LENGTH_SHORT
                        )
                                .show();*/
                    }
                });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                noTabunganSelected = lastSelected[0];
                tvNoTabungan.setText(listItem.get(noTabunganSelected).toString());
            }
        });

        builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                noTabunganSelected = selected;
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    //endregion

    //region initEvent()
    private void initEvent(){

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
                intent.putExtra("jenis", Utils.Penarikan);
                startActivity(intent);
            }
        });

        llSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(tvNoTabungan.getText().length() > 0 && tvJenisTabungan.getText().length() > 0 /*&& Converter.convertJenisTabungan(tvJenisTabungan.getText().toString()) != Converter.convertJenisTabungan("Simpanan Sukarela")*/){

                    jumlahPenarikan = edtJumlah.getText().toString().replace(",", "");
                    jumlahPenarikan = jumlahPenarikan.replace(".", "");
                    if(iv.parseNullInteger(jumlahPenarikan) <= 0){

                        edtJumlah.setError("Jumlah Penarikan harus lebih dari dari 0");
                        return;
                    }

                    if(bitmap == null){
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Anda Belum Tanda Tangan")
                                .setIcon(R.mipmap.ic_kopkar_logo)
                                .setMessage("Silahkan gambar tanda tangan anda dengan menekan tombol Gambar Tanda Tangan")
                                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).show();
                        return;
                    }

                    simpanPenarikan();
                }else if(Converter.convertJenisTabungan(tvJenisTabungan.getText().toString()) == Converter.convertJenisTabungan("Simpanan Sukarela")){
                    /*noTabungan = "";
                    jumlahPenarikan = edtJumlah.getText().toString().replace(",", "");
                    jumlahPenarikan = jumlahPenarikan.replace(".", "");
                    if(iv.parseNullInteger(jumlahPenarikan) <= 0){

                        edtJumlah.setError("Jumlah Penarikan harus lebih dari dari 0");
                        return;
                    }

                    if(bitmap == null){
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Anda Belum Tanda Tangan")
                                .setIcon(R.mipmap.ic_kopkar_logo)
                                .setMessage("Silahkan gambar tanda tangan anda dengan menekan tombol Gambar TTD")
                                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).show();
                        return;
                    }

                    simpanPenarikan();*/
                    Toast.makeText(getContext(), "Anda belum memiliki Tabungan terdaftar", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getContext(), "Anda belum memiliki Tabungan terdaftar", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void simpanPenarikan(){

        showProgressDialog();
        JSONObject jBody = new JSONObject();
        final JenisTabunganModel model = jenisTabunganModels.get(jenisTabunganSelected);
        selectedJenisTabungan = model.getId();
        SessionManager session = new SessionManager(getContext());
        try {
            jBody.put("signature", ImageUtils.convert(bitmap));
            jBody.put("nik", session.getNik());
            jBody.put("jumlah", jumlahPenarikan);
            jBody.put("jenis_tabungan", selectedJenisTabungan);
            jBody.put("no_tabungan", tvNoTabungan.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(getContext(), jBody, "POST", WebServiceURL.insertPenarikan, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                JSONObject responseAPI;
                try {
                    responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");

                    if(iv.parseNullInteger(status) == 200){

                        Toast.makeText(getContext(), "Data Penarikan berhasil disimpan", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getContext(), DetailPengajuan.class);
                        intent.putExtra("jenis", Utils.Penarikan);
                        intent.putExtra("jenis_tabungan", tvJenisTabungan.getText().toString());
                        intent.putExtra("no_tabungan", tvNoTabungan.getText().toString());
                        startActivity(intent);
                        edtJumlah.setText("");
                        ivSignature.setImageResource(0);
                        bitmap = null;
                    }else{
                        Toast.makeText(getContext(), "Data tidak tersimpan, silahkan coba beberapa saat lagi", Toast.LENGTH_LONG).show();
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
    //endregion

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
