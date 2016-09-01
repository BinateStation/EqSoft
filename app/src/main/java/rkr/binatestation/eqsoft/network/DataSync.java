package rkr.binatestation.eqsoft.network;

import android.content.Context;
import android.database.SQLException;
import android.os.AsyncTask;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import rkr.binatestation.eqsoft.models.CustomerModel;
import rkr.binatestation.eqsoft.models.OrderItemModel;
import rkr.binatestation.eqsoft.models.OrderModel;
import rkr.binatestation.eqsoft.models.ProductModel;
import rkr.binatestation.eqsoft.models.ReceiptModel;
import rkr.binatestation.eqsoft.models.UserDetailsModel;
import rkr.binatestation.eqsoft.utils.Constants;
import rkr.binatestation.eqsoft.utils.Util;

/**
 * Created by RKR on 2/8/2016.
 * DataSync.
 */
public class DataSync extends AsyncTask<Integer, Integer, Boolean> {
    Context context;

    public DataSync(Context context) {
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(Integer... integers) {
        switch (integers[0]) {
            case 1:
                return writeToFile(getCompleteJsonStringFromDB());
            case 2:
                return readFromFile(context);
            default:
                if (writeToFile(getCompleteJsonStringFromDB())) {
                    return readFromFile(context);
                } else {
                    return false;
                }
        }
    }

    private String getCompleteJsonStringFromDB() {
        OrderModel orderModelDB = new OrderModel(context);
        orderModelDB.open();
        JSONArray ordersJsonArray = orderModelDB.getAllRowsAsJSONArray();
        orderModelDB.close();


        ReceiptModel receiptModelDB = new ReceiptModel(context);
        receiptModelDB.open();
        JSONArray receiptsJsonArray = receiptModelDB.getAllRowsAsJSONArray();
        receiptModelDB.close();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Order", ordersJsonArray);
            jsonObject.put("Receipts", receiptsJsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private Boolean writeToFile(String data) {
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(new File(Util.getDatabasePath() + "output.json"));
            outputStream.write(data.getBytes());
            outputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private Boolean readFromFile(Context context) {
        try {
            File jsonFile = new File(Util.getDatabasePath() + "input.json");
            if (jsonFile.exists()) {
                //Read text from file
                StringBuilder text = new StringBuilder();

                try {
                    BufferedReader br = new BufferedReader(new FileReader(jsonFile));
                    String line;

                    while ((line = br.readLine()) != null) {
                        text.append(line);
                        text.append('\n');
                    }
                    br.close();
                } catch (IOException e) {
                    //You'll need to add proper error handling here
                }
                JSONObject jsonObj = new JSONObject(text.toString());

                // Getting data JSON Array nodes
                JSONArray customersJsonArray = jsonObj.getJSONArray("Customers");

                List<CustomerModel> customerModelList = new ArrayList<>();
                // looping through All nodes
                for (int i = 0; i < customersJsonArray.length(); i++) {
                    JSONObject customerJsonObject = customersJsonArray.getJSONObject(i);
                    customerModelList.add(new CustomerModel(
                            customerJsonObject.optString("Address1").trim(),
                            customerJsonObject.optString("Address2").trim(),
                            customerJsonObject.optString("Address3").trim(),
                            customerJsonObject.optDouble("Balance"),
                            customerJsonObject.optString("Code").trim(),
                            customerJsonObject.optString("Email").trim(),
                            customerJsonObject.optString("LedgerName").trim(),
                            customerJsonObject.optString("Mobile").trim(),
                            customerJsonObject.optString("Name").trim(),
                            customerJsonObject.optString("Phone").trim(),
                            customerJsonObject.optString("Route").trim(),
                            customerJsonObject.optString("RouteIndex").trim()
                    ));
                }

                try {
                    CustomerModel customerModelDB = new CustomerModel(context);
                    customerModelDB.open();
                    customerModelDB.deleteAll();
                    customerModelDB.insertMultipleRows(customerModelList);
                    customerModelDB.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                JSONArray productsJsonArray = jsonObj.getJSONArray("Products");

                List<ProductModel> productModelList = new ArrayList<>();
                for (int i = 0; i < productsJsonArray.length(); i++) {
                    JSONObject productJsonObject = productsJsonArray.getJSONObject(i);
                    productModelList.add(new ProductModel(
                            productJsonObject.optString("Category").trim(),
                            productJsonObject.optString("Code").trim(),
                            productJsonObject.optDouble("MRP"),
                            productJsonObject.optString("Name").trim(),
                            productJsonObject.optDouble("SellingRate"),
                            productJsonObject.optDouble("Stock"),
                            productJsonObject.optDouble("TaxRate")
                    ));
                }

                try {
                    ProductModel productModelDB = new ProductModel(context);
                    productModelDB.open();
                    productModelDB.deleteAll();
                    productModelDB.insertMultipleRows(productModelList);
                    productModelDB.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                JSONArray usersJsonArray = jsonObj.getJSONArray("User");

                List<UserDetailsModel> userDetailsModelList = new ArrayList<>();
                for (int i = 0; i < usersJsonArray.length(); i++) {
                    JSONObject userJsonObject = usersJsonArray.getJSONObject(i);
                    String username = userJsonObject.optString("Username");
                    if (username != null && !TextUtils.isEmpty(username)) {
                        userDetailsModelList.add(new UserDetailsModel(
                                userJsonObject.optString("Userid"),
                                username.toUpperCase(Locale.getDefault()),
                                userJsonObject.optString("Password")
                        ));
                    }
                }
                try {
                    UserDetailsModel userDetailsTable = new UserDetailsModel(context);
                    userDetailsTable.open();
                    userDetailsTable.insertMultipleRows(userDetailsModelList);
                    userDetailsTable.close();

                    OrderModel orderModelDB = new OrderModel(context);
                    orderModelDB.open();
                    orderModelDB.deleteAll();
                    orderModelDB.close();

                    OrderItemModel orderItemModelDB = new OrderItemModel(context);
                    orderItemModelDB.open();
                    orderItemModelDB.deleteAll();
                    orderItemModelDB.close();

                    ReceiptModel receiptModelDB = new ReceiptModel(context);
                    receiptModelDB.open();
                    receiptModelDB.deleteAll();
                    receiptModelDB.close();

                    context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE)
                            .edit().putString(Constants.KEY_LAST_SELECTED_CUSTOMER, "").apply();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return true;
            } else {
                Util.showAlert(context, "Alert", "File doesn't exists, please copy the file to specified folder and sync.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Util.showAlert(context, "Alert", "File doesn't exists, please copy the file to specified folder and sync.");
        }
        return false;
    }

}
