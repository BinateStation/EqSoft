package rkr.binatestation.eqsoft.network;

import android.content.Context;
import android.os.AsyncTask;

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

import rkr.binatestation.eqsoft.models.CustomerModel;
import rkr.binatestation.eqsoft.models.OrderItemModel;
import rkr.binatestation.eqsoft.models.OrderModel;
import rkr.binatestation.eqsoft.models.ProductModel;
import rkr.binatestation.eqsoft.models.ReceiptModel;
import rkr.binatestation.eqsoft.models.UserDetailsModel;
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

        OrderItemModel orderItemModelDB = new OrderItemModel(context);
        orderItemModelDB.open();
        JSONArray orderItemsJsonArray = orderItemModelDB.getAllRowsAsJSONArray();
        orderItemModelDB.close();

        ReceiptModel receiptModelDB = new ReceiptModel(context);
        receiptModelDB.open();
        JSONArray receiptsJsonArray = receiptModelDB.getAllRowsAsJSONArray();
        receiptModelDB.close();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("orders", ordersJsonArray);
            jsonObject.put("order_items", orderItemsJsonArray);
            jsonObject.put("receipts", receiptsJsonArray);
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
                            customerJsonObject.optString("Balance").trim(),
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

                CustomerModel customerModelDB = new CustomerModel(context);
                customerModelDB.open();
                customerModelDB.deleteAll();
                customerModelDB.insertMultipleRows(customerModelList);
                customerModelDB.close();

                // Getting data JSON Array nodes
                JSONArray productsJsonArray = jsonObj.getJSONArray("Products");

                List<ProductModel> productModelList = new ArrayList<>();
                // looping through All nodes
                for (int i = 0; i < productsJsonArray.length(); i++) {
                    JSONObject productJsonObject = productsJsonArray.getJSONObject(i);
                    productModelList.add(new ProductModel(
                            productJsonObject.optString("Category").trim(),
                            productJsonObject.optString("Code").trim(),
                            productJsonObject.optString("MRP").trim(),
                            productJsonObject.optString("Name").trim(),
                            productJsonObject.optString("SellingRate").trim(),
                            productJsonObject.optString("Stock").trim(),
                            productJsonObject.optString("TaxRate").trim()
                    ));
                }

                ProductModel productModelDB = new ProductModel(context);
                productModelDB.open();
                productModelDB.deleteAll();
                productModelDB.insertMultipleRows(productModelList);
                productModelDB.close();

                UserDetailsModel userDetailsTable = new UserDetailsModel(context);
                userDetailsTable.open();
                userDetailsTable.insert(new UserDetailsModel(
                        "12345",
                        "username",
                        "password"
                ));
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
