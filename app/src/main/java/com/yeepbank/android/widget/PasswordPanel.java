package com.yeepbank.android.widget;

import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import com.yeepbank.android.R;

import java.util.ArrayList;

/**
 * Created by WW on 2016/3/10.
 */
public class PasswordPanel implements View.OnClickListener,PopupWindow.OnDismissListener{

    private static Context mContext;
    private View passwordView;
    private EditText firstEdit,secEdit,thEdit,fourEdit,fiveEdit,sixEdit,sevenEdit;
    private InputMethodManager inputMethodManager;
    private ArrayList<EditText> editTexts = new ArrayList<EditText>();
    private OnInputCompleted onInputCompleted;
    private StringBuilder passwordStr = new StringBuilder();
    private PopupWindow passwordWindow;
    public static PasswordPanel passwordPanel;


    public static PasswordPanel getInstances(Context context){
        synchronized (PasswordPanel.class){
            if(passwordPanel == null){
                passwordPanel = new PasswordPanel();
            }
            mContext = context;
            return passwordPanel;
        }

    }



    public PopupWindow createPasswordWindow() {

        passwordView = LayoutInflater.from(mContext).inflate(R.layout.password_panel,null);
        passwordWindow = new PopupWindow(passwordView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        passwordWindow.setFocusable(true);
        passwordWindow.setOnDismissListener(this);
        passwordWindow.setAnimationStyle(R.style.exist_style);
        passwordWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);

        firstEdit = (EditText) passwordView.findViewById(R.id.first);
        secEdit = (EditText) passwordView.findViewById(R.id.second);
        thEdit = (EditText) passwordView.findViewById(R.id.third);
        fourEdit = (EditText) passwordView.findViewById(R.id.fourth);
        fiveEdit = (EditText) passwordView.findViewById(R.id.firve);
        sixEdit = (EditText) passwordView.findViewById(R.id.six);

        sevenEdit = (EditText) passwordView.findViewById(R.id.seven);

        firstEdit.setOnClickListener(this);
        secEdit.setOnClickListener(this);
        thEdit.setOnClickListener(this);
        fourEdit.setOnClickListener(this);
        fiveEdit.setOnClickListener(this);
        sixEdit.setOnClickListener(this);
        passwordView.findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordWindow.dismiss();
            }
        });
        passwordView.setOnTouchListener(touchListener);
        passwordWindow.setContentView(passwordView);

        return passwordWindow;
    }

    public void show(View view){
        passwordWindow.showAsDropDown(view);
        editTexts.add(firstEdit);editTexts.add(secEdit);editTexts.add(thEdit);
        editTexts.add(fourEdit);editTexts.add(fiveEdit);editTexts.add(sixEdit);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                firstEdit.setFocusable(true);
                sevenEdit.addTextChangedListener(textWatcher);
                inputMethodManager.showSoftInput(sevenEdit, 0);

            }
        }, 500);
    }

        View.OnTouchListener touchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                inputMethodManager.showSoftInput(sevenEdit, 0);
                return false;
            }
        };


    @Override
    public void onClick(View v) {
        inputMethodManager.showSoftInput(sevenEdit, 0);
    }

    public void setOnInputCompleted(OnInputCompleted onInputCompleted) {
        this.onInputCompleted = onInputCompleted;
    }

    @Override
    public void onDismiss() {
        sevenEdit.removeTextChangedListener(textWatcher);
        sevenEdit.setText("");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                passwordStr.delete(0, passwordStr.length());
            }
        },1000);

        for (int i = 0; i < editTexts.size(); i++) {
            editTexts.get(i).setText("");
        }
        editTexts.clear();

    }
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(before == 0){
                editTexts.get(start).setText(String.valueOf(s.charAt(start)));
                passwordStr.append(String.valueOf(s.charAt(start)));
            }else {
                editTexts.get(start).setText("");
                passwordStr.delete(start,start+1);
            }
            if(!editTexts.get(editTexts.size() - 1).getText().toString().equals("")){
                if(onInputCompleted != null){

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            passwordWindow.dismiss();
                        }
                    }, 300);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onInputCompleted.onCompleted(passwordStr.toString());
                        }
                    },800);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public interface OnInputCompleted{
        void onCompleted(String pass);
    }

}
