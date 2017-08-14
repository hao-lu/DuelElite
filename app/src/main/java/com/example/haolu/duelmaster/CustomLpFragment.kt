package com.example.haolu.duelmaster

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.support.v4.app.DialogFragment
import android.os.Bundle
import android.view.*

class CustomLpFragment : DialogFragment() {

    interface CustomLpDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment)
        fun onDialogNegativeClick(dialog: DialogFragment)
    }

    lateinit var mListener: CustomLpDialogListener

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = activity as CustomLpDialogListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException(activity.toString() + " must implement CustomLpDialogListener")
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = AlertDialog.Builder(context)
        dialog.setView(activity.layoutInflater.inflate(R.layout.fragment_custom_lp, null))
        dialog.setTitle("Enter life points")
                .setPositiveButton("Set", DialogInterface.OnClickListener { dialog, which -> mListener.onDialogPositiveClick(this)})
                .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which -> mListener.onDialogNegativeClick(this) })
        return dialog.create()
    }

    // Called after onCreateDialog
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val editText = container?.findViewById(R.id.edit_custom)
        // Open the keyboard for the user
        editText?.requestFocus()
        dialog.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

}