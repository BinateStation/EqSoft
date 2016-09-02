package rkr.binatestation.eqsoft.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import rkr.binatestation.eqsoft.R;
import rkr.binatestation.eqsoft.models.CustomerModel;
import rkr.binatestation.eqsoft.models.OrderItemModelTemp;
import rkr.binatestation.eqsoft.models.ProductModel;
import rkr.binatestation.eqsoft.utils.Util;

/**
 * Created by RKR on 20/7/2016.
 * CustomerAdapter.
 */
public class OrderSummaryAdapter extends RecyclerView.Adapter<OrderSummaryAdapter.ItemView> {
    List<ProductModel> productModelList;
    CustomerModel customerModel;
    Map<String, OrderItemModelTemp> orderItemModelMap = new LinkedHashMap<>();
    OnAdapterInteractionListener onAdapterInteractionListener;
    Boolean clickable;

    public OrderSummaryAdapter(List<ProductModel> productModelList, CustomerModel customerModel, Map<String, OrderItemModelTemp> orderItemModelMap, boolean clickable, OnAdapterInteractionListener onAdapterInteractionListener) {
        this.productModelList = productModelList;
        this.customerModel = customerModel;
        this.onAdapterInteractionListener = onAdapterInteractionListener;
        this.orderItemModelMap = orderItemModelMap;
        this.clickable = clickable;
    }

