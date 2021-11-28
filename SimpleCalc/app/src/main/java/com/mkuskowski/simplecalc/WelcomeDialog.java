package com.mkuskowski.simplecalc;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class WelcomeDialog extends DialogFragment
{
    //Custom dialog which will represent our changes made in new Update
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Witaj w kalkulatorze")
                .setPositiveButton("Zaczynajmy", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Action if needed ^^
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
