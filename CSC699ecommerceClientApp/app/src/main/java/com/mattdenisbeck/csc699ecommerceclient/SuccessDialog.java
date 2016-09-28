package com.mattdenisbeck.csc699ecommerceclient;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class SuccessDialog extends DialogFragment {

    private AlertDialog.Builder builder;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        builder = new AlertDialog.Builder(getActivity());
        AlertDialog.Builder builder = this.builder.setMessage("Success! Your order has been submitted.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(((Dialog) dialog).getContext(), MainActivity.class);
                        startActivity(intent);
                    }
                });

        // Create the AlertDialog object and return it
        return this.builder.create();
    }
}
