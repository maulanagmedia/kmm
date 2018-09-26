package gmedia.net.id.kopkarmitramakmur.MenuJaket;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import gmedia.net.id.kopkarmitramakmur.Util.ApiVolley;
import gmedia.net.id.kopkarmitramakmur.Util.Converter;
import gmedia.net.id.kopkarmitramakmur.Util.ItemValidation;
import gmedia.net.id.kopkarmitramakmur.Util.SessionManager;
import gmedia.net.id.kopkarmitramakmur.Util.WebServiceURL;

public class DetailInfoJaket extends AppCompatActivity {

    private Spinner spBulan, spTahun;
    private TextView tvSaldoAwal, tvSubsidi, tvTabunganWajib, tvSaldoAkhir;
    private SessionManager session;
    private String nik;
    private List<CustomListItem> masterList;
    private ItemValidation iv = new ItemValidation();
    private String bulanNow, tahunNow;
    private TextView tvNama;
    private TextView tvBunga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_info_jaket);

        initUI();
    }

    private void initUI() {

        setTitle("Info Jaket");
        spBulan = (Spinner) findViewById(R.id.sp_bulan);
        spTahun = (Spinner) findViewById(R.id.sp_tahun);
        tvSaldoAwal = (TextView) findViewById(R.id.tv_saldo_awal);
        tvSubsidi = (TextView) findViewById(R.id.tv_subsidi);
        tvTabunganWajib = (TextView) findViewById(R.id.tv_tabungan_wajib);
        tvBunga = (TextView) findViewById(R.id.tv_bunga);
        tvSaldoAkhir = (TextView) findViewById(R.id.tv_saldo_akhir);
        tvNama = (TextView) findViewById(R.id.tv_nama);

        session = new SessionManager(DetailInfoJaket.this);
        nik = session.getNik();
        tvNama.setText(session.getNama()+" ("+ nik+")");

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        bulanNow = Converter.convertBulan(String.valueOf(calendar.get(Calendar.MONTH)+1));
        tahunNow  = String.valueOf(calendar.get(Calendar.YEAR));
        getDetailJaket();
    }

    private void getDetailJaket() {

        JSONObject jBody = new JSONObject();

        ApiVolley request = new ApiVolley(DetailInfoJaket.this, jBody, "GET", WebServiceURL.getJaket + nik, "", "", 0, new ApiVolley.VolleyCallback() {
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
                            masterList.add(new CustomListItem(item.getString("id"), Converter.convertBulan(item.getString("bulan")), item.getString("tahun"), item.getString("saldo_awal"), item.getString("subsidi"), item.getString("tabungan_wajib"), item.getString("saldo_akhir"),item.getString("bunga")));
                        }
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
                boolean isNew = true;
                for(String tahun: tahunList){

                    if(tahun.equals(item.getItem3())) isNew = false;
                }
                if(isNew) tahunList.add(item.getItem3());
            }
            setTahunAdapter(tahunList);

        }

        spBulan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(spBulan.getAdapter() != null && spTahun != null){

                    String selectedBulan = spBulan.getSelectedItem().toString();
                    String selectedTahun = spTahun.getSelectedItem().toString();

                    tvSaldoAwal.setText(iv.ChangeToRupiahFormat("0"));
                    tvSubsidi.setText(iv.ChangeToRupiahFormat("0"));
                    tvTabunganWajib.setText(iv.ChangeToRupiahFormat("0"));
                    tvBunga.setText(iv.ChangeToRupiahFormat("0"));
                    tvSaldoAkhir.setText(iv.ChangeToRupiahFormat("0"));

                    if(listItem != null && listItem.size() > 0){

                        int total = 0;

                        for(CustomListItem item: masterList){

                            if(item.getItem2().toLowerCase().equals(selectedBulan.toLowerCase()) && item.getItem3().equals(selectedTahun)){

                                tvSaldoAwal.setText(iv.ChangeToRupiahFormat(item.getItem4()));
                                tvSubsidi.setText(iv.ChangeToRupiahFormat(item.getItem6()));
                                tvTabunganWajib.setText(iv.ChangeToRupiahFormat(item.getItem7()));
                                tvBunga.setText(iv.ChangeToRupiahFormat(item.getItem9()));
                                tvSaldoAkhir.setText(iv.ChangeToRupiahFormat(item.getItem8()));
                            }
                        }

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

                    tvSaldoAwal.setText(iv.ChangeToRupiahFormat("0"));
                    tvSubsidi.setText(iv.ChangeToRupiahFormat("0"));
                    tvTabunganWajib.setText(iv.ChangeToRupiahFormat("0"));
                    tvBunga.setText(iv.ChangeToRupiahFormat("0"));
                    tvSaldoAkhir.setText(iv.ChangeToRupiahFormat("0"));

                    if(listItem != null && listItem.size() > 0){

                        int total = 0;

                        for(CustomListItem item: masterList){

                            if(item.getItem2().toLowerCase().equals(selectedBulan.toLowerCase()) && item.getItem3().equals(selectedTahun)){

                                tvSaldoAwal.setText(iv.ChangeToRupiahFormat(item.getItem4()));
                                tvSubsidi.setText(iv.ChangeToRupiahFormat(item.getItem6()));
                                tvTabunganWajib.setText(iv.ChangeToRupiahFormat(item.getItem7()));
                                tvBunga.setText(iv.ChangeToRupiahFormat(item.getItem9()));
                                tvSaldoAkhir.setText(iv.ChangeToRupiahFormat(item.getItem8()));
                            }
                        }

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

            ArrayAdapter adapter = new ArrayAdapter(DetailInfoJaket.this, R.layout.normal_spinner, listItem);
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

            ArrayAdapter adapter = new ArrayAdapter(DetailInfoJaket.this, R.layout.normal_spinner, listItem);
            spTahun.setAdapter(adapter);
            spTahun.setSelection(0);
            int x = 0;
            for(String item:listItem){
                if(item.equals(tahunNow)) spTahun.setSelection(x);
                x++;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }
}
