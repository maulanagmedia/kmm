package gmedia.net.id.kopkarmitramakmur.MenuBelanjaMandiri;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.kopkarmitramakmur.R;
import gmedia.net.id.kopkarmitramakmur.CustomView.EndlessScroll;
import gmedia.net.id.kopkarmitramakmur.GeneralModel.CustomListItem;
import gmedia.net.id.kopkarmitramakmur.MenuBelanjaMandiri.Adapter.DaftarCartAdapter;
import gmedia.net.id.kopkarmitramakmur.Util.ApiVolley;
import gmedia.net.id.kopkarmitramakmur.Util.ItemValidation;
import gmedia.net.id.kopkarmitramakmur.Util.SessionManager;
import gmedia.net.id.kopkarmitramakmur.Util.WebServiceURL;

public class DetailCart extends AppCompatActivity {

    private static RecyclerView rvListCart;
    public static TextView tvTotal;
    private LinearLayout llRemovaAll;
    private LinearLayout llSave;
    public static List<CustomListItem> cartList, filterList;
    private static ItemValidation iv = new ItemValidation();
    private static Context context;
    private SessionManager session;
    private String nik = "";
    private RadioButton rbCash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_cart);

        initUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Searhbar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
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
                setListAdapter(context, cartList);
                return false;
            }
        });

        return true;
    }

    private void getFilterList(String keyword){

        filterList = new ArrayList<>();
        for(int x = 0; x < cartList.size(); x++){
            CustomListItem item = cartList.get(x);
            if(item.getItem2().toLowerCase().contains(keyword.toLowerCase())){
                filterList.add(item);
            }
        }
        setListAdapter(context , filterList);
    }

    @Override
    public void onBackPressed() {
        Gson gson = new Gson();
        Intent intent = getIntent();
        String selectedItems = gson.toJson(cartList);
        intent.putExtra("cart", selectedItems);
        setResult(DetailBelanjaMandiri.DETAIL_BELANJA,intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }

    private void initUI() {

        rvListCart = (RecyclerView) findViewById(R.id.rv_list_barang);
        tvTotal = (TextView) findViewById(R.id.tv_total);
        rbCash = (RadioButton) findViewById(R.id.rb_cash);
        rbCash.setChecked(true);
        llRemovaAll = (LinearLayout) findViewById(R.id.ll_remove_all);
        llSave = (LinearLayout) findViewById(R.id.ll_save);
        setTitle("Keranjang");
        cartList = new ArrayList<>();
        context = DetailCart.this;

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){
            Type cartListType = new TypeToken<List<CustomListItem>>(){}.getType();
            Gson gson = new Gson();
            String cartListGson = bundle.getString("cart");
            if(cartListGson != null){

                cartList = gson.fromJson(cartListGson, cartListType);
                refreshCartList();
                setListAdapter(context, cartList);
            }
        }

        llRemovaAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Hapus semua item")
                        .setMessage("Anda yakin ingin menghapus semua item yang ada pada keranjang?")
                        .setIcon(R.mipmap.ic_kopkar_logo)
                        .setPositiveButton("Hapus Semua", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cartList = new ArrayList<CustomListItem>();
                                refreshCartList();
                            }
                        })
                        .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }
        });

        llSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                session = new SessionManager(DetailCart.this);
                nik = session.getNik();

                if(cartList.size() > 0){
                    JSONObject jBody = new JSONObject();
                    JSONArray jArray = new JSONArray();

                    try {
                        for (int i = 0; i < cartList.size(); i++){

                            CustomListItem item = cartList.get(i);
                            JSONObject json = new JSONObject();

                            json.put("nik",nik);
                            json.put("id_barang",item.getItem1());
                            json.put("jumlah",item.getItem6());
                            json.put("jenis_pembayaran",(rbCash.isChecked())?"Cash":"Kredit");
                            jArray.put(json);
                        }

                        jBody.put("data", jArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    ApiVolley request = new ApiVolley(DetailCart.this, jBody, "POST", WebServiceURL.insertCartBarang, "", "", 0, new ApiVolley.VolleyCallback() {
                        @Override
                        public void onSuccess(String result) {

                            JSONObject responseAPI;
                            try {
                                responseAPI = new JSONObject(result);
                                String status = responseAPI.getJSONObject("metadata").getString("status");

                                if(iv.parseNullInteger(status) == 200){

                                    cartList = new ArrayList<CustomListItem>();
                                    Gson gson = new Gson();
                                    Intent intent = getIntent();
                                    intent.putExtra("cart", gson.toJson(cartList));
                                    setResult(DetailBelanjaMandiri.DETAIL_BELANJA,intent);
                                    Toast.makeText(context,"Daftar belanja telah tersimpan",Toast.LENGTH_SHORT).show();
                                    finish();
                                }else {

                                    Toast.makeText(DetailCart.this,"Pesanan tidak tersimpan, silahkan coba beberapa saat lagi",Toast.LENGTH_SHORT).show();

                                }
                            } catch (JSONException e) {
                                Toast.makeText(DetailCart.this,"Pesanan tidak tersimpan, silahkan coba beberapa saat lagi",Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(String result) {
                            Toast.makeText(DetailCart.this,"Pesanan tidak tersimpan, silahkan coba beberapa saat lagi",Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    Toast.makeText(DetailCart.this,"Keranjang anda masih kosong",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public static void refreshCartList(){

        int hargaTotal = 0;
        List<CustomListItem> tempCartList = new ArrayList<>(cartList);
        cartList = new ArrayList<>();
        for (int x = 0; x < tempCartList.size(); x++){
            CustomListItem item = tempCartList.get(x);
            if(item != null){
                hargaTotal += (iv.parseNullInteger(item.getItem3()) * iv.parseNullInteger(item.getItem6()));
                cartList.add(item);
            }
        }

        tvTotal.setText(iv.ChangeToRupiahFormat(String.valueOf(hargaTotal)));
        setListAdapter(context, cartList);
    }

    private static void setListAdapter(Context context, List<CustomListItem> listItem){

        rvListCart.setAdapter(null);

        if(listItem != null && listItem.size() > 0){

            DaftarCartAdapter menuAdapter = new DaftarCartAdapter(context, listItem);

            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context, 1);
            rvListCart.setLayoutManager(mLayoutManager);
            rvListCart.setItemAnimator(new DefaultItemAnimator());
            rvListCart.setAdapter(menuAdapter);

            EndlessScroll scroll = new EndlessScroll((GridLayoutManager) mLayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {

                }
            };

            rvListCart.addOnScrollListener(scroll);
        }
    }
}
