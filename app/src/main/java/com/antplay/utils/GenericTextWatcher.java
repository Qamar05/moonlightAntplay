package com.antplay.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.antplay.R;

public class GenericTextWatcher implements TextWatcher {

    private final EditText[] editText;
    private View view;
    public GenericTextWatcher(View view, EditText editText[])
    {
        this.editText = editText;
        this.view = view;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        String text = editable.toString();
        switch (view.getId()) {

            case R.id.otp_edit_box1:
                if (text.length() == 1)
                    editText[1].requestFocus();
                break;
            case R.id.otp_edit_box2:

                if (text.length() == 1)
                    editText[2].requestFocus();
                else if (text.length() == 0)
                    editText[0].requestFocus();
                break;
            case R.id.otp_edit_box3:
                if (text.length() == 1)
                    editText[3].requestFocus();
                else if (text.length() == 0)
                    editText[1].requestFocus();
                break;
            case R.id.otp_edit_box4:
                if (text.length() == 1)
                    editText[4].requestFocus();
                else if (text.length() == 0)
                    editText[2].requestFocus();
                break;
            case R.id.otp_edit_box5:
                if (text.length() == 1)
                    editText[5].requestFocus();
                else if (text.length() == 0)
                    editText[3].requestFocus();
                break;
            case R.id.otp_edit_box6:
                if (text.length() == 0)
                    editText[4].requestFocus();
                break;
        }
    }
}
