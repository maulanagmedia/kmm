package gmedia.net.id.kopkarmitramakmur.NavHome;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import gmedia.net.id.kopkarmitramakmur.R;
import gmedia.net.id.kopkarmitramakmur.CustomView.WrapContentViewPager;
import gmedia.net.id.kopkarmitramakmur.MenuBelanjaMandiri.DetailBelanjaMandiri;
import gmedia.net.id.kopkarmitramakmur.MenuInfoSimpanan.DetailInfoSimpanan;
import gmedia.net.id.kopkarmitramakmur.MenuInfoPinjaman.SubDetailPinjaman;
import gmedia.net.id.kopkarmitramakmur.MenuInfoPotongan.DetailInfoPotongan;
import gmedia.net.id.kopkarmitramakmur.MenuInformasiTabungan.DetailInfoTabungan;
import gmedia.net.id.kopkarmitramakmur.MenuJaket.DetailInfoJaket;
import gmedia.net.id.kopkarmitramakmur.NavHome.Adapter.MenuHeaderSliderAdapter;
import gmedia.net.id.kopkarmitramakmur.NavHome.Model.HeaderSliderModel;
import gmedia.net.id.kopkarmitramakmur.NavNews.NewsActivity;
import gmedia.net.id.kopkarmitramakmur.PengajuanHandler;
import gmedia.net.id.kopkarmitramakmur.Util.ApiVolley;
import gmedia.net.id.kopkarmitramakmur.Util.GlobalFunction;
import gmedia.net.id.kopkarmitramakmur.Util.ItemValidation;
import gmedia.net.id.kopkarmitramakmur.Util.SessionManager;
import gmedia.net.id.kopkarmitramakmur.Util.Utils;
import gmedia.net.id.kopkarmitramakmur.Util.WebServiceURL;

/**
 * Created by Shin on 2/11/2017.
 */

public class MainMenu extends Fragment implements ViewPager.OnPageChangeListener {

    private Context context;
    private View layout;
    private Animation anim, animSub;
    private LinearLayout llInfoProsedur;
    private LinearLayout llInfoProsedur1, llInfoProsedur2;
    private boolean isLoading = false; // for waiting if animation is on progress
    private GlobalFunction gf = new GlobalFunction();
    private LinearLayout llInfoAnggota;
    private LinearLayout llInfoPinjaman;
    private LinearLayout llInfoBelanja;
    private LinearLayout llIkutSurvey;
    private LinearLayout llSetoran;
    private LinearLayout llPenarikan;
    private LinearLayout llInfoTabungan;
    private LinearLayout llPengajuanPinjaman;
    private LinearLayout llInfoJaket;
    private LinearLayout llBelanjaMandiri;
    private int defaultMenuHeight = 0;
    private LinearLayout llLine1, llLine2, llLine3, llLine4, llLine6, llLine7;

    //Header slide
    private MenuHeaderSliderAdapter sliderHeaderAdapter;
    private int dotsCount;
    private ImageView[] dots;
    private Timer timer;
    private int changeHeaderTimes = 5;
    private WrapContentViewPager vpHeaderSlider;
    private LinearLayout llPagerIndicator;
    private List<HeaderSliderModel> masterHeaderList;
    private boolean firstLoad = true;
    private ItemValidation iv = new ItemValidation();
    private LinearLayout llSlider;
    private SessionManager session;
    private LinearLayout llNews;

    public MainMenu(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        layout =  inflater.inflate(R.layout.menu_main,container,false);
        context = getActivity();
        initUI();
        return layout;
    }
    public void setView(Context context, View layout){
        this.context = context;
        this.layout = layout;
        initUI();
    }

