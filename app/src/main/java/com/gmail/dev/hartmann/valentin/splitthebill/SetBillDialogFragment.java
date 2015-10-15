package com.gmail.dev.hartmann.valentin.splitthebill;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DecimalFormatSymbols;

public class SetBillDialogFragment extends DialogFragment{

    public interface SetBillDialogListener {
        void setBill(long bill);
    }

    private View numberEntryView;
    private SetBillDialogListener billListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // verify that the host activity implements the callback interface
        try {
            billListener = (SetBillDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity + " must implement SetBillDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        numberEntryView = inflater.inflate(R.layout.fragment_set_bill_dialog, null);

        EditText billFirstEdit = (EditText) numberEntryView.findViewById(R.id.editText_paid_amount_first);
        EditText billSecondEdit = (EditText) numberEntryView.findViewById(R.id.editText_paid_amount_second);
        LayoutUtils.setLongNumberEntryWidth(billFirstEdit, billSecondEdit);

        // localize the decimal separator and the currency symbol
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        ((TextView) numberEntryView.findViewById(R.id.textView_decimal_separator))
                .setText(String.valueOf(dfs.getDecimalSeparator()));
        ((TextView) numberEntryView.findViewById(R.id.textView_currency_symbol))
                .setText(dfs.getCurrencySymbol());

        builder.setView(numberEntryView);
        builder.setMessage(R.string.question_bill);
        builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setBill();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getDialog().cancel();
            }
        });

        Dialog dialog = builder.create();
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    setBill();
                    return true;
                }
                return false;
            }
        });

        return dialog;
    }

    private void setBill() {
        EditText billFirstView = (EditText) numberEntryView.findViewById(R.id.editText_paid_amount_first);
        EditText billSecondView = (EditText) numberEntryView.findViewById(R.id.editText_paid_amount_second);
        String billFirstString = billFirstView.getText().toString();
        String billSecondString = billSecondView.getText().toString();
        long billFirst = (billFirstString.isEmpty()) ? 0 : Long.parseLong(billFirstString);
        long billSecond = (billSecondString.isEmpty()) ? 0 : Long.parseLong(billSecondString);
        if (billSecond < 10) {
            billSecond *= 10;
        }
        long bill = 100 * billFirst + billSecond;

        getDialog().cancel();

        billListener.setBill(bill);
    }
}
