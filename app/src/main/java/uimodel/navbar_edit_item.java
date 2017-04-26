package uimodel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.hlkhjk_ok.weathercool.MyApplication;
import com.example.hlkhjk_ok.weathercool.R;

/**
 * Created by hlkhjk_ok on 17/4/13.
 */

public class navbar_edit_item extends LinearLayout implements View.OnClickListener{
    private EditText edittext;
    private ImageView back;
    private ImageView ok;

    public navbar_edit_item(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.navbar_edit_item, this);
        back = (ImageView) findViewById(R.id.back);
        ok   = (ImageView) findViewById(R.id.ok);
        ok.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.back:
                ((Activity)getContext()).finish();
                break;
            case R.id.ok:
                Toast.makeText(MyApplication.getContext(), "hello world", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }
}
