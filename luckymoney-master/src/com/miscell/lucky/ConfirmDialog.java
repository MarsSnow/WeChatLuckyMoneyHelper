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

//确认对话框
public class ConfirmDialog extends Dialog {
	
    private LinearLayout m_buttonLayout;											//线性布局
    private float m_density;														//屏幕密度

    private View.OnClickListener m_positiveListener;								//监听器
    private String m_positiveTextStr;												//位置文本str

    //构造函数
    public ConfirmDialog(Context context) {
        super(context, R.style.FullHeightDialog);
        
        setCanceledOnTouchOutside(true);								            //点击屏幕外围时退出
        setContentView(R.layout.confirm_dialog_layout);							    //设置布局文件

        m_buttonLayout = (LinearLayout) findViewById(R.id.button_layout);			//获得线性布局
        m_density = getContext().getResources().getDisplayMetrics().density;		//获得屏幕密度
    }
    
    @Override
    public void show() {
        int screenWidth = getContext().getResources().getDisplayMetrics().widthPixels;  //获得屏幕宽度

        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();			//获得布局参数
        layoutParams.width = (int) (screenWidth * 0.6f);								//设置布局宽度
        getWindow().setAttributes(layoutParams);										//设置布局参数

        super.show();
    }
    
    //设置标题内容
    public ConfirmDialog setTitle(String title) {
    	
        TextView textView = (TextView) findViewById(R.id.dialog_title);				//获得对话框标题控件
        textView.setText(title);													//设置标题内容
        textView.setVisibility(View.VISIBLE);										//设置为可见状态

        return this;
    }

    //设置消息
    public ConfirmDialog setMessage(String message) {
    	
        TextView textView = (TextView) findViewById(R.id.dialog_message);			//获得对话框内容控件
        textView.setText(message);													//设置内容
        textView.setVisibility(View.VISIBLE);										//设置为可见状态

        return this;
    }

    //设置确定按钮
    public ConfirmDialog setPositiveButton(String text, View.OnClickListener listener) {
        m_positiveListener = listener;
        m_positiveTextStr = text;
        m_buttonLayout.removeAllViews();

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        Button button = getButton(getContext(), text, listener);
        m_buttonLayout.addView(button, lp);

        return this;
    }

    //设置取消按钮
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

    //获得按钮
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
