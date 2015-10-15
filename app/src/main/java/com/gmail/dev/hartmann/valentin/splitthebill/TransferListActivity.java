package com.gmail.dev.hartmann.valentin.splitthebill;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Map;

public class TransferListActivity extends ActionBarBaseActivity {

    private ArrayList<Map.Entry<String, ArrayList<Map.Entry<String, Long>>>> transfers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            transfers = (ArrayList<Map.Entry<String, ArrayList<Map.Entry<String, Long>>>>)
                    getIntent().getSerializableExtra("transfers");
        } else {
            transfers = (ArrayList<Map.Entry<String, ArrayList<Map.Entry<String, Long>>>>)
                    savedInstanceState.getSerializable("transfers");
        }

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_transfer_list);
        RecyclerView recyclerPayerList = (RecyclerView) findViewById(R.id.recycler_transfer_list);
        recyclerPayerList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerPayerList.setLayoutManager(llm);

        TransferAdapter transferAdapter = new TransferAdapter(transfers);
        recyclerPayerList.setAdapter(transferAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("transfers", transfers);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // not declaring parent activity in manifest because when pressing the up button in the
        // ActionBar the state of PayerListActivity shall be restored instead of creating a new
        // instance
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
