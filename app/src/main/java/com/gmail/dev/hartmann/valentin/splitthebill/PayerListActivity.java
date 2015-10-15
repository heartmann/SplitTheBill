package com.gmail.dev.hartmann.valentin.splitthebill;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.gmail.dev.hartmann.valentin.splitthebill.payment.CumulativeDebtNotFittingException;
import com.gmail.dev.hartmann.valentin.splitthebill.payment.DuplicatePayerException;
import com.gmail.dev.hartmann.valentin.splitthebill.payment.EvenPayment;
import com.gmail.dev.hartmann.valentin.splitthebill.payment.NoSuchPayerException;
import com.gmail.dev.hartmann.valentin.splitthebill.payment.Payment;
import com.gmail.dev.hartmann.valentin.splitthebill.payment.UnevenPayment;
import com.google.gson.Gson;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;

public class PayerListActivity extends ActionBarBaseActivity
        implements AddPayerDialogFragment.AddPayerDialogListener{

    private Payment payment;
    // caches (payment instanceof EvenPayment)
    private boolean even;
    private PayerAdapter payerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            Bundle intentExtras = getIntent().getExtras();
            if (intentExtras != null) {
                payment = new EvenPayment(intentExtras.getLong("bill"));
                even = true;
            } else {
                payment = new UnevenPayment();
                even = false;
            }
        } else {
            even = savedInstanceState.getBoolean("even");
            if (even) {
                payment = new Gson().fromJson(
                        savedInstanceState.getString("payment"), EvenPayment.class);
            } else {
                payment = new Gson().fromJson(
                        savedInstanceState.getString("payment"), UnevenPayment.class);
            }
        }

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_payer_list);
        RecyclerView recyclerPayerList = (RecyclerView) findViewById(R.id.recycler_payer_list);
        recyclerPayerList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerPayerList.setLayoutManager(llm);

        payerAdapter = new PayerAdapter(payment, this);
        recyclerPayerList.setAdapter(payerAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("even", even);
        outState.putString("payment", new Gson().toJson(payment));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_payer_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add) {
            DialogFragment addPayerDialog = new AddPayerDialogFragment();
            Bundle args = new Bundle();
            args.putBoolean("even", even);
            addPayerDialog.setArguments(args);
            addPayerDialog.show(getFragmentManager(), "addPayer");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void addPayer(String name, long paid) throws DuplicatePayerException {
        payment.addPayer(name, paid);
        payerAdapter.notifyItemInserted(payment.getPayers().size() - 1);
    }

    @Override
    public void addPayer(String name, long individualBill, long paid)
            throws DuplicatePayerException {
        payment.addPayer(name, individualBill, paid);
        payerAdapter.notifyItemInserted(payment.getPayers().size() - 1);
    }

    @Override
    public void removePayer(String name) {
        int pos = payment.removePayer(name);
        payerAdapter.notifyItemRemoved(pos);
    }

    @Override
    public void editPayer(String oldName, String newName, long newPaid)
            throws NoSuchPayerException, DuplicatePayerException {
        int pos = payment.editPayer(oldName, newName, newPaid);
        payerAdapter.notifyItemChanged(pos);
    }

    @Override
    public void editPayer(String oldName, String newName, long newIndividualBill, long newPaid)
            throws NoSuchPayerException, DuplicatePayerException {
        int pos = payment.editPayer(oldName, newName, newIndividualBill, newPaid);
        payerAdapter.notifyItemChanged(pos);
    }

    public void showTransfers(View v) {
        Intent i = new Intent(this, TransferListActivity.class);
        try {
            Map<String, Map<String, Long>> transfers = payment.getTransfersAsStrings();
            // In the following: Convert the Map to a List which is needed for accessing items via
            // index by the RecyclerView in the TransferListActivity. Additionally, it ensures that
            // the order of the elements is stored, because Android would convert the underlying
            // TreeMap to a HashMap when put into an Intent.
            ArrayList<Map.Entry<String, ArrayList<Map.Entry<String, Long>>>> transfersList =
                    new ArrayList<>(transfers.size());
            for (String sender : transfers.keySet()) {
                Map<String, Long> receivers = transfers.get(sender);
                Map.Entry<String, ArrayList<Map.Entry<String, Long>>> senderTransfers =
                        new AbstractMap.SimpleImmutableEntry<>
                                (sender, new ArrayList<Map.Entry<String, Long>>(receivers.size()));
                for (String currReceiver : receivers.keySet()) {
                    senderTransfers.getValue().add(
                            new AbstractMap.SimpleImmutableEntry<>(
                                    currReceiver, receivers.get(currReceiver)));
                }
                transfersList.add(senderTransfers);
            }
            i.putExtra("transfers", transfersList);
            // code for activating transition
//            if (Build.VERSION.SDK_INT >= 16) {
//                startActivity(i, ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle());
//            } else {
//                startActivity(i);
//            }
            startActivity(i);
        } catch (CumulativeDebtNotFittingException e) {
            String errorMessage;
            if (e.isGreaterZero()) {
                errorMessage = getString(R.string.error_bill_too_high);
            } else {
                errorMessage = getString(R.string.error_paid_too_high);
            }
            Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
        }
    }
}
