package gmedia.net.id.kopkarmitramakmur.NavHome.Adapter;

/**
 * Created by Shin on 2/28/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import gmedia.net.id.kopkarmitramakmur.R;
import gmedia.net.id.kopkarmitramakmur.NavHome.Model.HeaderSliderModel;
import gmedia.net.id.kopkarmitramakmur.NavNews.DetailNews;
import gmedia.net.id.kopkarmitramakmur.Util.GlobalFunction;
import gmedia.net.id.kopkarmitramakmur.Util.ImageUtils;


public class MenuHeaderSliderAdapter extends PagerAdapter {

    private Context context;
    private List<HeaderSliderModel> resource;
    private int maxSlide, screenHeight;
    private GlobalFunction gf = new GlobalFunction();

    public MenuHeaderSliderAdapter(Context context, List<HeaderSliderModel> resource, int screenHeight) {
        this.context = context;
        this.resource = resource;
        maxSlide = context.getResources().getInteger(R.integer.max_item_slide);
        this.screenHeight = screenHeight;
    }

    @Override
    public int getCount() {
        if( maxSlide > resource.size()){
            return resource.size();
        }else{
            return maxSlide;
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.slider_pager_item, container, false);

        final HeaderSliderModel data = resource.get(position);
        RelativeLayout rlContainer = (RelativeLayout) itemView.findViewById(R.id.rl_container);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.img_pager_item);
        TextView tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
        TextView tvSubtitle = (TextView) itemView.findViewById(R.id.tv_subtitle);

        RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
        RelativeLayout.LayoutParams rl2 = new RelativeLayout.LayoutParams(rl.width, (int) (screenHeight * 1.3));
        imageView.setLayoutParams(rl2);

        rlContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailNews.class);
                intent.putExtra("id", data.getId());
                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        ImageUtils iu = new ImageUtils();
        iu.LoadImageHeaderSlider(context, data.getUrlGambar(), imageView);
        tvTitle.setText(data.getTitle());
        tvSubtitle.setText(Html.fromHtml(data.getKeterangan()));

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
