package com.miscell.lucky;

import android.app.Dialog;
import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class ConfirmDialog extends Dialog {
	
    private LinearLayout m_buttonLayout;											
    private float m_density;														

    private View.OnClickListener m_positiveListener;								
    private String m_positiveTextStr;												


    public ConfirmDialog(Context context) {
        super(context, R.style.FullHeightDialog);
        
        setCanceledOnTouchOutside(true);								           
        setContentView(R.layout.confirm_dialog_layout);							   

        m_buttonLayout = (LinearLayout) findViewById(R.id.button_layout);			
        m_density = getContext().getResources().getDisplayMetrics().density;		
    }
    
    @Override
    public void show() {
        int screenWidth = getContext().getResources().getDisplayMetrics().widthPixels;  

        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();			
        layoutParams.width = (int) (screenWidth * 0.6f);								
        getWindow().setAttributes(layoutParams);										

        super.show();
    }
    
    public ConfirmDialog setTitle(String title) {
    	
        TextView textView = (TextView) findViewById(R.id.dialog_title);				
        textView.setText(title);													
        textView.setVisibility(View.VISIBLE);										

        return this;
    }


    public ConfirmDialog setMessage(String message) {
    	
        TextView textView = (TextView) findViewById(R.id.dialog_message);			
        textView.setText(message);													
        textView.setVisibility(View.VISIBLE);										

        return this;
    }

   
    public ConfirmDialog setPositiveButton(String text, View.OnClickListener listener) {
        m_positiveListener = listener;
        m_positiveTextStr = text;
        m_buttonLayout.removeAllViews();

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        Button button = getButton(getContext(), text, listener);
        m_buttonLayout.addView(button, lp);

        return this;
    }

   
    public ConfirmDialog setNegativeButton(String text, View.OnClickListener listener) {
        m_buttonLayout.removeAllViews();
        m_buttonLayout.setWeightSum(2.f);

        Context context = getContext();

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, WRAP_CONTENT);
        lp.weight = 1.f;

        Button button1 = getButton(context, text, listener);
        m_buttonLayout.addView(button1, lp);

        if (null == listener) {
            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }

        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(1, MATCH_PARENT);
        View divider = new View(context);
        divider.setBackgroundColor(0xFFDDDDDD);
        m_buttonLayout.addView(divider, lp1);

        Button button2 = getButton(context, m_positiveTextStr, m_positiveListener);
        m_buttonLayout.addView(button2, lp);

        return this;
    }

   
    private Button getButton(Context context, String text, View.OnClickListener listener) {
        int padding = (int) (8 * m_density + .5f);

        Button button = new Button(context);
        button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15.f);
        button.setTextColor(0xFF333333);
        button.setText(text);
        button.setBackgroundResource(R.drawable.item_highlight_bkg);
        button.setPadding(0, padding, 0, padding);
        button.setOnClickListener(listener);

        return button;
    }
}