package gmedia.net.id.kopkarmitramakmur.ActivityDetailPengajuan;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import gmedia.net.id.kopkarmitramakmur.R;
import gmedia.net.id.kopkarmitramakmur.ActivityDetailPengajuan.Adapter.PengajuanAdapter;
import gmedia.net.id.kopkarmitramakmur.GeneralModel.CustomListItem;
import gmedia.net.id.kopkarmitramakmur.MainActivity;
import gmedia.net.id.kopkarmitramakmur.Util.ApiVolley;
import gmedia.net.id.kopkarmitramakmur.Util.Converter;
import gmedia.net.id.kopkarmitramakmur.Util.GlobalFunction;
import gmedia.net.id.kopkarmitramakmur.Util.ItemValidation;
import gmedia.net.id.kopkarmitramakmur.Util.JenisTabunganModel;
import gmedia.net.id.kopkarmitramakmur.Util.SessionManager;
import gmedia.net.id.kopkarmitramakmur.Util.Utils;
import gmedia.net.id.kopkarmitramakmur.Util.WebServiceURL;

public class DetailPengajuan extends AppCompatActivity {

    private static RecyclerView rvPengajuan;
    private static String jenisDetail;
    private static String noTransaksi = "";
    private SearchView searchView;
    public static List<CustomListItem> masterList;
    private static List<CustomListItem> filterList;
    private static List<CustomListItem> filterListALL;
    private ItemValidation iv = new ItemValidation();
    private SessionManager session;
    private String nik;
    private static boolean firstLoad = true;
    private static LinearLayout llTampil;
    private static Spinner spBulan, spTahun;
    private static String bulanNow;
    private static String tahunNow;
    private static TextView tvJenisTabungan, tvNoTabungan;
    private static List<JenisTabunganModel> jenisTabunganModels;
    private static List<String> jenisTabunganList, noTabunganList;
    private static String selectedJenisTabungan = "", selectedNoTabungan = "";
    private static String kodeDetail = "";
    private static String selectedJenisTabunganBundle, selectedNoTabunganBundle;
    private String backToHome = "0";

