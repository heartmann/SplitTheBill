package com.gmail.dev.hartmann.valentin.splitthebill;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.gmail.dev.hartmann.valentin.splitthebill.payment.DuplicatePayerException;
import com.gmail.dev.hartmann.valentin.splitthebill.payment.NoSuchPayerException;
import com.gmail.dev.hartmann.valentin.splitthebill.payment.Payer;

import java.text.DecimalFormatSymbols;

public class AddPayerDialogFragment extends DialogFragment {

    private Payer payer;
    private AddPayerDialogListener addListener;
    private boolean even;
    private boolean edit; // Is the dialog opened to add or to edit a Payer?
    private EditText nameEdit;
    private EditText paidAmountFirstEdit;
    private EditText paidAmountSecondEdit;
    private EditText individualBillAmountFirstEdit;
    private EditText individualBillAmountSecondEdit;

    public interface AddPayerDialogListener {
        void addPayer(String name, long paid) throws DuplicatePayerException;
        void addPayer(String name, long individualBill, long paid) throws DuplicatePayerException;
        void removePayer(String name);
        void editPayer(String oldName, String newName, long newPaid)
                throws NoSuchPayerException, DuplicatePayerException;
        void editPayer(String oldName, String newName, long newIndividualBill, long newPaid)
                throws NoSuchPayerException, DuplicatePayerException;
    }

    public static class DuplicatePayerDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            String name = getArguments().getString("name");
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(getResources().getString(R.string.warning_duplicate_payer_first)
                    + name +
                    getResources().getString(R.string.warning_duplicate_payer_second));
            builder.setNeutralButton(R.string.OK, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getDialog().cancel();
                }
            });
            return builder.create();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // verify that the host activity implements the callback interface
        try {
            addListener = (AddPayerDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity + " must implement AddPayerDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Bundle args = getArguments();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        even = args.getBoolean("even");

        if (args.containsKey("payerName")) {
            edit = true;
            builder.setTitle(R.string.dialog_title_edit_payer);
            payer = new Payer(args.getString("payerName"), args.getLong("payerIndividualBill"),
                    args.getLong("payerPaid"));
        } else {
            edit = false;
            builder.setTitle(R.string.dialog_title_add_payer);
        }

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View payerDataView;
        if (even) {
            payerDataView = inflater.inflate(R.layout.fragment_add_payer_even_dialog, null);
        } else {
            payerDataView = inflater.inflate(R.layout.fragment_add_payer_uneven_dialog, null);
            individualBillAmountFirstEdit = (EditText) payerDataView
                    .findViewById(R.id.editText_individualBill_amount_first);
            individualBillAmountSecondEdit = (EditText) payerDataView
                    .findViewById(R.id.editText_individualBill_amount_second);
            LayoutUtils.setLongNumberEntryWidth(
                    individualBillAmountFirstEdit, individualBillAmountSecondEdit);
        }
        nameEdit = (EditText) payerDataView.findViewById(R.id.editText_payer_name);
        paidAmountFirstEdit = (EditText) payerDataView
                .findViewById(R.id.editText_paid_amount_first);
        paidAmountSecondEdit = (EditText) payerDataView
                .findViewById(R.id.editText_paid_amount_second);
        LayoutUtils.setLongNumberEntryWidth(paidAmountFirstEdit, paidAmountSecondEdit);
        builder.setView(payerDataView);

        // localize the decimal separator and the currency symbol
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        ((TextView) payerDataView.findViewById(R.id.textView_decimal_separator_paid))
                .setText(String.valueOf(dfs.getDecimalSeparator()));
        ((TextView) payerDataView.findViewById(R.id.textView_currency_symbol_paid))
                .setText(dfs.getCurrencySymbol());
        if (!even) {
            ((TextView) payerDataView.findViewById(R.id.textView_decimal_separator_individualBill))
                    .setText(String.valueOf(dfs.getDecimalSeparator()));
            ((TextView) payerDataView.findViewById(R.id.textView_currency_symbol_individualBill))
                    .setText(dfs.getCurrencySymbol());
        }

        if (edit) {
            nameEdit.setText(payer.getName());
            initEditTexts(payer.getPaid(), paidAmountFirstEdit, paidAmountSecondEdit);
            if (!even) {
                initEditTexts(payer.getIndividualBill(),
                        individualBillAmountFirstEdit, individualBillAmountSecondEdit);
            }
        }

        builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addPayer();
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
                if ((event.getAction() == KeyEvent.ACTION_UP)
                        && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    addPayer();
                    return true;
                }
                return false;
            }
        });

        return dialog;
    }

    private void addPayer() {
        String name = nameEdit.getText().toString();

        String paidAmountFirstString = paidAmountFirstEdit.getText().toString();
        String paidAmountSecondString = paidAmountSecondEdit.getText().toString();
        long paidFirst = (paidAmountFirstString.isEmpty()) ? 0 :
                Long.parseLong(paidAmountFirstString);
        long paidSecond = (paidAmountSecondString.isEmpty()) ? 0 :
                Long.parseLong(paidAmountSecondString);
        // second condition to make sure the number doesn't have a leading 0
        if (paidSecond < 10 && paidAmountSecondString.length() == 1) {
            paidSecond *= 10;
        }
        long paid = 100 * paidFirst + paidSecond;

        DialogFragment dupPayerWarning = new DuplicatePayerDialogFragment();
        Bundle args = new Bundle();
        args.putString("name", name);
        dupPayerWarning.setArguments(args);

        if (even) {
            try {
                if (edit) {
                    Log.i("ATTENTION", "old: " + payer.getName() + "new: " + name + " " + paid);
                    addListener.editPayer(payer.getName(), name, paid);
                } else {
                    addListener.addPayer(name, paid);
                }
                getDialog().cancel();
            } catch (DuplicatePayerException dupPayerException) {
                dupPayerWarning.show(getFragmentManager(), "duplicatePayerWarning");
            }
        } else {
            String individualBillAmountFirstString = individualBillAmountFirstEdit
                    .getText().toString();
            String individualBillAmountSecondString = individualBillAmountSecondEdit
                    .getText().toString();
            int indBillFirst = (individualBillAmountFirstString.isEmpty()) ? 0 :
                    Integer.parseInt(individualBillAmountFirstString);
            int indBillSecond = (individualBillAmountSecondString.isEmpty()) ? 0 :
                    Integer.parseInt(individualBillAmountSecondString);
            // second condition to make sure the number doesn't have a leading 0
            if (indBillSecond < 10 && individualBillAmountSecondString.length() == 1) {
                indBillSecond *= 10;
            }
            long indBill = 100 * indBillFirst + indBillSecond;
            try {
                if (edit) {
                    addListener.editPayer(payer.getName(), name, indBill, paid);
                } else {
                    addListener.addPayer(name, indBill, paid);
                }
                getDialog().cancel();
            } catch (DuplicatePayerException dupPayerException) {
                dupPayerWarning.show(getFragmentManager(), "duplicatePayerWarning");
            }
        }
    }

    private static void initEditTexts(long value, EditText editTextFirst, EditText editTextSecond) {
        long first = value / 100;
        long second = value % 100;
        editTextFirst.setText(String.valueOf(first));
        if (second < 10) {
            editTextSecond.setText("0" + second);
        } else {
            editTextSecond.setText(String.valueOf(second));
        }
    }
}
