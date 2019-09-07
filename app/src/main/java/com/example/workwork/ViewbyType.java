package com.example.workwork;

import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toolbar;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ViewbyType extends AppCompatActivity {
    private List<ConsumeItem> consumeItemList;
    private List<ConsumeItem> consumeItemList_sorted=new ArrayList<>();
    private RecyclerView TYPEListView;
    private ConsumeList_adapter adapter;
    private  Spinner ChooseType;
    private TextView ValueSum;
    private  android.support.v7.widget.Toolbar toolbar;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Activity_collector.addActivity(this);
        setContentView(R.layout.activity_viewby_type);
        init();
        setToolbar();
        consumeItemList= DataSupport.findAll(ConsumeItem.class);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        TYPEListView.setLayoutManager(layoutManager);
        adapter=new ConsumeList_adapter(consumeItemList_sorted);
        TYPEListView.setAdapter(adapter);
        adapter.setOnItemClickListener((v,position)->{
            ShowEditDialog(position);
        });
        initTypechoose();
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

        AlertDialog.Builder alterDiaglog = new AlertDialog.Builder(ViewbyType.this);
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
                        TYPEListView.scrollToPosition(position);
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
        TYPEListView = findViewById(R.id.ListView_Type);
        ValueSum=findViewById(R.id.SumValue);
        ChooseType=findViewById(R.id.ChooseType);
        toolbar=findViewById(R.id.ToolBar_type);
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private  void setToolbar() {
        toolbar.setTitle("按类型统计");//设置主标题
        toolbar.setNavigationOnClickListener((V) -> {
            finish();
        });
    }
    private  void initTypechoose()
    {
        ChooseType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private void updateList() {
        int count=consumeItemList_sorted.size();
        float sum=0;
        consumeItemList_sorted.clear();
        for (ConsumeItem I : consumeItemList) {
            if (ChooseType.getSelectedItemPosition()==I.getType())
                consumeItemList_sorted.add(I);
        }
        consumeItemList.clear();
        consumeItemList=DataSupport.findAll(ConsumeItem.class);
        adapter.notifyDataSetChanged();
        for (ConsumeItem I : consumeItemList_sorted) {
            sum += I.getValue();
        }
    }
}
