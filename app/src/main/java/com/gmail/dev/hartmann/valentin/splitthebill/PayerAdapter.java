package com.gmail.dev.hartmann.valentin.splitthebill;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.gmail.dev.hartmann.valentin.splitthebill.payment.EvenPayment;
import com.gmail.dev.hartmann.valentin.splitthebill.payment.Payer;
import com.gmail.dev.hartmann.valentin.splitthebill.payment.Payment;

public class PayerAdapter extends RecyclerView.Adapter<PayerAdapter.PayerViewHolder>{

    private Payment payment;
    // caches (payment instanceof EvenPayment)
    private boolean even;
    private PayerListActivity parent;

    public PayerAdapter(Payment payment, PayerListActivity parent) {
        this.payment = payment;
        even = payment instanceof EvenPayment;
        this.parent = parent;
    }

    @Override
    public int getItemCount() {
        return payment.getPayers().size();
    }

    @Override
    public void onBindViewHolder(PayerViewHolder payerViewHolder, int position) {
        Payer payer = payment.getPayers().get(position);
        payerViewHolder.name.setText(payer.getName());
        long paid = payer.getPaid();
        payerViewHolder.paid.setText("+" + Payment.longToCurrencyAmount(paid));
        payerViewHolder.editButton.setOnClickListener(new editClickListener(payer));

        if (!even) {
            long indBill = payer.getIndividualBill();
            ((UnevenPayerViewHolder) payerViewHolder).individualBill
                    .setText("-" + Payment.longToCurrencyAmount(indBill));
        }
    }

    @Override
    public PayerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (even) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_entry_payer_even, parent, false);
            return new PayerViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_entry_payer_uneven, parent, false);
            return new UnevenPayerViewHolder(itemView);
        }
    }

    public class PayerViewHolder extends RecyclerView.ViewHolder {

        protected ImageButton editButton;
        protected TextView name;
        protected TextView paid;

        public PayerViewHolder(View itemView) {
            super(itemView);
            editButton = (ImageButton) itemView.findViewById(R.id.button_edit_payer);
            name = (TextView) itemView.findViewById(R.id.textView_payer_name);
            paid = (TextView) itemView.findViewById(R.id.textView_payer_paid);
        }
    }

    public class UnevenPayerViewHolder extends PayerViewHolder {

        protected TextView individualBill;

        public UnevenPayerViewHolder(View itemView) {
            super(itemView);
            individualBill = (TextView) itemView.findViewById(R.id.textView_payer_individualBill);
        }
    }

    private class editClickListener implements View.OnClickListener {
        private Payer payer;

        public editClickListener(Payer payer) {
            this.payer = payer;
        }

        @Override
        public void onClick(View v) {
            PopupMenu editPopup = new PopupMenu(v.getContext(), v);
            editPopup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.action_edit) {
                        DialogFragment addPayerDialog = new AddPayerDialogFragment();
                        Bundle args = new Bundle();
                        args.putBoolean("even", even);
                        args.putString("payerName", payer.getName());
                        args.putLong("payerIndividualBill", payer.getIndividualBill());
                        args.putLong("payerPaid", payer.getPaid());
                        addPayerDialog.setArguments(args);
                        addPayerDialog.show(parent.getFragmentManager(), "editPayer");
                    } else { // item.getItemId() == R.id.action_delete
                        parent.removePayer(payer.getName());
                    }
                    return true;
                }
            });
            editPopup.getMenuInflater().inflate(R.menu.menu_popup_edit_payer, editPopup.getMenu());
            editPopup.show();
        }
    }
}
