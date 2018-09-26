package gmedia.net.id.kopkarmitramakmur.MenuInformasiTabungan.Adapter;

/**
 * Created by Shin on 3/21/2017.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import gmedia.net.id.kopkarmitramakmur.R;
import gmedia.net.id.kopkarmitramakmur.GeneralModel.CustomListItem;
import gmedia.net.id.kopkarmitramakmur.Util.ItemValidation;

public class DetailInfoTabunganAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<CustomListItem> masterList;
    private ItemValidation iv = new ItemValidation();

    public class MyViewHolderHeader extends RecyclerView.ViewHolder {

        public MyViewHolderHeader(View view) {
            super(view);
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvKeterangan, tvStatus, tvSaldo;

        public MyViewHolder(View view) {
            super(view);
            tvKeterangan = (TextView) view.findViewById(R.id.tv_keterangan);
            //tvStatus = (TextView) view.findViewById(R.id.tv_status);
            tvSaldo = (TextView) view.findViewById(R.id.tv_saldo);

        }
    }

    public class MyViewHolderDate extends RecyclerView.ViewHolder {

        public TextView tvDate;

        public MyViewHolderDate(View view) {
            super(view);
            tvDate = (TextView) view.findViewById(R.id.tv_date);
        }
    }

    public DetailInfoTabunganAdapter(Context context, List<CustomListItem> masterList){
        this.context = context;
        this.masterList = masterList;
    }

    @Override
    public int getItemViewType(int position) {
        final CustomListItem list = masterList.get(position);

        if(list.getItem8().toLowerCase().equals("header")){
            return 0;
        }else if(list.getItem8().toLowerCase().equals("date")){
            return 2;
        }else{
            return 1;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        switch (viewType){
            case 0:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.cv_transaksi_tabungan_header, parent, false);
                return new DetailInfoTabunganAdapter.MyViewHolderHeader(itemView);
            case 1:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.cv_transaksi_tabungan, parent, false);
                return new DetailInfoTabunganAdapter.MyViewHolder(itemView);
            case 2:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.cv_transaksi_tabungan_date, parent, false);
                return new DetailInfoTabunganAdapter.MyViewHolderDate(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holderMaster, int position) {
        final CustomListItem cli = masterList.get(position);
        switch (holderMaster.getItemViewType()){
            case 0:
                break;
            case 1:
                MyViewHolder holder = (MyViewHolder) holderMaster;
                String formatDate = context.getResources().getString(R.string.format_date);
                String formatDateDisplay = "dd/MM/yy";

                String status = "DB";
                String nominal = "0";
                if(iv.parseNullInteger(cli.getItem3())> 0){
                    nominal = cli.getItem3();
                    status = "CR";
                    holder.tvSaldo.setText(Html.fromHtml("<font color='#4390da'>+ "+iv.ChangeToCurrencyFormat(nominal)+"</font>"));
                }else{
                    nominal = cli.getItem4();
                    holder.tvSaldo.setText(Html.fromHtml("<font color='#E35152'>- "+iv.ChangeToCurrencyFormat(nominal)+"</font>"));
                }
                holder.tvKeterangan.setText(cli.getItem7());
                //holder.tvStatus.setText(status);
                //holder.tvSaldo.setText(iv.ChangeToCurrencyFormat(cli.getItem6()));
                break;
            case 2:
                MyViewHolderDate holderDate = (MyViewHolderDate) holderMaster;
                String formatDate1 = context.getResources().getString(R.string.format_date);
                String formatDateDisplay1 = "dd/MM/yy";
                holderDate.tvDate.setText(iv.ChangeFormatDateString(cli.getItem1(), formatDate1, formatDateDisplay1));
                break;
        }

    }

    @Override
    public int getItemCount() {
        return masterList.size();
    }

}