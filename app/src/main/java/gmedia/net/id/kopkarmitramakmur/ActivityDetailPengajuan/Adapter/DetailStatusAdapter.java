package gmedia.net.id.kopkarmitramakmur.ActivityDetailPengajuan.Adapter;

/**
 * Created by Shin on 3/21/2017.
 */

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import gmedia.net.id.kopkarmitramakmur.R;
import gmedia.net.id.kopkarmitramakmur.GeneralModel.CustomListItem;
import gmedia.net.id.kopkarmitramakmur.Util.ItemValidation;

public class DetailStatusAdapter extends RecyclerView.Adapter<DetailStatusAdapter.MyViewHolder> {

    private Context context;
    private List<CustomListItem> masterList;
    private ItemValidation iv = new ItemValidation();

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public CardView cvContainer;
        public TextView tvTitle, tvTampil, tvNoTransaksi, tvTglPengajuan, tvJmlPengajuan, tvNamaPenyetuju, tvJabatan, tvKeterangan, tvTglPersetujuan;
        public LinearLayout llTampil, llDetail;

        public MyViewHolder(View view) {
            super(view);
            cvContainer = (CardView) view.findViewById(R.id.cv_container);
            tvTitle = (TextView) view.findViewById(R.id.tv_title);
            llDetail = (LinearLayout) view.findViewById(R.id.ll_detail);
            llTampil = (LinearLayout) view.findViewById(R.id.ll_tampil);
            tvTampil = (TextView) view.findViewById(R.id.tv_tampil);
            tvNoTransaksi = (TextView) view.findViewById(R.id.tv_no_transaksi);
            tvTglPengajuan = (TextView) view.findViewById(R.id.tv_tgl_pengajuan);
            tvJmlPengajuan = (TextView) view.findViewById(R.id.tv_jml_pengajuan);
            tvNamaPenyetuju = (TextView) view.findViewById(R.id.tv_nama);
            tvJabatan = (TextView) view.findViewById(R.id.tv_jabatan);
            tvKeterangan = (TextView) view.findViewById(R.id.tv_keterangan);
            tvTglPersetujuan = (TextView) view.findViewById(R.id.tv_tgl_persetujuan);
        }
    }

    public DetailStatusAdapter(Context context, List masterlist){
        this.context = context;
        this.masterList = masterlist;
    }

    @Override
    public DetailStatusAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cv_status_pinjaman, parent, false);

        return new DetailStatusAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final CustomListItem cli = masterList.get(position);
        holder.tvTitle.setText(Html.fromHtml(cli.getItem1()));
        holder.tvNoTransaksi.setText(cli.getItem2());
        holder.tvTglPengajuan.setText(cli.getItem3());
        holder.tvJmlPengajuan.setText(cli.getItem4());
        holder.tvNamaPenyetuju.setText(cli.getItem6());
        holder.tvJabatan.setText(cli.getItem7());
        holder.tvKeterangan.setText(cli.getItem8());
        holder.tvTglPersetujuan.setText(cli.getItem9());
        holder.llTampil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.llDetail.getVisibility() == View.VISIBLE){

                    holder.tvTampil.setText("Tampilkan");
                    holder.llDetail.setVisibility(View.GONE);
                }else{

                    holder.tvTampil.setText("Sembunyikan");
                    holder.llDetail.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return masterList.size();
    }

}