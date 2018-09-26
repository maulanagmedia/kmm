package gmedia.net.id.kopkarmitramakmur.MenuInformasiTabungan;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import gmedia.net.id.kopkarmitramakmur.R;
import gmedia.net.id.kopkarmitramakmur.GeneralModel.CustomListItem;
import gmedia.net.id.kopkarmitramakmur.PDFReader;
import gmedia.net.id.kopkarmitramakmur.Util.ApiVolley;
import gmedia.net.id.kopkarmitramakmur.Util.Converter;
import gmedia.net.id.kopkarmitramakmur.Util.ItemValidation;
import gmedia.net.id.kopkarmitramakmur.Util.PDFViewerActivities;
import gmedia.net.id.kopkarmitramakmur.Util.SessionManager;
import gmedia.net.id.kopkarmitramakmur.Util.WebServiceURL;

public class DetailInfoTabungan extends AppCompatActivity {

    private LinearLayout llTampilTransaksi, llNoTabungan;
    private CheckBox cbPotongGaji;
    private List<String> jenisTabunganList, noTabunganList;
    private LinkedHashMap<String, List<CustomListItem>> jenisTabunganHashList;
    private String nama, nik, dept;
    private TextView tvTitle;
    private TextView tvSubTitle;
    private TextView tvDept;
    private SessionManager session;
    private List<CustomListItem> masterList;
    private ItemValidation iv = new ItemValidation();
    private TextView tvNama;
    private TextView tvTotal;
    private LinearLayout llDownloadMutasi;
    private AlertDialog.Builder dialog;
    private AlertDialog dialogViews;
    private View dialogView;
    private EditText edtFrom, edtTo;
    private LinearLayout llCancel, llDownload;
    private ProgressDialog progressDialog;
    private TextView tvJenisTabungan, tvNoTabungan;
    private LinearLayout llJenisTabunganButton, llNoTabunganButton;
    private int jenisTabunganSelected = 0, noTabunganSelected = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_info_tabungan);

        initUI();
    }

    private void initUI() {

        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvSubTitle = (TextView) findViewById(R.id.tv_subtitle);
        tvDept = (TextView) findViewById(R.id.tv_dept);
        tvNama = (TextView) findViewById(R.id.tv_nama);
        tvTotal = (TextView) findViewById(R.id.tv_total);

        llTampilTransaksi = (LinearLayout) findViewById(R.id.ll_tampil_transaksi);
        llNoTabungan = (LinearLayout) findViewById(R.id.ll_no_tabungan);
        cbPotongGaji = (CheckBox) findViewById(R.id.cb_potong_gaji);

        tvJenisTabungan = (TextView) findViewById(R.id.tv_jenis_tabungan);
        tvNoTabungan = (TextView) findViewById(R.id.tv_no_tabungan);
        llJenisTabunganButton = (LinearLayout) findViewById(R.id.ll_jenis_tabungan_btn);
        llNoTabunganButton = (LinearLayout) findViewById(R.id.ll_no_tabungan_btn);

        llDownloadMutasi = (LinearLayout) findViewById(R.id.ll_download_mutasi);
        setTitle("Informasi Tabungan");

        session = new SessionManager(DetailInfoTabungan.this);
        nama = session.getNama();
        nik = session.getNik();
        dept = session.getDepartement();
        tvTitle.setText(nama);
        tvSubTitle.setText(nik);
        tvDept.setText(dept);

        //getListDataTabungan();
        getJenisTabungan();

        llTampilTransaksi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tvJenisTabungan.getText().length() > 0){
                    tampilkanTransaksi();
                }else{
                    Toast.makeText(DetailInfoTabungan.this, "Anda belum memiliki No Tabungan", Toast.LENGTH_LONG).show();
                }
            }
        });

        llDownloadMutasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tvJenisTabungan.getText().length() > 0){
                    if((Converter.convertJenisTabungan(tvJenisTabungan.getText().toString()) == Converter.convertJenisTabungan("Simpanan Sukarela")) || tvNoTabungan.getText().length() > 0){

                        String jenisTabungan = tvJenisTabungan.getText().toString();
                        String noTabungan = tvNoTabungan.getText().toString();

                        ShowDownloadDialog(jenisTabungan, noTabungan);
                    }else{
                        Toast.makeText(DetailInfoTabungan.this, "Anda belum memiliki Tabungan terdaftar", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(DetailInfoTabungan.this, "Anda belum memiliki No Tabungan", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void ShowDownloadDialog(final String jenisTabungan, final String noTabungan) {
        dialog = new AlertDialog.Builder(DetailInfoTabungan.this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        dialogView = inflater.inflate(R.layout.layout_download_mutasi, null);
        dialog.setView(dialogView);
        dialog.setCancelable(false);
        dialog.setIcon(R.mipmap.ic_kopkar_logo);
        dialog.setTitle("Rentang Mutasi");

        edtFrom = (EditText) dialogView.findViewById(R.id.edt_from);
        edtTo = (EditText) dialogView.findViewById(R.id.edt_to);

        iv.datePickerEventKlick(DetailInfoTabungan.this, edtFrom, getResources().getString(R.string.format_date));
        iv.datePickerEventKlick(DetailInfoTabungan.this, edtTo, getResources().getString(R.string.format_date));

        llCancel = (LinearLayout) dialogView.findViewById(R.id.ll_cancel);
        llDownload = (LinearLayout) dialogView.findViewById(R.id.ll_download);

        llCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogViews.dismiss();
            }
        });

        llDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(edtFrom.getText().toString().length() <= 0){

                    edtFrom.setError("Tanggal Dari tidak boleh kosong");
                    edtFrom.requestFocus();
                    return;
                }

                if(edtTo.getText().toString().length() <= 0){

                    edtTo.setError("Tanggal Sampai tidak boleh kosong");
                    edtTo.requestFocus();
                    return;
                }

                if(iv.isMoreThanTheDate(edtFrom, edtTo, getResources().getString(R.string.format_date))){

                    edtTo.setError("Tanggal Sampai harus lebih besar dari tanggal Dari");
                    edtTo.requestFocus();
                    return;
                }

                getDownloadURL();
            }
        });

        dialogViews = dialog.create();
        dialogViews.show();

    }

    private void getDownloadURL() {

        JSONObject jBody = new JSONObject();
        SessionManager session = new SessionManager(DetailInfoTabungan.this);
        String jenisTabungan = tvJenisTabungan.getText().toString();
        String noTabungan = tvNoTabungan.getText().toString();
        try {
            jBody.put("nik", session.getNik());
            jBody.put("tgl_dari", edtFrom.getText().toString());
            jBody.put("tgl_sampai", edtTo.getText().toString());
            jBody.put("jenis_tabungan", jenisTabungan);
            jBody.put("no_tabungan", noTabungan);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(DetailInfoTabungan.this, jBody, "POST", WebServiceURL.downloadMutasiPDF, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                JSONObject responseAPI;
                try {
                    responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");
                    noTabunganList = new ArrayList<>();
                    //noTabunganList.add("Rekening Baru");

                    if(iv.parseNullInteger(status) == 200){

                        JSONObject jsonObject = responseAPI.getJSONObject("response");

                        try{

                            new DownloadFileFromURL().execute(jsonObject.getString("link"), jsonObject.getString("filename"));
                        }catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(DetailInfoTabungan.this,"File tidak benar, silahkan pilih tanggal yang berbeda",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(DetailInfoTabungan.this,"File tidak benar, silahkan pilih tanggal yang berbeda",Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(DetailInfoTabungan.this,"File tidak benar, silahkan pilih tanggal yang berbeda",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String result) {

            }
        });
    }

    //region getJenisTabungan()
    private void getJenisTabungan() {

        JSONObject jBody = new JSONObject();
        ApiVolley request = new ApiVolley(DetailInfoTabungan.this, jBody, "GET", WebServiceURL.getJenisTabungan, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                JSONObject responseAPI;
                try {
                    responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");
                    jenisTabunganList = new ArrayList<>();

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray jsonArray = responseAPI.getJSONArray("response");

                        for(int i = 0; i < jsonArray.length();i++){

                            JSONObject item = jsonArray.getJSONObject(i);
                            jenisTabunganList.add(item.getString("nama"));
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
//                llNoTabungan.setVisibility(View.GONE);
//                getListDataTabungan("");
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

        final AlertDialog.Builder builder = new AlertDialog.Builder(DetailInfoTabungan.this, R.style.AlertDialog1);

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
//                    llNoTabungan.setVisibility(View.GONE);
//                    tvNoTabungan.setText("");
//                    getListDataTabungan("");
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

        JSONObject jBody = new JSONObject();
        SessionManager session = new SessionManager(DetailInfoTabungan.this);
        try {
            jBody.put("nik", session.getNik());
            jBody.put("kode_tab", jenisTabungan);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(DetailInfoTabungan.this, jBody, "POST", WebServiceURL.getNoTabungan, "", "", 0, new ApiVolley.VolleyCallback() {
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
    }

    private void getNoTabunganEvent(final List<String> listItem){

        if(listItem != null && listItem.size() > 0){
            noTabunganSelected = 0;
            tvNoTabungan.setText(listItem.get(noTabunganSelected).toString());
            getListDataTabungan(tvNoTabungan.getText().toString());

            llNoTabunganButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    tampilNoTabunganChosen(listItem);
                }
            });
        }else{

            llNoTabunganButton.setOnClickListener(null);
            tvNoTabungan.setText("");
            tvNama.setText("");
            tvTotal.setText(iv.ChangeToRupiahFormat("0"));
        }
    }

    private void tampilNoTabunganChosen(final List<String> listItem) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(DetailInfoTabungan.this, R.style.AlertDialog2);

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
                getListDataTabungan(tvNoTabungan.getText().toString());
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

    private void getListDataTabungan(final String noTab) {

        JSONObject jBody = new JSONObject();

        tvNama.setText("");
        tvTotal.setText(iv.ChangeToRupiahFormat("0"));

        ApiVolley request = new ApiVolley(DetailInfoTabungan.this, jBody, "GET", WebServiceURL.getTabungan + nik, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                JSONObject responseAPI;
                try {
                    responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");
                    masterList = new ArrayList<>();
                    noTabunganList = new ArrayList<>();
                    jenisTabunganList = new ArrayList<>();

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray jsonArray = responseAPI.getJSONArray("response");

                        for(int i = 0; i < jsonArray.length();i++){

                            JSONObject item = jsonArray.getJSONObject(i);
                            masterList.add(new CustomListItem(item.getString("id_tab") , item.getString("kd_tab"), item.getString("no_tab"), "", item.getString("tot_tab")));

                            if(item.getString("no_tab").equals(noTab)){
                                tvNama.setText(nama);
                                tvTotal.setText(iv.ChangeToRupiahFormat(item.getString("tot_tab")));
                            }

                            /*if(item.getString("no_tab").length() > 0) noTabunganList.add(item.getString("no_tab"));
                            jenisTabunganList.add(item.getString("kd_tab"));*/

                        }
                    }

                    //setListAdapter(noTabunganList);
                    //FilterJenisTabungan(masterList);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {

            }
        });
    }
    //endregion

    private void tampilkanTransaksi() {

        if(tvNoTabungan.getText().length() > 0/* && Converter.convertJenisTabungan(tvJenisTabungan.getText().toString()) != Converter.convertJenisTabungan("simpanan sukarela")*/){

            String jenisTabungan = tvJenisTabungan.getText().toString();
            String noTabungan = tvNoTabungan.getText().toString();
            Boolean potongGaji = cbPotongGaji.isSelected() ? true : false;
            String keterangan = nama + " (" + nik + ")";

            Intent intent = new Intent(DetailInfoTabungan.this, SubDetailInfoTabungan.class);
            intent.putExtra("no_tabungan", noTabungan);
            intent.putExtra("kode_tabungan", jenisTabungan);
            intent.putExtra("potong_gaji", potongGaji);
            intent.putExtra("keterangan", keterangan);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, android.R.anim.slide_in_left);

        }else if(Converter.convertJenisTabungan(tvJenisTabungan.getText().toString()) == Converter.convertJenisTabungan("simpanan sukarela")){

            /*String jenisTabungan = tvJenisTabungan.getText().toString();
            String noTabungan = tvNoTabungan.getText().toString();
            Boolean potongGaji = cbPotongGaji.isSelected() ? true : false;
            String keterangan = nama + " (" + nik + ")";

            Intent intent = new Intent(DetailInfoTabungan.this, SubDetailInfoTabungan.class);
            intent.putExtra("no_tabungan", noTabungan);
            intent.putExtra("kode_tabungan", jenisTabungan);
            intent.putExtra("potong_gaji", potongGaji);
            intent.putExtra("keterangan", keterangan);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, android.R.anim.slide_in_left);*/
            Toast.makeText(DetailInfoTabungan.this, "Anda belum memiliki Tabungan terdaftar", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(DetailInfoTabungan.this, "Anda belum memiliki Tabungan terdaftar", Toast.LENGTH_LONG).show();
        }
    }

    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        String extension;
        boolean success;
        String fileName;
        String donwloadedFilePath;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog();
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();

                // this will be useful so that you can show a tipical 0-100% progress bar
                int lenghtOfFile = conection.getContentLength();

                // download the file
                extension = f_url[0].substring(f_url[0].lastIndexOf("."));
                File vidDir = new File(android.os.Environment.getExternalStoragePublicDirectory
                        (Environment.DIRECTORY_DOCUMENTS) + File.separator + "KMM");
                vidDir.mkdirs();

                // create unique identifier
                Date date = new Date();
                // create file name
                //fileName = "mutasi_" + date.getYear()+date.getMonth()+date.getDate()+date.getHours()+date.getMinutes()+date.getSeconds()+extension;
                fileName = f_url[1];

                File fileDownload = new File(vidDir.getAbsolutePath(), fileName);
                donwloadedFilePath = fileDownload.getAbsolutePath();
                success = false;
                InputStream input = new BufferedInputStream(url.openStream());
                // Output stream
                OutputStream output = new FileOutputStream(fileDownload);
                byte data[] = new byte[1024];
                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress(""+(int)((total*100)/lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                    success = true;
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            progressDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {

            dismissDialog();

            //String downloadedPath = "file://" + Environment.getExternalStorageDirectory().toString() + "/downloadedfile."+extension;

            showFinishDialog(success);
        }
        private void showDialog(){
            progressDialog = new ProgressDialog(DetailInfoTabungan.this);
            progressDialog.setMessage("Downloading file. Please wait...");
            progressDialog.setIndeterminate(false);
            progressDialog.setMax(100);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.show();
        }

        private void dismissDialog(){
            progressDialog.dismiss();
        }

        private void showFinishDialog(final boolean downloadSuccess){

            String title, message;
            if(downloadSuccess){

                title = "Download selesai";
                message = "File telah terdownload "+ donwloadedFilePath;

                AlertDialog.Builder builder = new AlertDialog.Builder(DetailInfoTabungan.this)
                        .setTitle(title)
                        .setIcon(R.mipmap.ic_kopkar_logo)
                        .setMessage(message)
                        .setPositiveButton("Buka", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                String path = donwloadedFilePath;
                                File targetFile = new File(path);
                                Uri targetUri = Uri.fromFile(targetFile);

                                Intent intent;
                                intent = new Intent(Intent.ACTION_VIEW);
                                intent.setDataAndType(targetUri, "application/pdf");

                                try {
                                    startActivity(intent);
                                }catch (Exception e){


                                    final Intent intent2 = new Intent(DetailInfoTabungan.this, PDFReader.class);
                                    intent2.putExtra(PDFViewerActivities.EXTRA_PDFFILENAME, path);
                                    try {
                                        startActivity(intent2);
                                    } catch (Exception e2) {
                                        e2.printStackTrace();
                                    }
                                }
                            }
                        })
                        .setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                builder.show();
            }else {

                title = "Download terganggu";
                message = "Tidak dapat mengunduh file, silahkan coba kembali";

                final AlertDialog.Builder builder = new AlertDialog.Builder(DetailInfoTabungan.this)
                        .setTitle(title)
                        .setIcon(R.mipmap.ic_kopkar_logo)
                        .setMessage(message)
                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder.show();
            }

            //delete file from server
            JSONObject jBody = new JSONObject();

            try {
                jBody.put("file_name", fileName);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ApiVolley request = new ApiVolley(DetailInfoTabungan.this, jBody, "POST", WebServiceURL.deletePDF, "", "", 0, new ApiVolley.VolleyCallback() {
                @Override
                public void onSuccess(String result) {

                    JSONObject responseAPI;
                    try {
                        responseAPI = new JSONObject(result);
                        String status = responseAPI.getJSONObject("metadata").getString("status");

                        if(iv.parseNullInteger(status) == 200){

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(String result) {

                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }
}
