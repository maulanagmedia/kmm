package gmedia.net.id.kopkarmitramakmur.ActivityDetailPengajuan.Adapter;

/**
 * Created by Shin on 3/21/2017.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import gmedia.net.id.kopkarmitramakmur.R;
import gmedia.net.id.kopkarmitramakmur.ActivityDetailPengajuan.DetailPengajuan;
import gmedia.net.id.kopkarmitramakmur.ActivityDetailPengajuan.DetailStatusPengajuan;
import gmedia.net.id.kopkarmitramakmur.GeneralModel.CustomListItem;
import gmedia.net.id.kopkarmitramakmur.Util.ApiVolley;
import gmedia.net.id.kopkarmitramakmur.Util.GlobalFunction;
import gmedia.net.id.kopkarmitramakmur.Util.ItemValidation;
import gmedia.net.id.kopkarmitramakmur.Util.SessionManager;
import gmedia.net.id.kopkarmitramakmur.Util.Utils;
import gmedia.net.id.kopkarmitramakmur.Util.WebServiceURL;

public class PengajuanAdapter extends RecyclerView.Adapter<PengajuanAdapter.MyViewHolder> {

    private Context context;
    private List<CustomListItem> masterList;
    private ItemValidation iv = new ItemValidation();
    private String tipePengajuan;
    private SessionManager session;
    private String bulanPosition, tahunPosition;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout llJenisTabungan, llNoTabungan, llDetailStatusContainer , llKomentarContainer, llDetailStatus, llDetailHapus, llHapus, llAngsuran, llMenuContainer;
        public TextView tvNoTransaksi, tvTanggal, tvJenisTabungan, tvNoTabungan, tvJumlah, tvStatus, tvLamaAngsuran, tvJumlahAngsuran, tvKeterangan;

        public MyViewHolder(View view) {
            super(view);
            llJenisTabungan = (LinearLayout) view.findViewById(R.id.ll_jenis_tabungan);
            llNoTabungan = (LinearLayout) view.findViewById(R.id.ll_no_tabungan);
            llDetailStatusContainer = (LinearLayout) view.findViewById(R.id.ll_detail_status_container);
            llAngsuran = (LinearLayout) view.findViewById(R.id.ll_angsuran_container);
            llKomentarContainer = (LinearLayout) view.findViewById(R.id.ll_komentar_container);
            llDetailStatus = (LinearLayout) view.findViewById(R.id.ll_detail_status);
            llMenuContainer = (LinearLayout) view.findViewById(R.id.ll_menu_container);
            llDetailHapus = (LinearLayout) view.findViewById(R.id.ll_detail_hapus);
            llHapus = (LinearLayout) view.findViewById(R.id.ll_hapus);
            //tvNoTransaksi = (TextView) view.findViewById(R.id.tv_no_transaksi);
            tvTanggal = (TextView) view.findViewById(R.id.tv_tanggal);
            tvJenisTabungan = (TextView) view.findViewById(R.id.tv_jenis_tabungan);
            tvNoTabungan = (TextView) view.findViewById(R.id.tv_no_tabungan);
            tvJumlah = (TextView) view.findViewById(R.id.tv_jumlah);
            tvStatus = (TextView) view.findViewById(R.id.tv_status);
            tvLamaAngsuran = (TextView) view.findViewById(R.id.tv_lama_angsuran);
            tvJumlahAngsuran = (TextView) view.findViewById(R.id.tv_jumlah_angsuran);
            tvKeterangan = (TextView) view.findViewById(R.id.tv_keterangan);
        }
    }

    public PengajuanAdapter(Context context, List masterList, String tipePengajuan, String bulanPosition, String tahunPosition){
        this.context = context;
        this.masterList = masterList;
        this.tipePengajuan = tipePengajuan;
        this.bulanPosition = bulanPosition;
        this.tahunPosition = tahunPosition;
    }

    @Override
    public PengajuanAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cv_detail_setoran_penarikan, parent, false);

        return new PengajuanAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        final CustomListItem cli = masterList.get(position);
        String dateFormat = context.getResources().getString(R.string.format_date);
        String dateFormatDisplay = context.getResources().getString(R.string.format_date_display);

        String deleteURL = "";

        if (tipePengajuan.equals(Utils.Setoran)){

            //holder.tvNoTransaksi.setText(cli.getItem1());
            holder.tvTanggal.setText(iv.ChangeFormatDateString(cli.getItem2(), dateFormat, dateFormatDisplay));
            holder.tvJenisTabungan.setText(cli.getItem3());
            holder.tvNoTabungan.setText(cli.getItem4());
            holder.tvJumlah.setText(iv.ChangeToCurrencyFormat(cli.getItem6()));
            holder.tvStatus.setText(Html.fromHtml(cli.getItem7()));
            holder.tvKeterangan.setText(cli.getItem8());
            holder.llDetailStatusContainer.setVisibility(View.GONE);
            holder.llAngsuran.setVisibility(View.GONE);
            deleteURL = WebServiceURL.deleteSetoran;

            if(cli.getItem7().equals(GlobalFunction.getStatus("3"))){
                holder.llKomentarContainer.setVisibility(View.VISIBLE);
            }else{
                holder.llKomentarContainer.setVisibility(View.GONE);
            }

            if(cli.getItem7().equals(GlobalFunction.getStatus("0"))){
                holder.llMenuContainer.setVisibility(View.VISIBLE);
            }else{
                holder.llMenuContainer.setVisibility(View.GONE);
            }
        }else if(tipePengajuan.equals(Utils.Penarikan)){

            //holder.tvNoTransaksi.setText(cli.getItem1());
            holder.tvTanggal.setText(iv.ChangeFormatDateString(cli.getItem2(), dateFormat, dateFormatDisplay));
            holder.tvJenisTabungan.setText(cli.getItem3());
            holder.tvNoTabungan.setText(cli.getItem4());
            holder.tvJumlah.setText(iv.ChangeToCurrencyFormat(cli.getItem6()));
            holder.tvStatus.setText(Html.fromHtml(cli.getItem7()));
            holder.tvKeterangan.setText(cli.getItem8());
            holder.llDetailStatusContainer.setVisibility(View.GONE);
            holder.llAngsuran.setVisibility(View.GONE);
            deleteURL = WebServiceURL.deletePenarikan;

            if(cli.getItem7().equals(GlobalFunction.getStatus("3"))){
                holder.llKomentarContainer.setVisibility(View.VISIBLE);
            }else{
                holder.llKomentarContainer.setVisibility(View.GONE);
            }

            if(cli.getItem7().equals(GlobalFunction.getStatus("0"))){
                holder.llMenuContainer.setVisibility(View.VISIBLE);
            }else{
                holder.llMenuContainer.setVisibility(View.GONE);
            }
        }else if(tipePengajuan.equals(Utils.Pinjaman)){

            //holder.tvNoTransaksi.setText(cli.getItem1());
            holder.tvTanggal.setText(iv.ChangeFormatDateString(cli.getItem2(), dateFormat, dateFormatDisplay));
            holder.tvJumlah.setText(iv.ChangeToCurrencyFormat(cli.getItem6()));
            holder.tvStatus.setText(Html.fromHtml(cli.getItem7()));
            holder.tvLamaAngsuran.setText(cli.getItem3()+" Bulan");
            holder.tvJumlahAngsuran.setText(iv.ChangeToCurrencyFormat(cli.getItem4()));
            holder.tvKeterangan.setText("Lihat Detail");
            holder.llJenisTabungan.setVisibility(View.GONE);
            holder.llNoTabungan.setVisibility(View.GONE);
            holder.llDetailStatusContainer.setVisibility(View.VISIBLE);
            holder.llAngsuran.setVisibility(View.VISIBLE);
            holder.llKomentarContainer.setVisibility(View.GONE);
            deleteURL = WebServiceURL.deletePinjaman;

            holder.llDetailStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, DetailStatusPengajuan.class);
                    intent.putExtra("no_transaksi", cli.getItem1());
                    context.startActivity(intent);
                    //((AppCompatActivity) context).finish();
                }
            });

            if(cli.getItem7().equals(GlobalFunction.getStatus("0"))){
                holder.llDetailHapus.setVisibility(View.VISIBLE);
            }else{
                holder.llDetailHapus.setVisibility(View.GONE);
            }
        }

        final String finalDeleteURL = deleteURL;
        holder.llHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(context)
                        .setTitle("Konfirmasi")
                        .setMessage("Anda yakin ingin menghapus transaksi "+ cli.getItem1())
                        .setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                JSONObject jBody = new JSONObject();
                                session = new SessionManager(context);

                                ApiVolley request = new ApiVolley(context, jBody, "DELETE", finalDeleteURL+session.getNik()+"/"+ cli.getItem1(), "", "", 0, new ApiVolley.VolleyCallback() {
                                    @Override
                                    public void onSuccess(String result) {

                                        JSONObject responseAPI;
                                        try {
                                            responseAPI = new JSONObject(result);
                                            String status = responseAPI.getJSONObject("metadata").getString("status");
                                            String message = responseAPI.getJSONObject("metadata").getString("message");

                                            if(iv.parseNullInteger(status) == 200){

                                                Toast.makeText(context, "Data Berhasil dihapus", Toast.LENGTH_LONG).show();
                                                removeItem(position,cli.getItem1());
                                            }else if(iv.parseNullInteger(status) == 404){
                                                Toast.makeText(context, "Data tidak terhapus karena status telah berubah", Toast.LENGTH_LONG).show();
                                            }else{
                                                Toast.makeText(context, "Data tidak terhapus, silahkan coba beberapa saat lagi", Toast.LENGTH_LONG).show();
                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            Toast.makeText(context, "Data tidak terhapus, silahkan coba beberapa saat lagi", Toast.LENGTH_LONG).show();
                                        }
                                    }

                                    @Override
                                    public void onError(String result) {
                                        Toast.makeText(context, "Data tidak terhapus, silahkan coba beberapa saat lagi", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder.show();
            }
        });
    }

    private void removeItem(int position, String noTransaksi){
        masterList.remove(position);
        int x = 0;
        for(CustomListItem item: DetailPengajuan.masterList){
            if(item.getItem1().equals(noTransaksi)){
                DetailPengajuan.masterList.remove(x);
                DetailPengajuan.parseData(context, DetailPengajuan.masterList, bulanPosition, tahunPosition);
                break;
            }
            x++;
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return masterList.size();
    }

}