package com.example.workwork;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ViewByDate extends AppCompatActivity {
    private List<ConsumeItem> consumeItemList;
    private List<ConsumeItem> consumeItemList_sorted=new ArrayList<>();
    private RecyclerView DataListView;
    private ConsumeList_adapter adapter;
    private Toolbar toolbar;
    private  Spinner ChooseYear;
    private  Spinner ChooseMonth;
    private  Spinner ChooseDay;
    private   Button findButton;
    private TextView ValueSum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Activity_collector.addActivity(this);
        setContentView(R.layout.activity_view_by_date);
        init();
        setToolbar();
        consumeItemList= DataSupport.findAll(ConsumeItem.class);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        DataListView.setLayoutManager(layoutManager);
        adapter=new ConsumeList_adapter(consumeItemList_sorted);
        DataListView.setAdapter(adapter);
        initDatechoose();
        adapter.setOnItemClickListener((v,position)->{
            ShowEditDialog(position);
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Activity_collector.removeActivity(this);
    }
    private void  ShowEditDialog(int position) {
        LayoutInflater factory = LayoutInflater.from(getBaseContext());
        final View DialogEditView = factory.inflate(R.layout.edit_dialog_layout,null);
        //内容编辑
        TextInputEditText EditText=DialogEditView.findViewById(R.id.EditText);
        TextInputEditText EditValue=DialogEditView.findViewById(R.id.EditValue);
        Spinner EditTypes=DialogEditView.findViewById(R.id.EditTypes);
        Button DeleteButton=DialogEditView.findViewById(R.id.Delete_button);
        //年月日编辑
        Spinner EditYear=DialogEditView.findViewById(R.id.EditYear);
        Spinner EditMonth=DialogEditView.findViewById(R.id.EditMonth);
        Spinner EditDay=DialogEditView.findViewById(R.id.EditDay);
        ArrayAdapter<String> adapterYear;
        ArrayAdapter<String> adapterDay;
        ArrayList<String> dataYear = new ArrayList<String>();
        ArrayList<String> dataDay = new ArrayList<String>();
        //初始化年月日编辑栏
        Calendar Time = consumeItemList.get(position).getTime();//从对应项中获取时间
        for (int i = 0; i < 40; i++) {
            dataYear.add("" + (Time.get(Calendar.YEAR) - 20 + i));//将spinner的选项设置为前后20年
        }
        adapterYear = new ArrayAdapter<String>(this, R.layout.time_edit_spinner_item, dataYear);
        adapterYear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        EditYear.setAdapter(adapterYear);
        EditYear.setSelection(20);// 默认选中今年
        adapterDay = new ArrayAdapter<String>(this, R.layout.time_edit_spinner_item, dataDay);
        adapterDay.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        EditDay.setAdapter(adapterDay);
        EditDay.setSelection(Time.get(Calendar.DATE));
        EditMonth.setSelection(Time.get(Calendar.MONTH));
        EditMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dataDay.clear();
                Calendar cal = Calendar.getInstance();//利用calender类计算每个月天数
                cal.set(Calendar.YEAR, Integer.valueOf(EditYear.getSelectedItem().toString()));
                cal.set(Calendar.MONTH,position);
                cal.set(Calendar.DATE,1);
                int dayofm = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                for (int i = 1; i <= dayofm; i++) {
                    dataDay.add("" + (i < 10 ? "0" + i : i));
                }
                adapterDay.notifyDataSetChanged();
                updateList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //初始化内容编辑栏
        EditText.setText(consumeItemList_sorted.get(position).getContent());
        EditTypes.setSelection(consumeItemList_sorted.get(position).getType());
        EditValue.setText(String.valueOf(consumeItemList_sorted.get(position).getValue()));
        //

        AlertDialog.Builder alterDiaglog = new AlertDialog.Builder(ViewByDate.this);
        alterDiaglog.setView(R.layout.input_dialog_layout)
                .setTitle("修改")
                .setView(DialogEditView)//加载自定义布局
                .setPositiveButton("完成", (dialog,whichButton)->{
                    int type_get=EditTypes.getSelectedItemPosition();
                    float value_get=Float.valueOf(EditValue.getText().toString());
                    String text_get=EditText.getText().toString();
                    Time.set(Calendar.YEAR,Integer.valueOf(EditYear.getSelectedItem().toString()));
                    Time.set(Calendar.MONTH,Integer.valueOf(EditMonth.getSelectedItem().toString())-1);
                    Time.set(Calendar.DATE,Integer.valueOf(EditDay.getSelectedItem().toString()));
                    if(!"".equals(text_get)&&!"".equals(value_get)) {
                        consumeItemList_sorted.get(position).setContent(text_get);
                        consumeItemList_sorted.get(position).setValue(value_get);
                        consumeItemList_sorted.get(position).setType(type_get);
                        consumeItemList_sorted.get(position).setTime(Time);
                        consumeItemList_sorted.get(position).save();
                        adapter.notifyItemRangeChanged(0, consumeItemList.size());
                        DataListView.scrollToPosition(position);
                        EditText.setText("");
                        EditValue.setText("");;
                    }
                    //更新recyclerView
                })
                .setNegativeButton("取消",(DialogInterface dialog, int whichButton)->{
                }).setCancelable(false).create();
        AlertDialog dialog=alterDiaglog.show();
        DeleteButton.setOnClickListener((v)->{
            consumeItemList_sorted.get(position).delete();
            consumeItemList_sorted.remove(position);
            adapter.notifyItemRemoved(position);
            dialog.dismiss();
        });

    }
    private  void init() {
        DataListView = findViewById(R.id.ListView_Date);
        findButton = findViewById(R.id.findButton);
        ValueSum=findViewById(R.id.valueSum);
        toolbar=findViewById(R.id.ToolBar_date);
    }
    private  void initDatechoose() {
        ChooseDay = findViewById(R.id.ChooseDay);
        ChooseYear = findViewById(R.id.ChooseYear);
        ChooseMonth = findViewById(R.id.ChooseMonth);


        Calendar today = Calendar.getInstance();
        ArrayAdapter<String> adapterYear;
        ArrayAdapter<String> adapterDay;
        ArrayList<String> dataYear = new ArrayList<String>();
        ArrayList<String> dataDay = new ArrayList<String>();
        //初始化年月日编辑栏
        Calendar Time = today;//从对应项中获取时间
        for (int i = 0; i < 40; i++) {
            dataYear.add("" + (Time.get(Calendar.YEAR) - 20 + i));//将spinner的选项设置为前后20年
        }
        adapterYear = new ArrayAdapter<String>(this, R.layout.time_edit_spinner_item, dataYear);
        adapterYear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ChooseYear.setAdapter(adapterYear);
        ChooseYear.setSelection(20);// 默认选中今年

        //初始化日期和月份
        adapterDay = new ArrayAdapter<String>(this, R.layout.time_edit_spinner_item, dataDay);
        adapterDay.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ChooseDay.setAdapter(adapterDay);
        int dayofm = Time.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = 1; i <= dayofm; i++) {
            dataDay.add("" + (i < 10 ? "0" + i : i));
        }
        ChooseDay.setSelection(Time.get(Calendar.DATE));
        ChooseMonth.setSelection(Time.get(Calendar.MONTH));


        //绑定监听器实现修改年份和月份后日期改变
        ChooseMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dataDay.clear();
                Calendar cal = Calendar.getInstance();//利用calender类计算每个月天数
                cal.set(Calendar.YEAR, Integer.valueOf(ChooseYear.getSelectedItem().toString()));
                cal.set(Calendar.MONTH, position);
                cal.set(Calendar.DATE, 1);
                int dayofm = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                for (int i = 1; i <= dayofm; i++) {
                    dataDay.add("" + (i < 10 ? "0" + i : i));
                }
                dataDay.add("all");
                adapterDay.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ChooseYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dataDay.clear();
                Calendar cal = Calendar.getInstance();//利用calender类计算每个月天数
                cal.set(Calendar.YEAR, Integer.valueOf(ChooseYear.getSelectedItem().toString()));
                cal.set(Calendar.MONTH, Integer.valueOf(ChooseMonth.getSelectedItem().toString())-1);
                cal.set(Calendar.DATE, 1);
                int dayofm = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                for (int i = 1; i <= dayofm; i++) {
                    dataDay.add("" + (i < 10 ? "0" + i : i));
                }
                dataDay.add("all");
                adapterDay.notifyDataSetChanged();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        }
        );


        findButton.setOnClickListener((v)->{
            updateList();
        });
    }
    private void updateList() {
        int count = consumeItemList_sorted.size();
        float sum = 0;
        float limit = 0;
        boolean monthly=false;
        SharedPreferences sp = getSharedPreferences("setting", 0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        consumeItemList_sorted.clear();
        consumeItemList.clear();
        consumeItemList = DataSupport.findAll(ConsumeItem.class);
        Calendar Time = Calendar.getInstance();
        Time.set(Calendar.YEAR, Integer.valueOf(ChooseYear.getSelectedItem().toString()));
        Time.set(Calendar.MONTH, Integer.valueOf(ChooseMonth.getSelectedItem().toString()) - 1);

        //对于是否统计整个月数据进行判断
        if (!ChooseDay.getSelectedItem().toString().equals("all")) {
            Time.set(Calendar.DATE, Integer.valueOf(ChooseDay.getSelectedItem().toString()));
            limit = sp.getFloat("DayLimit", 0);
        } else {
            sdf = new SimpleDateFormat("yyyy-MM");
            limit = sp.getFloat("MonthLimit", 0);
            monthly=true;
        }


        for (ConsumeItem I : consumeItemList) {
            if (sdf.format(I.getTime().getTime()).equals(sdf.format(Time.getTime().getTime())))
                consumeItemList_sorted.add(I);
        }

        adapter.notifyItemRangeChanged(0, adapter.getItemCount());
        adapter.notifyItemRangeChanged(0, count);

        for (ConsumeItem I : consumeItemList_sorted) {
            sum += I.getValue();
        }
        String base=sdf.format(Time.getTime().getTime()) + "消费金额:" + String.valueOf(sum);
        if(monthly) {
            Spinner editday=findViewById(R.id.ChooseDay);
            base =base+ "本月日均消费：" + sum /(editday.getAdapter().getCount()-1);
        }
        if (sum <= limit)
                {
                    ValueSum.setText(base);
                    ValueSum.setTextColor(Color.parseColor("#0F0F0F"));
                }
            else if(sum>limit)
             {
                ValueSum.setText(base+"超出了限额"+(sum-limit));
                ValueSum.setTextColor( Color.parseColor("#FF0000"));
                }

        }

    private  void setToolbar() {
        toolbar.setTitle("按时间统计");//设置主标题
        toolbar.setNavigationOnClickListener((V)->{
            finish();
        });



    }

    }
