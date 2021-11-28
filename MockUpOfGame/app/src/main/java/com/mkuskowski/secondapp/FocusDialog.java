package com.mkuskowski.secondapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class FocusDialog extends DialogFragment
{
    //Custom dialog which will represent our changes made in new Update
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.FocusDialog_Text)
                .setPositiveButton(R.string.FocusDialog_ConfirmButt, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Action if needed ^^
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
