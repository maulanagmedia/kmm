package gmedia.net.id.kopkarmitramakmur;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;

import org.json.JSONException;
import org.json.JSONObject;

import gmedia.net.id.kopkarmitramakmur.NavChangePassword.DetailChangePassword;
import gmedia.net.id.kopkarmitramakmur.NavHome.MainMenu;
import gmedia.net.id.kopkarmitramakmur.NavNews.MainNews;
import gmedia.net.id.kopkarmitramakmur.NavPenarikanSimpanan.MainPenarikanSimpanan;
import gmedia.net.id.kopkarmitramakmur.NavPengajuanPinjaman.MainPengajuanPinjaman;
import gmedia.net.id.kopkarmitramakmur.NavProfile.DetailProfile;
import gmedia.net.id.kopkarmitramakmur.NavProfileKMM.MainProfileKMM;
import gmedia.net.id.kopkarmitramakmur.NavSetoranSimpanan.MainSetoranSimpanan;
import gmedia.net.id.kopkarmitramakmur.Util.ApiVolley;
import gmedia.net.id.kopkarmitramakmur.Util.ItemValidation;
import gmedia.net.id.kopkarmitramakmur.Util.RuntimePermissionsActivity;
import gmedia.net.id.kopkarmitramakmur.Util.SessionManager;
import gmedia.net.id.kopkarmitramakmur.Util.WebServiceURL;

public class MainActivity extends RuntimePermissionsActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private boolean doubleBackToExitPressedOnce = false;
    private TextView tvUsername;
    private TextView tvKeterangan;
    private SessionManager session;
    private Fragment fragment;
    private static final int REQUEST_PERMISSIONS = 20;
    private static final String TAG = "Main";
    private ItemValidation iv = new ItemValidation();
    private String version, latestVersion, link;
    private boolean updateRequired = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if( getIntent().getBooleanExtra("Exit me", false)){
            finish();
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            return;
        }

        checkVersion();

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        // for android > M
        if (ContextCompat.checkSelfPermission(
                MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                MainActivity.this, android.Manifest.permission.WAKE_LOCK) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            MainActivity.super.requestAppPermissions(new
                            String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.WAKE_LOCK, android.Manifest.permission.READ_EXTERNAL_STORAGE}, R.string
                            .runtime_permissions_txt
                    , REQUEST_PERMISSIONS);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        tvUsername = (TextView) headerView.findViewById(R.id.tv_username);
        tvKeterangan = (TextView) headerView.findViewById(R.id.tv_keterangan);
        session = new SessionManager(MainActivity.this);
        //Logout
        if(!session.isLoggedIn()){
            session.logoutUser();
        }
        tvUsername.setText(session.getNama());
        tvKeterangan.setText(session.getDepartement());

        Bundle bundle = getIntent().getExtras();

        /*if(bundle != null){
            try {

                String state = bundle.getString("state");
                if(state.equals("2")){

                    navigationView.setCheckedItem(R.id.nav_news);
                    FrameLayout flContainer = (FrameLayout) findViewById(R.id.fl_main_container);
                    flContainer.removeAllViews();
                    setTitle("News");
                    fragment = new MainNews();
                    callFragment(fragment);
                }
            }catch (Exception e){
                e.printStackTrace();

            }
        }*/

        if(savedInstanceState == null){
            navigationView.setCheckedItem(R.id.nav_home);
            FrameLayout flContainer = (FrameLayout) findViewById(R.id.fl_main_container);
            flContainer.removeAllViews();
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
            MainMenu contentMain = new MainMenu();
            View childLayoutMain = inflater.inflate(R.layout.menu_main, flContainer);
            contentMain.setView(MainActivity.this, childLayoutMain);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void checkVersion(){

        PackageInfo pInfo = null;
        version = "";

        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        version = pInfo.versionName;
        latestVersion = "";
        link = "";

        ApiVolley request = new ApiVolley(MainActivity.this, new JSONObject(), "GET", WebServiceURL.getLatestVersion, "", "", 0, new ApiVolley.VolleyCallback() {

            @Override
            public void onSuccess(String result) {

                JSONObject responseAPI;
                try {
                    responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");

                    if(iv.parseNullInteger(status) == 200){
                        latestVersion = responseAPI.getJSONObject("response").getString("versi");
                        link = responseAPI.getJSONObject("response").getString("link");
                        updateRequired = (iv.parseNullInteger(responseAPI.getJSONObject("response").getString("wajib")) == 1) ? true : false;

                        if(!version.trim().equals(latestVersion.trim()) && link.length() > 0){

                            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            if(updateRequired){

                                builder.setIcon(R.mipmap.ic_kopkar_logo)
                                        .setTitle("Update")
                                        .setMessage("Versi terbaru "+latestVersion+" telah tersedia, mohon download versi terbaru.")
                                        .setPositiveButton("Update Sekarang", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                                                startActivity(browserIntent);
                                            }
                                        })
                                        .setCancelable(false)
                                        .show();
                            }else{

                                builder.setIcon(R.mipmap.ic_kopkar_logo)
                                        .setTitle("Update")
                                        .setMessage("Versi terbaru "+latestVersion+" telah tersedia, mohon download versi terbaru.")
                                        .setPositiveButton("Update Sekarang", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                                                startActivity(browserIntent);
                                            }
                                        })
                                        .setNegativeButton("Update Nanti", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).show();
                            }
                        }
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

    @Override
    public void onPermissionsGranted(int requestCode) {
        Log.d(TAG, "onPermissionsGranted: "+requestCode);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                /*finish();
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                System.exit(0);*/
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("Exit me", true);
                startActivity(intent);
                finish();
            }else{
                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, "Tekan lagi untuk keluar", Toast.LENGTH_SHORT).show();

                // take 2 second before the doubleBackToExitPressedOnce become false again
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            }
        }
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FrameLayout flContainer = (FrameLayout) findViewById(R.id.fl_main_container);
        flContainer.removeAllViews();
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        setTitle(item.getTitle());

        if(id == R.id.nav_home){

            MainMenu contentMain = new MainMenu();
            View childLayoutMain = inflater.inflate(R.layout.menu_main, flContainer);
            contentMain.setView(MainActivity.this, childLayoutMain);
        }else if(id == R.id.nav_profile_kmm){

            fragment = new MainProfileKMM();
            callFragment(fragment);
        }else if(id == R.id.nav_news){

            fragment = new MainNews();
            callFragment(fragment);
        }else if(id == R.id.nav_setoran){

            fragment = new MainSetoranSimpanan();
            callFragment(fragment);
        }else if(id == R.id.nav_penarikan){

            fragment = new MainPenarikanSimpanan();
            callFragment(fragment);
        }else if(id == R.id.nav_pinjaman){

            fragment = new MainPengajuanPinjaman();
            callFragment(fragment);
        }else if (id == R.id.nav_profile) {

            fragment = new DetailProfile();
            callFragment(fragment);
        }else if (id == R.id.nav_ubah_password) {

            fragment = new DetailChangePassword();
            callFragment(fragment);
        } else if (id == R.id.nav_logout) {

            session.logoutUser();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void callFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_main_container, fragment, fragment.getClass().getSimpleName()).setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).addToBackStack(null).commit();
    }
}
