package rkr.binatestation.eqsoft.adapters;

import android.content.Context;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import rkr.binatestation.eqsoft.R;
import rkr.binatestation.eqsoft.models.ProductModel;

/**
 * Created by RKR on 20/7/2016.
 * CustomerAdapter.
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ItemView> {
    List<ProductModel> productModelList;

    public ProductAdapter(List<ProductModel> productModelList) {
        this.productModelList = productModelList;
    }

    @Override
    public ItemView onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemView(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_product, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(ItemView holder, int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupEnterQuantity(view.getContext());
            }
        });
        holder.productName.setText(getItem(position).getName());
        holder.productMRP.setText(String.format(Locale.getDefault(), "MRP - %s", getItem(position).getMRP()));
        holder.productCode.setText(getItem(position).getCode());
        holder.stock.setText(getItem(position).getStock());
        holder.sellingPrice.setText(getItem(position).getSellingRate());
    }

    private ProductModel getItem(Integer position) {
        return productModelList.get(position);
    }

    @Override
    public int getItemCount() {
        return productModelList.size();
    }

    private void popupEnterQuantity(Context context) {
        AppCompatDialog appCompatDialog = new AppCompatDialog(context);
        appCompatDialog.setContentView(R.layout.popup_enter_product_quantity);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        Window window = appCompatDialog.getWindow();
        layoutParams.copyFrom(window.getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(layoutParams);
        window.getAttributes().windowAnimations = R.style.dialog_animation;

        appCompatDialog.show();
    }

    class ItemView extends RecyclerView.ViewHolder {
        View itemView;
        TextView productName, productMRP, productCode, stock, sellingPrice;
        View selectedView;

        public ItemView(View itemView) {
            super(itemView);
            this.itemView = itemView;
            productName = (TextView) itemView.findViewById(R.id.AP_productName);
            productMRP = (TextView) itemView.findViewById(R.id.AP_productMRP);
            productCode = (TextView) itemView.findViewById(R.id.AP_productCode);
            stock = (TextView) itemView.findViewById(R.id.AP_productStock);
            sellingPrice = (TextView) itemView.findViewById(R.id.AP_productSellingPrice);
            selectedView = itemView.findViewById(R.id.AP_isSelected);
        }
    }
}
