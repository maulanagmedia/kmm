package gmedia.net.id.kopkarmitramakmur.MenuInfoPinjaman.Adapter;

/**
 * Created by Shin on 3/21/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;

import gmedia.net.id.kopkarmitramakmur.R;
import gmedia.net.id.kopkarmitramakmur.GeneralModel.CustomListItem;
import gmedia.net.id.kopkarmitramakmur.MenuInfoPinjaman.SubDetailPinjaman;
import gmedia.net.id.kopkarmitramakmur.Util.ItemValidation;

public class BulanTahunAdapter extends RecyclerView.Adapter<BulanTahunAdapter.MyViewHolder> {

    private Context context;
    private List<CustomListItem> titleList;
    private HashMap<String,List<CustomListItem>> masterList;
    private ItemValidation iv = new ItemValidation();

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public CardView cvContainer;
        public TextView tvTitle;

        public MyViewHolder(View view) {
            super(view);
            cvContainer = (CardView) view.findViewById(R.id.cv_container);
            tvTitle = (TextView) view.findViewById(R.id.tv_title);
        }
    }

    public BulanTahunAdapter(Context context, List titleList, HashMap<String, List<CustomListItem>> masterList){
        this.context = context;
        this.titleList = titleList;
        this.masterList = masterList;
    }

    @Override
    public BulanTahunAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cv_list_pinjaman, parent, false);

        return new BulanTahunAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        final CustomListItem cli = titleList.get(position);
        holder.tvTitle.setText(cli.getItem1());
        holder.cvContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Gson gson = new Gson();
                String items = gson.toJson(masterList.get(cli.getItem1()));
                Intent intent = new Intent(context, SubDetailPinjaman.class);
                intent.putExtra("key", cli.getItem1());
                intent.putExtra("data", items);
                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    @Override
    public int getItemCount() {
        return titleList.size();
    }

}