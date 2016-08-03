package rkr.binatestation.eqsoft.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;

import rkr.binatestation.eqsoft.R;

public class OrderActivity extends AppCompatActivity {
    RecyclerView selectedProductsRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        selectedProductsRecyclerView = (RecyclerView) findViewById(R.id.AO_selectedProducts);

        selectedProductsRecyclerView.setLayoutManager(new LinearLayoutManager(selectedProductsRecyclerView.getContext()));
//        selectedProductsRecyclerView.setAdapter(new ProductAdapter(2));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.global_menu, menu);
        return true;
    }
}
