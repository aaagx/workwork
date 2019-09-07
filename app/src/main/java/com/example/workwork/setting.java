package com.example.workwork;

import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toolbar;

public class setting extends AppCompatActivity {

    private EditText EditDayLimit;
    private EditText EditMonthLimit;
    private float DayLimit;
    private float MonthLimit;
    private Button Save;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private android.support.v7.widget.Toolbar toolbar;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        init();
        setToolbar();
        sp = getSharedPreferences("setting", 0);
        DayLimit = sp.getFloat("DayLimit", 1);
        MonthLimit = sp.getFloat("MonthLimit", 1);
        EditDayLimit.setText(String.valueOf(DayLimit));
        EditMonthLimit.setText(String.valueOf(MonthLimit));
        Save.setOnClickListener((v) -> {
            if (!EditDayLimit.getText().toString().equals("") && !EditMonthLimit.getText().toString().equals("")) {
                DayLimit = Float.valueOf(EditDayLimit.getText().toString());
                MonthLimit = Float.valueOf(EditMonthLimit.getText().toString());
                editor = sp.edit();
                editor.putFloat("DayLimit", DayLimit);
                editor.putFloat("MonthLimit", MonthLimit);
                Log.d("AAA", DayLimit + MonthLimit + "");
                editor.commit();
            }

        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        DayLimit = sp.getFloat("DayLimit", 1);
        MonthLimit = sp.getFloat("MonthLimit", 1);
        EditDayLimit.setText(String.valueOf(DayLimit));
        EditMonthLimit.setText(String.valueOf(MonthLimit));
    }

    private void init() {
        EditDayLimit = findViewById(R.id.EditDayLimit);
        EditMonthLimit = findViewById(R.id.EditMonthLimit);
        Save = findViewById(R.id.Save);
        toolbar=findViewById(R.id.ToolBar_setting);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setToolbar() {
        toolbar.setTitle("设置");//设置主标题
        toolbar.setNavigationOnClickListener((V) -> {
            finish();
        });

    }
}
