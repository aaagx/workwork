package com.example.workwork;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class drawDate extends AppCompatActivity {
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_date);
        toolbar=findViewById(R.id.ToolBar_draw);
        setToolbar();

    }

    private void setToolbar() {

        toolbar.setTitle("设置");//设置主标题
        toolbar.setNavigationOnClickListener((V) -> {
            finish();
        });

    }
}
