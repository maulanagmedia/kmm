package gmedia.net.id.kopkarmitramakmur;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import gmedia.net.id.kopkarmitramakmur.NavPenarikanSimpanan.MainPenarikanSimpanan;
import gmedia.net.id.kopkarmitramakmur.NavPengajuanPinjaman.MainPengajuanPinjaman;
import gmedia.net.id.kopkarmitramakmur.NavSetoranSimpanan.MainSetoranSimpanan;
import gmedia.net.id.kopkarmitramakmur.Util.Utils;

/**
 * Created by Shin on 4/27/2017.
 */

public class PengajuanHandler extends AppCompatActivity {

    private String kode = "";
    private Fragment fragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengajuan);

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){

            kode = bundle.getString("kode");

            switch (kode){
                case Utils.Setoran:
                    fragment = new MainSetoranSimpanan();
                    setTitle("Setoran Simpanan");
                    callFragment(fragment);
                    break;
                case Utils.Penarikan:
                    fragment = new MainPenarikanSimpanan();
                    setTitle("Penarikan Simpanan");
                    callFragment(fragment);
                    break;
                case Utils.Pinjaman:
                    fragment = new MainPengajuanPinjaman();
                    setTitle("Pengajuan Pinjaman");
                    callFragment(fragment);
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void callFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_main_container, fragment, fragment.getClass().getSimpleName()).setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).addToBackStack(null).commit();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
