package gmedia.net.id.kopkarmitramakmur.MenuInformasiTabungan;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import gmedia.net.id.kopkarmitramakmur.GeneralModel.CustomListItem;
import gmedia.net.id.kopkarmitramakmur.MenuInformasiTabungan.Adapter.DetailInfoTabunganAdapter;
import gmedia.net.id.kopkarmitramakmur.Util.ApiVolley;
import gmedia.net.id.kopkarmitramakmur.Util.Converter;
import gmedia.net.id.kopkarmitramakmur.Util.ItemValidation;
import gmedia.net.id.kopkarmitramakmur.Util.SessionManager;
import gmedia.net.id.kopkarmitramakmur.Util.WebServiceURL;

public class SubDetailInfoTabungan extends AppCompatActivity {

    private TextView tvSubtitle;
    private RecyclerView rvListInfo;
    private List<CustomListItem> masterList, filterList;
    private String noTabungan;
    private ItemValidation iv = new ItemValidation();
    private String nik, kodeTabungan;
    private SessionManager session;
    private TextView tvSaldoAkhir;
    private Spinner spBulan, spTahun;
    private String bulanNow, tahunNow;
    private String saldoAkhir = "0";
    private TextView tvTotalDebit, tvTotalKredit;
    private int totalDebit = 0, totalKredit = 0;
    private TextView tvNoTabungan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_detail_info_tabungan);
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

    private void initUI() {

        spBulan = (Spinner) findViewById(R.id.sp_bulan);
        spTahun = (Spinner) findViewById(R.id.sp_tahun);
        tvSubtitle = (TextView) findViewById(R.id.tv_subtitle);
        tvNoTabungan = (TextView) findViewById(R.id.tv_no_tabungan);
        rvListInfo = (RecyclerView) findViewById(R.id.rv_detail_info);
        tvSaldoAkhir = (TextView) findViewById(R.id.tv_saldo_akhir);
        tvTotalDebit = (TextView) findViewById(R.id.tv_total_debit);
        tvTotalKredit = (TextView) findViewById(R.id.tv_total_kredit);

        setTitle("Informasi Tabungan");
        session = new SessionManager(SubDetailInfoTabungan.this);
        nik = session.getNik();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            noTabungan = bundle.getString("no_tabungan");
            kodeTabungan = (bundle.getString("kode_tabungan") != null) ? bundle.getString("kode_tabungan") : "";
            String subTitle = bundle.getString("keterangan");
            Boolean potong_gaji = (Boolean) bundle.get("potong_gaji");
            String titleText = "";

            if(noTabungan.length() > 0){
                titleText = noTabungan+ " ("+kodeTabungan+")";
            }else{
                tvNoTabungan.setText("Jenis Tabungan");
                titleText = kodeTabungan;
            }
            tvSubtitle.setText(titleText);
            setTitle(session.getNama());
        }

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        bulanNow = Converter.convertBulan(String.valueOf(calendar.get(Calendar.MONTH)+1));
        tahunNow  = String.valueOf(calendar.get(Calendar.YEAR));

        getDataList();
    }


    @Override
    protected void onResume() {
        super.onResume();
        getDataList();
    }

    private void getDataList() {

        JSONObject jBody = new JSONObject();

        try {
            jBody.put("nik", nik);
            jBody.put("no_tabungan", noTabungan);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(SubDetailInfoTabungan.this, jBody, "POST", WebServiceURL.getTransaksi, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                JSONObject responseAPI;
                try {
                    responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");
                    masterList = new ArrayList<>();

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray jsonArray = responseAPI.getJSONArray("response");
                        //masterList.add(new CustomListItem("", "", "", "", "", "", "header"));

                        for(int i = 0; i < jsonArray.length();i++){

                            JSONObject item = jsonArray.getJSONObject(i);

                            // Jika Simpanan Sukarela
                            saldoAkhir = "0";
                            if(kodeTabungan.length() > 0 && noTabungan.length() <= 0){

                               if(Converter.convertJenisTabungan(kodeTabungan)== Converter.convertJenisTabungan("simpanan sukarela")){
                                   masterList.add(new CustomListItem(item.getString("tgl_transaksi"), "", item.getString("trans_masuk"), item.getString("trans_keluar"), item.getString("saldo"),item.getString("keterangan"), "item"));
                               }

                            }else{ // Tabungan
                                masterList.add(new CustomListItem(item.getString("tgl_transaksi"), "", item.getString("trans_masuk"), item.getString("trans_keluar"), item.getString("saldo"),item.getString("keterangan"), "item"));
                            }
                            saldoAkhir = item.getString("saldo");

                        }
                        tvSaldoAkhir.setText(iv.ChangeToRupiahFormat(saldoAkhir));
                    }

                    parseData(masterList);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {

            }
        });
    }

    private void parseData(final List<CustomListItem> listItem){

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
            setBulanAdapter(bulanList);

            // Tahun
            List<String> tahunList = new ArrayList<>();
            for (int x = 0; x < listItem.size(); x++){

                CustomListItem item = listItem.get(x);
                if(!item.getItem8().equals("header")){
                    String tanggal = item.getItem1();
                    String[] arrayTgl = tanggal.split("-");

                    boolean isNew = true;
                    for(String tahun: tahunList){

                        if(tahun.equals(arrayTgl[0].trim())) isNew = false;
                    }
                    if(isNew) tahunList.add(arrayTgl[0].trim());
                }
            }
            setTahunAdapter(tahunList);

        }

        spBulan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(spBulan.getAdapter() != null && spTahun != null){

                    String selectedBulan = spBulan.getSelectedItem().toString();
                    String selectedTahun = spTahun.getSelectedItem().toString();

                    tvTotalDebit.setText(iv.ChangeToRupiahFormat("0"));
                    tvTotalKredit.setText(iv.ChangeToRupiahFormat("0"));
                    totalDebit = 0;
                    totalKredit = 0;
                    //tvSaldoAkhir.setText(iv.ChangeToRupiahFormat("0"));

                    if(listItem != null && listItem.size() > 0){

                        filterList = new ArrayList<CustomListItem>();
                        String tanggalBuf = "";
                        for(CustomListItem item: listItem){

                            if(!item.getItem8().equals("header")){
                                String tanggal = item.getItem1();
                                String[] arrayTgl = tanggal.split("-");
                                String tahun = arrayTgl[0];
                                String bulan = arrayTgl[1];

                                if(selectedTahun.equals(tahun) && Converter.convertBulan(bulan).equals(selectedBulan)){

                                    if(!tanggalBuf.equals(item.getItem1())){
                                        filterList.add(new CustomListItem(item.getItem1(), "", "", "", "", "", "date"));
                                        tanggalBuf = item.getItem1();
                                    }

                                    filterList.add(item);
                                    if(iv.parseNullInteger(item.getItem3()) > 0){

                                        totalKredit += iv.parseNullInteger(item.getItem3());
                                    }else{

                                        totalDebit += iv.parseNullInteger(item.getItem4());
                                    }

                                }
                            }
                        }
                        tvTotalKredit.setText(iv.ChangeToRupiahFormat(String.valueOf(totalKredit)));
                        tvTotalDebit.setText(iv.ChangeToRupiahFormat(String.valueOf(totalDebit)));
                        setListAdapter(filterList);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spTahun.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(spBulan.getAdapter() != null && spTahun != null){

                    String selectedBulan = spBulan.getSelectedItem().toString();
                    String selectedTahun = spTahun.getSelectedItem().toString();

                    tvTotalDebit.setText(iv.ChangeToRupiahFormat("0"));
                    tvTotalKredit.setText(iv.ChangeToRupiahFormat("0"));
                    totalDebit = 0;
                    totalKredit = 0;
                    //tvSaldoAkhir.setText(iv.ChangeToRupiahFormat("0"));

                    if(listItem != null && listItem.size() > 0){

                        String tanggalBuf = "";
                        filterList = new ArrayList<CustomListItem>();
                        for(CustomListItem item: listItem){

                            if(!item.getItem8().equals("header")){
                                String tanggal = item.getItem1();
                                String[] arrayTgl = tanggal.split("-");
                                String tahun = arrayTgl[0];
                                String bulan = arrayTgl[1];

                                if(selectedTahun.equals(tahun) && Converter.convertBulan(bulan).equals(selectedBulan)){

                                    if(!tanggalBuf.equals(item.getItem1())){
                                        filterList.add(new CustomListItem(item.getItem1(), "", "", "", "", "", "date"));
                                        tanggalBuf = item.getItem1();
                                    }

                                    filterList.add(item);
                                    if(iv.parseNullInteger(item.getItem3()) > 0){

                                        totalKredit += iv.parseNullInteger(item.getItem3());
                                    }else{

                                        totalDebit += iv.parseNullInteger(item.getItem4());
                                    }
                                }
                            }
                        }
                        tvTotalKredit.setText(iv.ChangeToRupiahFormat(String.valueOf(totalKredit)));
                        tvTotalDebit.setText(iv.ChangeToRupiahFormat(String.valueOf(totalDebit)));
                        setListAdapter(filterList);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void setBulanAdapter(List<String> listItem){

        spBulan.setAdapter(null);

        if(listItem != null && listItem.size() > 0){

            ArrayAdapter adapter = new ArrayAdapter(SubDetailInfoTabungan.this, R.layout.normal_spinner, listItem);
            spBulan.setAdapter(adapter);
            spBulan.setSelection(0);
            int x = 0;
            for(String item:listItem){
                if(item.equals(bulanNow)) spBulan.setSelection(x);
                x++;
            }
        }
    }

    private void setTahunAdapter(List<String> listItem){

        spTahun.setAdapter(null);

        if(listItem != null && listItem.size() > 0){

            ArrayAdapter adapter = new ArrayAdapter(SubDetailInfoTabungan.this, R.layout.normal_spinner, listItem);
            spTahun.setAdapter(adapter);
            spTahun.setSelection(0);
            int x = 0;
            for(String item:listItem){
                if(item.equals(tahunNow)) spTahun.setSelection(x);
                x++;
            }
        }
    }

    private void setListAdapter(List<CustomListItem> listItem){

        rvListInfo.setAdapter(null);
        //tvSaldoAkhir.setText(iv.ChangeToRupiahFormat("0"));

        if(listItem != null && listItem.size() > 0){

            CustomListItem itemAwal = listItem.get(1);
            CustomListItem itemAkhir = listItem.get(listItem.size()-1);
            //listItem.add(0,new CustomListItem("", "", "", "", "", "", "header"));
            DetailInfoTabunganAdapter menuAdapter = new DetailInfoTabunganAdapter(SubDetailInfoTabungan.this, listItem);

            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(SubDetailInfoTabungan.this, 1);
            rvListInfo.setLayoutManager(mLayoutManager);
            rvListInfo.setItemAnimator(new DefaultItemAnimator());
            rvListInfo.setAdapter(menuAdapter);

            //tvSaldoAkhir.setText(iv.ChangeToRupiahFormat(itemAkhir.getItem6()));
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
    }
}
