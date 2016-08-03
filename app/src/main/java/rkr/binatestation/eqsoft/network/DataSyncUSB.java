package rkr.binatestation.eqsoft.network;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rkr.binatestation.eqsoft.models.CustomerModel;
import rkr.binatestation.eqsoft.models.ProductModel;
import rkr.binatestation.eqsoft.models.UserDetailsModel;
import rkr.binatestation.eqsoft.utils.Util;

/**
 * Created by RKR on 2/8/2016.
 * DataSyncUSB.
 */
public class DataSyncUSB extends AsyncTask<Context, Integer, Boolean> {
    @Override
    protected Boolean doInBackground(Context... contexts) {
        readFromFile(contexts[0]);
        return null;
    }

    private void readFromFile(Context context) {
        try {
            File jsonFile = new File(Util.getDatabasePath() + "sample data.json");
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

            } else {
                Util.showAlert(context, "Alert", "File doesn't exists, please copy the file to specified folder and sync.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Util.showAlert(context, "Alert", "File doesn't exists, please copy the file to specified folder and sync.");
        }
    }

}