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
import rkr.binatestation.eqsoft.models.OrderItemModel;
import rkr.binatestation.eqsoft.models.OrderModel;
import rkr.binatestation.eqsoft.models.ProductModel;
import rkr.binatestation.eqsoft.utils.Constants;
import rkr.binatestation.eqsoft.utils.Util;

/**
 * Created by RKR on 20/7/2016.
 * CustomerAdapter.
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ItemView> {
    List<ProductModel> productModelList;
    CustomerModel customerModel;
    Map<String, OrderItemModel> orderItemModelMap = new LinkedHashMap<>();
    OnAdapterInteractionListener onAdapterInteractionListener;
    Boolean clickable;

    public ProductAdapter(List<ProductModel> productModelList, CustomerModel customerModel, Map<String, OrderItemModel> orderItemModelMap, boolean clickable, OnAdapterInteractionListener onAdapterInteractionListener) {
        this.productModelList = productModelList;
        this.customerModel = customerModel;
        this.onAdapterInteractionListener = onAdapterInteractionListener;
        this.orderItemModelMap = orderItemModelMap;
        this.clickable = clickable;
    }

    public void setCustomerModel(CustomerModel customerModel) {
        this.customerModel = customerModel;
    }

    public void setOrderItemModelMap(Map<String, OrderItemModel> orderItemModelMap) {
        this.orderItemModelMap = orderItemModelMap;
    }

    @Override
    public ItemView onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemView(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_product, parent, false)
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
        holder.productMRP.setText(String.format(Locale.getDefault(), "MRP - %s", Double.parseDouble(getItem(position).getMRP())));
        holder.productCode.setText(getItem(position).getCode());
        holder.stock.setText(getItem(position).getStock());
        holder.sellingPrice.setText(String.format(Locale.getDefault(), "%s", Double.parseDouble(getItem(position).getSellingRate())));
        if (orderItemModelMap.containsKey(getItem(position).getCode())) {
            holder.selectedView.setVisibility(View.VISIBLE);
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

        try {
            if (item != null) {
                if (productName != null) {
                    productName.setText(item.getName());
                }
                if (MRP != null) {
                    MRP.setText(String.format(Locale.getDefault(), "MRP - %s", item.getMRP()));
                }
                if (productCode != null) {
                    productCode.setText(item.getCode());
                }
                if (stock != null) {
                    stock.setText(item.getStock());
                }
                if (sellingPrice != null) {
                    sellingPrice.setText(item.getSellingRate());
                }
                if (quantity != null) {
                    if (orderItemModelMap.containsKey(item.getCode())) {
                        quantity.setText(orderItemModelMap.get(item.getCode()).getQuantity());
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
                                        amount.setText(String.format("%s", Integer.parseInt(editable.toString()) * Double.parseDouble(item.getSellingRate())));
                                    }
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        appCompatDialog.show();
    }

    private void addOrder(final Context context, final ProductModel item, final String quantity, final String amount, final AppCompatDialog appCompatDialog, final int adapterPosition) {
        new AsyncTask<Void, Void, OrderItemModel>() {
            @Override
            protected OrderItemModel doInBackground(Void... voids) {
                OrderModel orderModelDB = new OrderModel(context);
                orderModelDB.open();
                OrderModel orderModel = orderModelDB.getCustomersRow(customerModel.getCode());
                orderModelDB.close();
                String totalAmount;
                OrderItemModel orderItemModel = null;
                if (orderModel != null) {
                    totalAmount = "" + (Double.parseDouble(orderModel.getAmount()) + Double.parseDouble(amount));
                    orderModelDB.open();
                    orderModelDB.updateRow(new OrderModel(
                            orderModel.getOrderId(),
                            orderModel.getDocDate(),
                            orderModel.getCustomerCode(),
                            totalAmount,
                            orderModel.getReceivedAmount(),
                            orderModel.getDueDate(),
                            orderModel.getRemarks(),
                            orderModel.getUserId()
                    ));
                    orderModelDB.close();
                    orderItemModel = new OrderItemModel(
                            orderModel.getOrderId(),
                            item.getCode(),
                            item.getSellingRate(),
                            quantity,
                            amount
                    );
                    OrderItemModel orderItemModelDB = new OrderItemModel(context);
                    orderItemModelDB.open();
                    orderItemModelDB.insert(orderItemModel);
                    orderItemModelDB.close();
                } else {
                    totalAmount = amount;
                    orderModelDB.open();
                    Long orderId = orderModelDB.insert(new OrderModel(
                            "0",
                            Util.getCurrentDate("yyyy-MM-dd HH:mm:ss"),
                            customerModel.getCode(),
                            amount,
                            "0",
                            "",
                            "",
                            Util.getStringFromSharedPreferences(context, Constants.KEY_USER_ID)
                    ));
                    orderModelDB.close();
                    if (orderId != -1) {
                        orderItemModel = new OrderItemModel(
                                "" + orderId,
                                item.getCode(),
                                item.getSellingRate(),
                                quantity,
                                amount
                        );
                        OrderItemModel orderItemModelDB = new OrderItemModel(context);
                        orderItemModelDB.open();
                        orderItemModelDB.insert(orderItemModel);
                        orderItemModelDB.close();
                    }
                }

                CustomerModel customerModelDB = new CustomerModel(context);
                customerModelDB.open();
                try {
                    if (customerModel != null) {
                        customerModel.setBalance("" +
                                (Double.parseDouble(customerModel.getBalance()) +
                                        Double.parseDouble(totalAmount))
                        );
                        customerModelDB.updateRow(customerModel);
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                customerModelDB.close();
                return orderItemModel;
            }

            @Override
            protected void onPostExecute(OrderItemModel orderItemModel) {
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

    public interface OnAdapterInteractionListener {
        void onItemClicked();

        void onProductSelected();
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
