package gmedia.net.id.kopkarmitramakmur.MenuBelanjaMandiri.Adapter;

/**
 * Created by Shin on 3/21/2017.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import gmedia.net.id.kopkarmitramakmur.R;
import gmedia.net.id.kopkarmitramakmur.GeneralModel.CustomListItem;
import gmedia.net.id.kopkarmitramakmur.MenuBelanjaMandiri.DetailCart;
import gmedia.net.id.kopkarmitramakmur.Util.ImageUtils;
import gmedia.net.id.kopkarmitramakmur.Util.ItemValidation;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class DaftarCartAdapter extends RecyclerView.Adapter<DaftarCartAdapter.MyViewHolder> {

    private Context context;
    private List<CustomListItem> masterList;
    private AlertDialog.Builder dialog;
    private AlertDialog dialogViews;
    private View dialogView;
    private ItemValidation iv = new ItemValidation();
    private TextView tvTitle;
    private TextView tvHarga;
    private ImageView ivGambar;
    private EditText edtJumlah;
    private LinearLayout llEdit;
    private LinearLayout llCancel;
    private ImageUtils iu = new ImageUtils();
    private TextView tvSubmitCart;
    private TextView tvTitle1;
    private TextView tvHarga1;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivImage;
        public TextView tvTitle, tvJumlah, tvHarga, tvHargaTotal;
        public LinearLayout llEdit, llDelete;

        public MyViewHolder(View view) {
            super(view);
            ivImage = (ImageView) view.findViewById(R.id.iv_thumbnail);
            tvTitle = (TextView) view.findViewById(R.id.tv_title);
            tvJumlah = (TextView) view.findViewById(R.id.tv_jumlah);
            tvHarga = (TextView) view.findViewById(R.id.tv_harga);
            llEdit = (LinearLayout) view.findViewById(R.id.ll_edit);
            llDelete = (LinearLayout) view.findViewById(R.id.ll_delete);
            tvHargaTotal = (TextView) view.findViewById(R.id.tv_total_per_item);
        }
    }

    public DaftarCartAdapter(Context context, List<CustomListItem> masterList){
        this.context = context;
        this.masterList = masterList;
    }

    @Override
    public DaftarCartAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cv_cart_detail, parent, false);

        return new DaftarCartAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final CustomListItem cli = masterList.get(position);

        holder.tvTitle.setText(cli.getItem2());
        holder.tvJumlah.setText(cli.getItem6());
        holder.tvHarga.setText(iv.ChangeToRupiahFormat(cli.getItem3()));
        String totalHarga = String.valueOf(iv.parseNullInteger(cli.getItem6()) * iv.parseNullInteger(cli.getItem3()));
        holder.tvHargaTotal.setText(iv.ChangeToRupiahFormat(totalHarga));
        iu.LoadImageNormal(context, cli.getItem4(), holder.ivImage);

        holder.llEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogForm(cli,position);
            }
        });

        holder.llDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Hapus item")
                        .setMessage("Anda yakin ingin menghapus item "+cli.getItem2()+" dari daftar?")
                        .setIcon(R.mipmap.ic_kopkar_logo)
                        .setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int x = 0;
                                for (CustomListItem itemRow: DetailCart.cartList){
                                    if(itemRow.getItem1().equals(cli.getItem1())){
                                        DetailCart.cartList.set(x, null);
                                        DetailCart.refreshCartList();
                                    }
                                    x++;
                                }
                            }
                        })
                        .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
            }
        });
    }

    private void DialogForm(final CustomListItem item, final int position) {
        dialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        dialogView = inflater.inflate(R.layout.layout_item_cart, null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.setIcon(R.mipmap.ic_kopkar_logo);
        dialog.setTitle("Ubah Jumlah");

        tvTitle1 = (TextView) dialogView.findViewById(R.id.tv_title);
        tvHarga1 = (TextView) dialogView.findViewById(R.id.tv_harga);
        ivGambar = (ImageView) dialogView.findViewById(R.id.iv_thumbnail);
        edtJumlah = (EditText) dialogView.findViewById(R.id.edt_jumlah);
        edtJumlah.setText(item.getItem6());
        edtJumlah.setSelection(edtJumlah.getText().length());
        edtJumlah.requestFocus();
        llEdit = (LinearLayout) dialogView.findViewById(R.id.ll_add);
        tvSubmitCart = (TextView) dialogView.findViewById(R.id.tv_cart_submit);
        tvSubmitCart.setText(context.getResources().getString(R.string.edit_cart_string));
        llCancel = (LinearLayout) dialogView.findViewById(R.id.ll_cancel);

        tvTitle1.setText(item.getItem2());
        tvHarga1.setText(iv.ChangeToRupiahFormat(item.getItem3()));
        iu.LoadImageNormal(context, item.getItem4(), ivGambar);

        llCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogViews.dismiss();
            }
        });

        llEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(iv.parseNullInteger(edtJumlah.getText().toString()) <= 0){
                    edtJumlah.setError("Jumlah tidak boleh kurang dari 1");
                    return;
                }

                CustomListItem updatedItem = new CustomListItem(item.getItem1(), item.getItem2(), item.getItem3(), item.getItem4(), edtJumlah.getText().toString());

                int x = 0;
                for (CustomListItem itemRow: DetailCart.cartList){
                    if(itemRow.getItem1().equals(item.getItem1())){
                        DetailCart.cartList.set(x, updatedItem);
                        DetailCart.refreshCartList();
                        dialogViews.dismiss();
                        return;
                    }
                    x++;
                }
            }
        });

        dialogViews = dialog.create();
        dialogViews.show();

    }

    @Override
    public int getItemCount() {
        return masterList.size();
    }

}