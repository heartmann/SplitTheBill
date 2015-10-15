package com.gmail.dev.hartmann.valentin.splitthebill;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gmail.dev.hartmann.valentin.splitthebill.payment.Payment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TransferAdapter extends RecyclerView.Adapter<TransferAdapter.TransferViewHolder>{

    private List<Map.Entry<String, ArrayList<Map.Entry<String, Long>>>> transfers;

    public TransferAdapter(List<Map.Entry<String, ArrayList<Map.Entry<String, Long>>>> transfers) {
        this.transfers = transfers;
    }

    @Override
    public int getItemCount() {
        return transfers.size();
    }

    @Override
    public void onBindViewHolder(TransferViewHolder transferViewHolder, int position) {
        String sender = transfers.get(position).getKey();
        transferViewHolder.senderName.setText(sender);

        for (Map.Entry<String, Long> currTransfer: transfers.get(position).getValue()) {
            ViewGroup parent = transferViewHolder.parentGroup;
            ViewGroup receiverView = (ViewGroup) LayoutInflater.from(
                    transferViewHolder.parentGroup.getContext())
                    .inflate(R.layout.transfer_receiver, parent, false);
            parent.addView(receiverView);

            ((TextView) receiverView.findViewById(R.id.textView_transfer_receiver_name))
                    .setText(currTransfer.getKey());
            String amount = Payment.longToCurrency(currTransfer.getValue());
            ((TextView) receiverView.findViewById(R.id.textView_transfer_amount)).setText(amount);
        }
    }

    @Override
    public TransferViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_entry_transfer, parent, false);
        return new TransferViewHolder(itemView);
    }

    public class TransferViewHolder extends RecyclerView.ViewHolder {
        protected TextView senderName;
        protected ViewGroup parentGroup;

        public TransferViewHolder(View itemView) {
            super(itemView);
            senderName = (TextView) itemView.findViewById(R.id.textView_transfer_sender_name);
            parentGroup = (ViewGroup) itemView.findViewById(R.id.linearLayout_transfer_list_parent);
        }
    }
}
