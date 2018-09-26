package gmedia.net.id.kopkarmitramakmur.MenuBelanjaMandiri.Adapter;

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
import gmedia.net.id.kopkarmitramakmur.GeneralModel.CustomListItem;
import gmedia.net.id.kopkarmitramakmur.MenuBelanjaMandiri.DetailRiwayatBelanja;
import gmedia.net.id.kopkarmitramakmur.MenuBelanjaMandiri.RiwayatBelanja;
import gmedia.net.id.kopkarmitramakmur.Util.ApiVolley;
import gmedia.net.id.kopkarmitramakmur.Util.GlobalFunction;
import gmedia.net.id.kopkarmitramakmur.Util.ImageUtils;
import gmedia.net.id.kopkarmitramakmur.Util.ItemValidation;
import gmedia.net.id.kopkarmitramakmur.Util.SessionManager;
import gmedia.net.id.kopkarmitramakmur.Util.WebServiceURL;

public class RiwayatBelanjaAdapter extends RecyclerView.Adapter<RiwayatBelanjaAdapter.MyViewHolder> {

    private Context context;
    private List<CustomListItem> masterList;
    private ItemValidation iv = new ItemValidation();
    private ImageUtils iu = new ImageUtils();
    private SessionManager session;
    private String selectedBulan, selectedTahun;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout llTampil, llDelete;
        public TextView tvNoTransaksi, tvTanggal, tvStatus, tvJumlah, tvTotalHarga, tvCaraBayar;

        public MyViewHolder(View view) {
            super(view);
            llTampil = (LinearLayout)view.findViewById(R.id.ll_detail_belanja);
            llDelete = (LinearLayout)view.findViewById(R.id.ll_delete);
            tvNoTransaksi = (TextView) view.findViewById(R.id.tv_no_transaksi);
            tvTanggal = (TextView) view.findViewById(R.id.tv_tanggal);
            tvStatus = (TextView) view.findViewById(R.id.tv_status);
            tvJumlah = (TextView) view.findViewById(R.id.tv_jumlah);
            tvTotalHarga = (TextView) view.findViewById(R.id.tv_total_harga);
            tvCaraBayar = (TextView) view.findViewById(R.id.tv_cara_bayar);
        }
    }

    public RiwayatBelanjaAdapter(Context context, List<CustomListItem> masterList, String selectedBulan, String selectedTahun){
        this.context = context;
        this.masterList = masterList;
        this.selectedBulan = selectedBulan;
        this.selectedTahun = selectedTahun;
    }

    @Override
    public RiwayatBelanjaAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cv_riwayat_belanja, parent, false);

        return new RiwayatBelanjaAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final CustomListItem cli = masterList.get(position);

        holder.tvNoTransaksi.setText(cli.getItem1());
        String formatDate = "yyyy-MM-dd HH:mm:ss";
        String formatDateDispay = context.getResources().getString(R.string.format_date_display);
        holder.tvTanggal.setText(iv.ChangeFormatDateString(cli.getItem2(), formatDate, formatDateDispay));
        holder.tvStatus.setText(Html.fromHtml(GlobalFunction.getStatusBelanja(cli.getItem3())));
        holder.tvJumlah.setText(cli.getItem4());
        holder.tvTotalHarga.setText(cli.getItem6());
        holder.tvCaraBayar.setText(cli.getItem7());

        holder.llTampil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailRiwayatBelanja.class);
                intent.putExtra("no_transaksi", cli.getItem1());
                context.startActivity(intent);
            }
        });

        holder.llDelete.setOnClickListener(new View.OnClickListener() {
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

                                ApiVolley request = new ApiVolley(context, jBody, "DELETE", WebServiceURL.deleteRiwayatBelanja +session.getNik()+"/"+ cli.getItem1(), "", "", 0, new ApiVolley.VolleyCallback() {
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

    private void removeItem(int position, String noTransaksi) {

        masterList.remove(position);
        int x = 0;
        for(CustomListItem item: RiwayatBelanja.masterList){
            if(item.getItem1().equals(noTransaksi)){
                RiwayatBelanja.masterList.remove(x);
                RiwayatBelanja.parseData(context,RiwayatBelanja.masterList,selectedBulan, selectedTahun);
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