    private void initUI() {

        llInfoAnggota = (LinearLayout) layout.findViewById(R.id.ll_info_anggota);
        llInfoPinjaman = (LinearLayout) layout.findViewById(R.id.ll_simulasi_pinjaman);
        llInfoProsedur = (LinearLayout) layout.findViewById(R.id.ll_info_prosedur);
        llInfoProsedur1 = (LinearLayout) layout.findViewById(R.id.ll_info_prosedur_1);
        llInfoBelanja = (LinearLayout) layout.findViewById(R.id.ll_info_belanja);
        llIkutSurvey = (LinearLayout) layout.findViewById(R.id.ll_ikut_survey);
        llSetoran = (LinearLayout) layout.findViewById(R.id.ll_setoran);
        llPenarikan = (LinearLayout) layout.findViewById(R.id.ll_penarikan);
        llInfoTabungan = (LinearLayout) layout.findViewById(R.id.ll_info_tabungan);
        llPengajuanPinjaman = (LinearLayout) layout.findViewById(R.id.ll_pengajuan_pinjaman);
        llInfoJaket = (LinearLayout) layout.findViewById(R.id.ll_info_jaket);
        llBelanjaMandiri = (LinearLayout) layout.findViewById(R.id.ll_belanja_mandiri);
        llNews = (LinearLayout) layout.findViewById(R.id.ll_news);

        // Slider
        llSlider = (LinearLayout) layout.findViewById(R.id.ll_slider_container);
        llPagerIndicator = (LinearLayout) layout.findViewById(R.id.ll_view_pager_dot_count);
        vpHeaderSlider = (WrapContentViewPager) layout.findViewById(R.id.vp_image_slider);
        vpHeaderSlider.setScrollDurationFactor(4);

        session = new SessionManager(context);

        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        try {
            display.getRealSize(size);
        } catch (NoSuchMethodError err) {
            display.getSize(size);
        }
        defaultMenuHeight = size.x/2;

        llLine1 = (LinearLayout) layout.findViewById(R.id.ll_line_1);
        llLine2 = (LinearLayout) layout.findViewById(R.id.ll_line_2);
        llLine3 = (LinearLayout) layout.findViewById(R.id.ll_line_3);
        llLine4 = (LinearLayout) layout.findViewById(R.id.ll_line_4);
        llLine6 = (LinearLayout) layout.findViewById(R.id.ll_line_6);
        llLine7 = (LinearLayout) layout.findViewById(R.id.ll_line_7);

        int padding = 2 * gf.dpToPx(context, context.getResources().getInteger(R.integer.horizontal_margin));
        syncronizeLayoutHeight(llLine1, (size.x - padding), true);
        syncronizeLayoutHeight(llLine2, (size.x - padding), true);
        syncronizeLayoutHeight(llLine3, (size.x - padding), true);
        syncronizeLayoutHeight(llLine4, (size.x - padding), true);
        syncronizeLayoutHeight(llLine6, (size.x - padding), false);
        syncronizeLayoutHeight(llLine7, (size.x - padding), false);

        initEvent();
        getListHeaderSlider();
    }