    @Override
    public ItemView onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemView(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_order_summary, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(final ItemView holder, int position) {
        if (clickable) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (customerModel != null) {
                        popupEnterQuantity(view.getContext(), getItem(holder.getAdapterPosition()), holder.getAdapterPosition());
                    } else {
                        if (onAdapterInteractionListener != null) {
                            onAdapterInteractionListener.onItemClicked();
                        }
                    }
                }
            });
        }
        holder.productName.setText(getItem(position).getName());
        holder.productMRP.setText(String.format(Locale.getDefault(), "MRP - %.2f", getItem(position).getMRP()));
        holder.productCode.setText(getItem(position).getCode());
        holder.sellingPrice.setText(String.format(Locale.getDefault(), "%.2f", getItem(position).getSellingRate()));
        if (orderItemModelMap.containsKey(getItem(position).getCode())) {
            holder.selectedView.setVisibility(View.VISIBLE);
            holder.quantity.setText(String.format(Locale.getDefault(), "Qty: %.2f", orderItemModelMap.get(getItem(position).getCode()).getQuantity()));
            holder.amount.setText(String.format(Locale.getDefault(), "Amt: %.2f", orderItemModelMap.get(getItem(position).getCode()).getAmount()));
        } else {
            holder.selectedView.setVisibility(View.GONE);
        }
    }

    private ProductModel getItem(Integer position) {
        return productModelList.get(position);
    }

    @Override
    public int getItemCount() {
        return productModelList.size();
    }

    private void popupEnterQuantity(Context context, final ProductModel item, final int adapterPosition) {
        final AppCompatDialog appCompatDialog = new AppCompatDialog(context);
        appCompatDialog.setContentView(R.layout.popup_enter_product_quantity);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        Window window = appCompatDialog.getWindow();
        layoutParams.copyFrom(window.getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(layoutParams);
        window.getAttributes().windowAnimations = R.style.dialog_animation;


        TextView productName = (TextView) appCompatDialog.findViewById(R.id.PEPQ_productName);
        TextView MRP = (TextView) appCompatDialog.findViewById(R.id.PEPQ_MRP);
        TextView productCode = (TextView) appCompatDialog.findViewById(R.id.PEPQ_productCode);
        TextView stock = (TextView) appCompatDialog.findViewById(R.id.PEPQ_stock);
        TextView sellingPrice = (TextView) appCompatDialog.findViewById(R.id.PEPQ_sellingPrice);
        final TextInputEditText quantity = (TextInputEditText) appCompatDialog.findViewById(R.id.PEPQ_quantity);
        final AppCompatTextView amount = (AppCompatTextView) appCompatDialog.findViewById(R.id.PEPQ_amount);
        final AppCompatButton okay = (AppCompatButton) appCompatDialog.findViewById(R.id.PEPQ_ok);
        final AppCompatButton remove = (AppCompatButton) appCompatDialog.findViewById(R.id.PEPQ_remove);

        try {
            if (item != null) {
                if (productName != null) {
                    productName.setText(item.getName());
                }
                if (MRP != null) {
                    MRP.setText(String.format(Locale.getDefault(), "MRP - %.2f", item.getMRP()));
                }
                if (productCode != null) {
                    productCode.setText(item.getCode());
                }
                if (stock != null) {
                    stock.setText(String.format(Locale.getDefault(), "%.2f", item.getStock()));
                }
                if (sellingPrice != null) {
                    sellingPrice.setText(String.format(Locale.getDefault(), "%.2f", item.getSellingRate()));
                }
                if (quantity != null) {
                    if (orderItemModelMap.containsKey(item.getCode())) {
                        quantity.setText(String.format(Locale.getDefault(), "%.2f", orderItemModelMap.get(item.getCode()).getQuantity()));
                        quantity.setSelection(quantity.getText().length());
                    }
                    quantity.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
                            if (editable.length() > 0) {
                                try {
                                    if (amount != null) {
                                        amount.setText(String.format(Locale.getDefault(), "%.2f", (Integer.parseInt(editable.toString()) * item.getSellingRate())));
                                    }
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                    Util.showAlert(amount.getContext(), "Alert", "Please enter a valid quantity.");
                                }
                            }
                        }
                    });
                    quantity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View view, boolean b) {
                            if (b) {
                                appCompatDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                            }
                        }
                    });
                    quantity.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                            if (actionId == EditorInfo.IME_ACTION_DONE) {
                                if (okay != null) {
                                    okay.performClick();
                                }
                                return true;
                            }
                            return false;
                        }
                    });
                    quantity.requestFocus();
                }
                if (okay != null) {
                    okay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (quantity != null && amount != null && quantity.getText().length() > 0 && amount.getText().length() > 0) {
                                addOrder(
                                        view.getContext(),
                                        item,
                                        quantity.getText().toString().trim(),
                                        amount.getText().toString().trim(),
                                        appCompatDialog,
                                        adapterPosition
                                );
                            } else {
                                appCompatDialog.dismiss();
                            }
                        }
                    });
                }

                if (remove != null) {
                    remove.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            removeOrder(view.getContext(), item, appCompatDialog, adapterPosition);
                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        appCompatDialog.show();
    }

    private void addOrder(final Context context, final ProductModel item, final String quantity, final String amount, final AppCompatDialog appCompatDialog, final int adapterPosition) {
        new AsyncTask<Void, Void, OrderItemModelTemp>() {
            @Override
            protected OrderItemModelTemp doInBackground(Void... voids) {
                OrderItemModelTemp orderItemModelTemp = new OrderItemModelTemp(
                        item.getCode(),
                        item.getSellingRate(),
                        Double.parseDouble(quantity),
                        Double.parseDouble(amount),
                        true
                );
                OrderItemModelTemp orderItemModelTempDB = new OrderItemModelTemp(context);
                orderItemModelTempDB.open();
                orderItemModelTempDB.insert(orderItemModelTemp);
                orderItemModelTempDB.close();
                return orderItemModelTemp;
            }

            @Override
            protected void onPostExecute(OrderItemModelTemp orderItemModel) {
                super.onPostExecute(orderItemModel);
                if (appCompatDialog != null && appCompatDialog.isShowing()) {
                    appCompatDialog.dismiss();
                }
                if (orderItemModel != null) {
                    orderItemModelMap.put(orderItemModel.getProductCode(), orderItemModel);
                    notifyItemChanged(adapterPosition);
                    if (onAdapterInteractionListener != null) {
                        onAdapterInteractionListener.onProductSelected();
                    }
                }
            }
        }.execute();
    }

    private void removeOrder(final Context context, final ProductModel item, final AppCompatDialog appCompatDialog, final int adapterPosition) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                OrderItemModelTemp orderItemModelTempDB = new OrderItemModelTemp(context);
                orderItemModelTempDB.open();
                orderItemModelTempDB.deleteRow(item.getCode());
                orderItemModelTempDB.close();
                return null;
            }

            @Override
            protected void onPostExecute(Void orderItemModel) {
                super.onPostExecute(orderItemModel);
                if (appCompatDialog != null && appCompatDialog.isShowing()) {
                    appCompatDialog.dismiss();
                }
                orderItemModelMap.remove(item.getCode());
                productModelList.remove(adapterPosition);
                notifyDataSetChanged();
                if (onAdapterInteractionListener != null) {
                    onAdapterInteractionListener.onProductSelected();
                }
            }
        }.execute();
    }

    public interface OnAdapterInteractionListener {
        void onItemClicked();

        void onProductSelected();
    }

    class ItemView extends RecyclerView.ViewHolder {
        View itemView;
        TextView productName, productMRP, productCode, sellingPrice, quantity, amount;
        View selectedView;

        public ItemView(View itemView) {
            super(itemView);
            this.itemView = itemView;
            productName = (TextView) itemView.findViewById(R.id.AP_productName);
            productMRP = (TextView) itemView.findViewById(R.id.AP_productMRP);
            productCode = (TextView) itemView.findViewById(R.id.AP_productCode);
            sellingPrice = (TextView) itemView.findViewById(R.id.AP_productSellingPrice);
            selectedView = itemView.findViewById(R.id.AP_isSelected);
            quantity = (TextView) itemView.findViewById(R.id.AP_productQty);
            amount = (TextView) itemView.findViewById(R.id.AP_productAmt);
        }
    }
}