package com.gmail.dev.hartmann.valentin.splitthebill;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends ActionBarBaseActivity
        implements SetBillDialogFragment.SetBillDialogListener {

    // bill == -1 means 'uneven' was chosen
    private long bill = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startEven(View v) {
        DialogFragment billDialog = new SetBillDialogFragment();
        billDialog.show(getFragmentManager(), "bill");
    }

    @Override
    public void setBill(long bill) {
        this.bill = bill;
        startPayerList();
    }

    public void startUneven(View v) {
        startPayerList();
    }

    private void startPayerList() {
        Intent i = new Intent(this, PayerListActivity.class);
        if (bill != -1) {
            i.putExtra("bill", bill);
        }
        // code for activating transition
//        if (Build.VERSION.SDK_INT >= 16) {
//            startActivity(i, ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle());
//        } else {
//            startActivity(i);
//        }
        startActivity(i);
        bill = -1;
    }
}