    private void syncronizeLayoutHeight(final LinearLayout container, int width, boolean paddingBottom){

        double x = 0.7 * defaultMenuHeight;
        int llHeight = (int) x;
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, llHeight);
        int paddingTop = gf.dpToPx(context, (int)context.getResources().getDimension(R.dimen.padding_top_menu_p));
        if(paddingBottom) lp.setMargins(0, 0,0, paddingTop);
        container.setLayoutParams(lp);
        container.setWeightSum(2);
    }

    private void initEvent(){

        llInfoAnggota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, DetailInfoSimpanan.class);
                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

                /*Intent intent = new Intent(context, SubDetailInfoAnggota.class);
                intent.putExtra("title", "Info Simpanan");
                intent.putExtra("subtitle", session.getNama()+" ("+session.getNik()+")");
                context.startActivity(intent);*/
            }
        });

        llInfoPinjaman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, SubDetailPinjaman.class);
                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            }
        });

        llInfoProsedur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*if(!isLoading){
                    anim = AnimationUtils.loadAnimation(context, R.anim.menu_anim_subitem);
                    animSub = AnimationUtils.loadAnimation(context, R.anim.menu_anim);
                    llInfoProsedur1.startAnimation(anim);
                    llInfoProsedur2.startAnimation(animSub);
                    llInfoProsedur2.postOnAnimationDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(context, InfoProsedure.class);
                            ((Activity) context).overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                            context.startActivity(intent);
                            isLoading = false;
                        }
                    },getActivity().getResources().getInteger(R.integer.menu_p_animation_duration));

                    isLoading = true;
                }*/

                Intent intent = new Intent(context, DetailInfoPotongan.class);
                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            }
        });

        llInfoTabungan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, DetailInfoTabungan.class);
                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            }
        });

        llBelanjaMandiri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, DetailBelanjaMandiri.class);
                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            }
        });

        llInfoJaket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, DetailInfoJaket.class);
                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            }
        });

        llSetoran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PengajuanHandler.class);
                intent.putExtra("kode", Utils.Setoran);
                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            }
        });

        llPenarikan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PengajuanHandler.class);
                intent.putExtra("kode", Utils.Penarikan);
                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            }
        });

        llPengajuanPinjaman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PengajuanHandler.class);
                intent.putExtra("kode", Utils.Pinjaman);
                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            }
        });

        llNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NewsActivity.class);
                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        isLoading = false;
    }

    private void getListHeaderSlider() {

        masterHeaderList = new ArrayList<>();

        ApiVolley request = new ApiVolley(context, new JSONObject(), "GET", WebServiceURL.getNewsActive, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                JSONObject responseAPI;
                try {
                    responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray jsonArray = responseAPI.getJSONArray("response");

                        for(int i = 0; i < jsonArray.length();i++){

                            JSONObject item = jsonArray.getJSONObject(i);
                            masterHeaderList.add(new HeaderSliderModel(item.getString("id"),item.getString("gambar"),item.getString("judul"),item.getString("keterangan")));
                        }

                        setHeaderSlider();
                        setUiPageViewController();
                        llSlider.setVisibility(View.VISIBLE);
                        if(firstLoad){
                            setViewPagerTimer(changeHeaderTimes);
                            firstLoad = false;
                        }
                    }else {
                        vpHeaderSlider.setAdapter(null);
                        llSlider.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    vpHeaderSlider.setAdapter(null);
                    llSlider.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(String result) {
                vpHeaderSlider.setAdapter(null);
                llSlider.setVisibility(View.GONE);
            }
        });
    }

    private void setHeaderSlider(){

        vpHeaderSlider.setAdapter(null);
        sliderHeaderAdapter = null;
        sliderHeaderAdapter = new MenuHeaderSliderAdapter(context, masterHeaderList, defaultMenuHeight);
        vpHeaderSlider.setAdapter(sliderHeaderAdapter);
        vpHeaderSlider.setCurrentItem(0);
        vpHeaderSlider.setOnPageChangeListener(this);
    }

    private void setUiPageViewController() {

        dotsCount = sliderHeaderAdapter.getCount();
        dots = new ImageView[dotsCount];
        llPagerIndicator.removeAllViews();

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(context);
            dots[i].setImageDrawable(context.getResources().getDrawable(R.drawable.dot_unselected_item));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(4, 0, 4, 0);

            llPagerIndicator.addView(dots[i], params);
        }

        dots[0].setImageDrawable(context.getResources().getDrawable(R.drawable.dot_selected_item));
    }

    private void setViewPagerTimer(int seconds){
        timer = new Timer(); // At this line a new Thread will be created
        timer.scheduleAtFixedRate(new RemindTask(), 0, seconds * 1000);
    }

    class RemindTask extends TimerTask {

        @Override
        public void run() {

            // As the TimerTask run on a seprate thread from UI thread we have
            // to call runOnUiThread to do work on UI thread.
            ((AppCompatActivity)context).runOnUiThread(new Runnable() {
                public void run() {

                    if(vpHeaderSlider.getCurrentItem() == sliderHeaderAdapter.getCount() - 1){
                        vpHeaderSlider.setCurrentItem(0);

                    }else{
                        vpHeaderSlider.setCurrentItem(vpHeaderSlider.getCurrentItem() + 1);
                    }
                }
            });

        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        for (int i = 0; i < dotsCount; i++) {
            dots[i].setImageDrawable(context.getResources().getDrawable(R.drawable.dot_unselected_item));
        }

        dots[position].setImageDrawable(context.getResources().getDrawable(R.drawable.dot_selected_item));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
