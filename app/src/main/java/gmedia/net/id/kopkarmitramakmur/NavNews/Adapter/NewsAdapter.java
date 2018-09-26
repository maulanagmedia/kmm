package gmedia.net.id.kopkarmitramakmur.NavNews.Adapter;

/**
 * Created by Shin on 3/21/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import gmedia.net.id.kopkarmitramakmur.R;
import gmedia.net.id.kopkarmitramakmur.GeneralModel.CustomListItem;
import gmedia.net.id.kopkarmitramakmur.NavNews.DetailNews;
import gmedia.net.id.kopkarmitramakmur.Util.ImageUtils;
import gmedia.net.id.kopkarmitramakmur.Util.ItemValidation;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyViewHolder> {

    private Context context;
    private List<CustomListItem> masterList;
    private ItemValidation iv = new ItemValidation();

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvTitle;
        public TextView tvKeterangan;
        public ImageView ivThumbnail;
        public LinearLayout llContainer;

        public MyViewHolder(View view) {
            super(view);
            llContainer = (LinearLayout) view.findViewById(R.id.ll_container);
            ivThumbnail = (ImageView) view.findViewById(R.id.iv_thumbnail);
            tvTitle = (TextView) view.findViewById(R.id.tv_title);
            tvKeterangan = (TextView) view.findViewById(R.id.tv_keterangan);
        }
    }

    public NewsAdapter(Context context, List masterList){
        this.context = context;
        this.masterList = masterList;
    }

    @Override
    public NewsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cv_news, parent, false);

        return new NewsAdapter.MyViewHolder(itemView);
    }

    public void AddMoreData(List<CustomListItem> listItems){
        masterList.addAll(listItems);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        final CustomListItem cli = masterList.get(position);
        holder.tvTitle.setText(cli.getItem2());
        holder.tvKeterangan.setText(cli.getItem3());
        ImageUtils iu = new ImageUtils();
        iu.LoadImageNormal(context,cli.getItem4(),holder.ivThumbnail);

        holder.llContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, DetailNews.class);
                intent.putExtra("id", cli.getItem1());
                intent.putExtra("state", "2");
                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

    }

    @Override
    public int getItemCount() {
        return masterList.size();
    }

}