    private static TextView tvJenisTabunganVal, tvNoTabunganVal;
    private static LinearLayout llJenisTabungan, llNoTabungan, llJenisTabunganButton, llNoTabunganButton;
    private static int jenisTabunganSelected = 0, noTabunganSelected = 0;
    private static boolean firstLoadTabungan = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pengajuan);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initUI();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //region OptionBar tak hidden
    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Searhbar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView =
                (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                getFilterList(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {

            @Override
            public boolean onClose() {

                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getFilterList(String keyword){

        filterList = new ArrayList<>();
        for(CustomListItem cli :filterListALL){

            String key = cli.getItem1();
            if(key.toLowerCase().contains(keyword.toLowerCase()) || cli.getItem4().toLowerCase().contains(keyword.toLowerCase())){

                filterList.add(cli);
            }
        }

        setAdapter(filterList);
    }
*/

    //endregion

    @Override
    public void onBackPressed() {

        if( backToHome.equals("1") && !getIntent().getBooleanExtra("backto", false)){

            Intent intent = new Intent(DetailPengajuan.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            finish();
            return;
        }else{
            super.onBackPressed();
            overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
        }
    }

    private void initUI() {

        rvPengajuan = (RecyclerView) findViewById(R.id.rv_detail_pengajuan);
        session = new SessionManager(DetailPengajuan.this);
        llTampil = (LinearLayout) findViewById(R.id.ll_tampilkan);
        tvJenisTabungan = (TextView) findViewById(R.id.tv_jenis_tabungan);
        tvNoTabungan = (TextView) findViewById(R.id.tv_no_tabungan);
        spBulan = (Spinner) findViewById(R.id.sp_bulan);
        spTahun = (Spinner) findViewById(R.id.sp_tahun);

        llJenisTabungan  = (LinearLayout) findViewById(R.id.ll_jenis_tabungan);
        llNoTabungan = (LinearLayout) findViewById(R.id.ll_no_tabungan);
        tvJenisTabunganVal = (TextView) findViewById(R.id.tv_jenis_tabungan_val);
        tvNoTabunganVal = (TextView) findViewById(R.id.tv_no_tabungan_val);
        llJenisTabunganButton = (LinearLayout) findViewById(R.id.ll_jenis_tabungan_btn);
        llNoTabunganButton = (LinearLayout) findViewById(R.id.ll_no_tabungan_btn);

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        bulanNow = Converter.convertBulan(String.valueOf(calendar.get(Calendar.MONTH)+1));
        tahunNow  = String.valueOf(calendar.get(Calendar.YEAR));
        if(!session.saveLogin()){
            session.logoutUser();
        }

        nik = session.getNik();

        Bundle bundle = getIntent().getExtras();

        firstLoadTabungan = true;
        firstLoad = true;
        if(bundle != null){

            jenisDetail = bundle.getString("jenis");
            try {
                noTransaksi = (bundle.getString("no_transaksi") != null) ? bundle.getString("no_transaksi") : "";
            }catch (Exception e){
                noTransaksi = "";
                e.printStackTrace();
            }

            try {
                selectedJenisTabunganBundle = (bundle.getString("jenis_tabungan") != null) ? bundle.getString("jenis_tabungan"): "";
            }catch (Exception e){
                selectedJenisTabunganBundle = "";
                e.printStackTrace();
            }

            try {
                selectedNoTabunganBundle = (bundle.getString("no_tabungan") != null) ? bundle.getString("no_tabungan"): "";
            }catch (Exception e){
                selectedNoTabunganBundle = "";
                e.printStackTrace();
            }

            try {

                if(bundle.getString("back") != null) backToHome = bundle.getString("back");
            }catch (Exception e){

                e.printStackTrace();
            }

            switch (jenisDetail){
                case Utils.Setoran:
                    setTitle("Setoran Simpanan");
                    setSPVVisibility(View.VISIBLE);
                    kodeDetail = Utils.Setoran;
                    getJenisTabungan();
                    getSetoranData();
                    break;
                case Utils.Penarikan:
                    setTitle("Penarikan Simpanan");
                    setSPVVisibility(View.VISIBLE);
                    kodeDetail = Utils.Penarikan;
                    getJenisTabungan();
                    getPenarikanData();
                    break;
                case  Utils.Pinjaman:
                    setSPVVisibility(View.GONE);
                    kodeDetail = Utils.Pinjaman;
                    setTitle("Pengajuan Pinjaman");
                    getPinjamanData();
                    break;
            }
        }
    }

    private static void setSPVVisibility(int visibility){
        tvJenisTabungan.setVisibility(visibility);
        llJenisTabungan.setVisibility(visibility);
        tvNoTabungan.setVisibility(visibility);
        llNoTabungan.setVisibility(visibility);
    }

    //region getJenisTabungan()
    private void getJenisTabungan() {

        JSONObject jBody = new JSONObject();
        ApiVolley request = new ApiVolley(DetailPengajuan.this, jBody, "GET", WebServiceURL.getJenisTabungan, "", "", 0, new ApiVolley.VolleyCallback() {
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
            tvJenisTabunganVal.setText(((selectedJenisTabunganBundle.length() > 0)? selectedJenisTabunganBundle : listItem.get(jenisTabunganSelected).toString()));

            int x = 0;
            for (String jeTab: listItem){
                if(selectedJenisTabunganBundle.length()>0){
                    if(jeTab.toLowerCase().equals(selectedJenisTabunganBundle.toLowerCase())){
                        jenisTabunganSelected = x;
                        break;
                    }
                    x++;
                }else{
                    break;
                }
            }

            getNoTabungan(tvJenisTabunganVal.getText().toString());

            if(Converter.convertJenisTabungan(tvJenisTabunganVal.getText().toString()) == Converter.convertJenisTabungan("Simpanan Sukarela")){
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

        final AlertDialog.Builder builder = new AlertDialog.Builder(DetailPengajuan.this, R.style.AlertDialog1);

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
                tvJenisTabunganVal.setText(listItem.get(jenisTabunganSelected).toString());
                if(Converter.convertJenisTabungan(tvJenisTabunganVal.getText().toString()) == Converter.convertJenisTabungan("Simpanan Sukarela")){
                    //llNoTabungan.setVisibility(View.GONE);
                    //tvNoTabungan.setText("");
                    getNoTabungan(tvJenisTabunganVal.getText().toString());
                }else {
                    llNoTabungan.setVisibility(View.VISIBLE);
                    getNoTabungan(tvJenisTabunganVal.getText().toString());
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

        if(!jenisTabungan.equals("Simpanan Sukarela".trim())){

            JSONObject jBody = new JSONObject();
            SessionManager session = new SessionManager(DetailPengajuan.this);
            try {
                jBody.put("nik", session.getNik());
                jBody.put("kode_tab", jenisTabungan);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ApiVolley request = new ApiVolley(DetailPengajuan.this, jBody, "POST", WebServiceURL.getNoTabungan, "", "", 0, new ApiVolley.VolleyCallback() {
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
    }

    private void getNoTabunganEvent(final List<String> listItem){

        if(listItem != null && listItem.size() > 0){
            noTabunganSelected = 0;
            tvNoTabunganVal.setText(((firstLoadTabungan && selectedNoTabunganBundle.length()>0) ? selectedNoTabunganBundle : listItem.get(noTabunganSelected).toString()));

            int x = 0;
            for (String noTab: listItem){
                if(firstLoadTabungan && selectedNoTabunganBundle.length()>0){
                    if(noTab.toLowerCase().equals(selectedNoTabunganBundle.toLowerCase())) {
                        noTabunganSelected = x;
                        break;
                    }
                    x++;
                }else{
                    break;
                }
            }

            firstLoadTabungan = false;

            llNoTabunganButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    tampilNoTabunganChosen(listItem);
                }
            });
        }else{

            llNoTabunganButton.setOnClickListener(null);
            tvNoTabunganVal.setText("");
        }
    }

    private void tampilNoTabunganChosen(final List<String> listItem) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(DetailPengajuan.this, R.style.AlertDialog2);

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
                tvNoTabunganVal.setText(listItem.get(noTabunganSelected).toString());
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

    private void getSetoranData(){

        JSONObject jBody = new JSONObject();
        ApiVolley request = new ApiVolley(DetailPengajuan.this, jBody, "GET", WebServiceURL.getSetoranByNIK + nik, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                JSONObject responseAPI;
                try {
                    responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");
                    masterList = new ArrayList<>();

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray jsonArray = responseAPI.getJSONArray("response");

                        for(int i = 0; i < jsonArray.length();i++){

                            JSONObject item = jsonArray.getJSONObject(i);
                            masterList.add(new CustomListItem(item.getString("no_transaksi"), item.getString("tgl_setoran"), item.getString("nama"), item.getString("no_tabungan"), item.getString("jumlah"), GlobalFunction.getStatus(item.getString("status")), item.getString("keterangan")));
                        }
                    }

                    //setAdapter(masterList);
                    parseData(DetailPengajuan.this, masterList, "", "");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {

            }
        });

    }

    private void getPenarikanData(){

        JSONObject jBody = new JSONObject();
        ApiVolley request = new ApiVolley(DetailPengajuan.this, jBody, "GET", WebServiceURL.getPenarikanByNIK + nik, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                JSONObject responseAPI;
                try {
                    responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");
                    masterList = new ArrayList<>();

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray jsonArray = responseAPI.getJSONArray("response");

                        for(int i = 0; i < jsonArray.length();i++){

                            JSONObject item = jsonArray.getJSONObject(i);
                            masterList.add(new CustomListItem(item.getString("no_transaksi"), item.getString("tgl_penarikan"), item.getString("nama"), item.getString("no_tabungan"), item.getString("jumlah"), GlobalFunction.getStatus(item.getString("status")), item.getString("keterangan")));
                        }
                    }

                    //setAdapter(masterList);
                    parseData(DetailPengajuan.this, masterList, "", "");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {

            }
        });
    }

    private void getPinjamanData(){

        JSONObject jBody = new JSONObject();
        ApiVolley request = new ApiVolley(DetailPengajuan.this, jBody, "GET", WebServiceURL.getPinjamanByNIK + nik, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                JSONObject responseAPI;
                try {
                    responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");
                    masterList = new ArrayList<>();

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray jsonArray = responseAPI.getJSONArray("response");

                        for(int i = 0; i < jsonArray.length();i++){

                            JSONObject item = jsonArray.getJSONObject(i);
                            masterList.add(new CustomListItem(item.getString("no_transaksi"), item.getString("tgl_pengajuan"), item.getString("lama_angsuran"), item.getString("jumlah_angsuran"), item.getString("nilai"), GlobalFunction.getStatus(item.getString("status")), ""));
                        }
                    }

                    //setAdapter(masterList);
                    parseData(DetailPengajuan.this, masterList, "", "");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {

            }
        });
    }

    public static void parseData(final Context context, final List<CustomListItem> listItem, String bulanPosition, String tahunPosition){

        if(listItem != null && listItem.size() > 0){

            // Bulan
            List<String> bulanList = new ArrayList<>();

            bulanList.add("Januari");
            bulanList.add("Februari");
            bulanList.add("Maret");
            bulanList.add("April");
            bulanList.add("Mei");
            bulanList.add("Juni");
            bulanList.add("Juli");
            bulanList.add("Agustus");
            bulanList.add("September");
            bulanList.add("Oktober");
            bulanList.add("November");
            bulanList.add("Desember");
            setBulanAdapter(context, bulanList, bulanPosition);

            // Tahun
            List<String> tahunList = new ArrayList<>();
            for (int x = 0; x < listItem.size(); x++){

                CustomListItem item = listItem.get(x);
                String tanggal = item.getItem2();
                String[] arrayTgl = tanggal.split("-");

                boolean isNew = true;
                for(String tahun: tahunList){

                    if(tahun.equals(arrayTgl[0].trim())) isNew = false;
                }
                if(isNew) tahunList.add(arrayTgl[0].trim());
            }
            setTahunAdapter(context, tahunList, tahunPosition);

        }

        llTampil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(spBulan.getAdapter() != null && spTahun.getAdapter() != null){

                    String selectedBulan = spBulan.getSelectedItem().toString();
                    String selectedTahun = spTahun.getSelectedItem().toString();

                    if(listItem != null && listItem.size() > 0){

                        filterListALL = new ArrayList<CustomListItem>();
                        for(CustomListItem item: listItem){

                            String tanggal = item.getItem2();
                            String[] arrayTgl = tanggal.split("-");
                            String tahun = arrayTgl[0];
                            String bulan = arrayTgl[1];

                            if(kodeDetail.equals(Utils.Pinjaman)){
                                if(selectedTahun.equals(tahun) && Converter.convertBulan(bulan).equals(selectedBulan)){

                                    filterListALL.add(item);
                                }
                            }else{

                                /*if(item.getItem3().toLowerCase().equals("simpanan sukarela")){

                                    if(item.getItem3().toLowerCase().equals(selectedJenisTabungan.toLowerCase()) && selectedTahun.equals(tahun) && Converter.convertBulan(bulan).equals(selectedBulan)){

                                        filterListALL.add(item);
                                    }
                                }else{

                                    try {
                                        selectedNoTabungan = spNoTabungan.getSelectedItem().toString();
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                    if(item.getItem3().toLowerCase().equals(selectedJenisTabungan.toLowerCase()) && item.getItem4().toLowerCase().equals(selectedNoTabungan.toLowerCase()) && selectedTahun.equals(tahun) && Converter.convertBulan(bulan).equals(selectedBulan)){
                                        filterListALL.add(item);
                                    }
                                }*/
                                selectedJenisTabungan = tvJenisTabunganVal.getText().toString();
                                selectedNoTabungan = tvNoTabunganVal.getText().toString();
                                if(item.getItem3().toLowerCase().equals(selectedJenisTabungan.toLowerCase()) && item.getItem4().toLowerCase().equals(selectedNoTabungan.toLowerCase()) && selectedTahun.equals(tahun) && Converter.convertBulan(bulan).equals(selectedBulan)){
                                    filterListALL.add(item);
                                }
                            }

                        }

                       setAdapter(context, filterListALL, selectedBulan, selectedTahun);
                    }else{

                        Snackbar.make(((Activity) context).findViewById(android.R.id.content), "Tidak ada data untuk ditampilkan",
                                Snackbar.LENGTH_SHORT).setAction("OK",
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                    }
                                }).show();
                    }
                }else{

                    Snackbar.make(((Activity) context).findViewById(android.R.id.content), "Tidak ada data untuk ditampilkan",
                            Snackbar.LENGTH_SHORT).setAction("OK",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                }
                            }).show();
                }
            }
        });
    }

    public static void setBulanAdapter(Context context, List<String> listItem, String selectedBulan){

        spBulan.setAdapter(null);

        if(listItem != null && listItem.size() > 0){

            ArrayAdapter adapter = new ArrayAdapter(context, R.layout.normal_spinner, listItem);
            spBulan.setAdapter(adapter);
            spBulan.setSelection(0);
            int x = 0;
            for(String item: listItem){
                if(item.equals(bulanNow) && selectedBulan.length()<=0) spBulan.setSelection(x);
                if(item.equals(selectedBulan)){
                    spBulan.setSelection(x);
                }
                x++;
            }
        }
    }

    private static void setTahunAdapter(Context context, List<String> listItem, String selectedTahun){

        spTahun.setAdapter(null);

        if(listItem != null && listItem.size() > 0){

            ArrayAdapter adapter = new ArrayAdapter(context, R.layout.normal_spinner, listItem);
            spTahun.setAdapter(adapter);
            spTahun.setSelection(0);
            int x = 0;
            for(String item: listItem){
                if(item.equals(tahunNow) && selectedTahun.length()<=0) spTahun.setSelection(x);
                if(item.equals(selectedTahun)){
                    spTahun.setSelection(x);
                }
                x++;
            }
        }
    }

    public static void setAdapter(Context context,List<CustomListItem> listItem, String bulanPosition, String tahunPosition) {

        rvPengajuan.setAdapter(null);

        if(listItem != null && listItem.size()>0){

            if(firstLoad && noTransaksi != null && noTransaksi.length() != 0){

                firstLoad = false;

                filterList = new ArrayList<>();
                for(CustomListItem cli :listItem){

                    String key = cli.getItem1();
                    if(key.toLowerCase().contains(noTransaksi.toLowerCase())){

                        filterList.add(cli);
                    }

                    PengajuanAdapter adapter = new PengajuanAdapter(context, filterList, jenisDetail, bulanPosition, bulanPosition);
                    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context, 1);
                    rvPengajuan.setLayoutManager(mLayoutManager);
                    rvPengajuan.setItemAnimator(new DefaultItemAnimator());
                    rvPengajuan.setAdapter(adapter);
                }
            }else{

                PengajuanAdapter adapter = new PengajuanAdapter(context, listItem, jenisDetail, bulanPosition, tahunPosition);
                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context, 1);
                rvPengajuan.setLayoutManager(mLayoutManager);
                rvPengajuan.setItemAnimator(new DefaultItemAnimator());
                rvPengajuan.setAdapter(adapter);
            }
        }else{

            Snackbar.make(((Activity) context).findViewById(android.R.id.content), "Tidak ada data untuk ditampilkan",
                    Snackbar.LENGTH_SHORT).setAction("OK",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    }).show();
        }
    }
}