package com.example.workwork;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.ColorSpace;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;
import org.litepal.util.Const;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<ConsumeItem> consumeItemList;
    private FloatingActionButton add;
    private RecyclerView MainListView;
    private ConsumeList_adapter adapter;
    private  Toolbar ToolBar;
    private SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Activity_collector.addActivity(this);
        setContentView(R.layout.activity_view_list);
        init();
        Connector.getDatabase();
        consumeItemList= DataSupport.findAll(ConsumeItem.class);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        MainListView.setLayoutManager(layoutManager);
        adapter=new ConsumeList_adapter(consumeItemList);
        MainListView.setAdapter(adapter);
        add.setOnClickListener((v)->{
            ShowInputDialog();
        });
        adapter.setOnItemClickListener((v,position)->{

            ShowEditDialog(position);
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Activity_collector.removeActivity(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        consumeItemList.clear();
        consumeItemList= DataSupport.findAll(ConsumeItem.class);
        adapter=new ConsumeList_adapter(consumeItemList);
        MainListView.setAdapter(adapter);
        adapter.setOnItemClickListener((v,position)->{

            ShowEditDialog(position);
        });
    }

    private void init()
    {
        MainListView =findViewById(R.id.MainListView);
        add=findViewById(R.id.AddButton);
        ToolBar=findViewById(R.id.ToolBar);
        setSupportActionBar(ToolBar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }
    @SuppressLint("RestrictedApi")
    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        if (menu != null) {
            if (menu.getClass() == MenuBuilder.class) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {

                }
            }
        }
        return super.onPrepareOptionsPanel(view,menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.Time_button:
                 intent=new Intent("android.intent.action.Date");
                startActivity(intent);
                break;
            case R.id.Type_button:
                 intent=new Intent("android.intent.action.Type");
                startActivity(intent);
                break;
            case R.id.Setting_button:
                intent=new Intent("android.intent.action.setting");
                startActivity(intent);
                break;
            case R.id.draw_button:
                intent=new Intent("android.intent.action.draw");
                startActivity(intent);
                break;
        }
        return  true;
    }

    private void  ShowInputDialog() {
        LayoutInflater factory = LayoutInflater.from(getBaseContext());
        final View DialogInputView = factory.inflate(R.layout.input_dialog_layout,null);
        AlertDialog.Builder alterDiaglog = new AlertDialog.Builder(MainActivity.this);
        //
        TextInputEditText InputText=DialogInputView.findViewById(R.id.InputText);
        TextInputEditText InputValue=DialogInputView.findViewById(R.id.InputValue);
        Spinner InputType=DialogInputView.findViewById(R.id.InputTypes);
        //
        alterDiaglog.setView(R.layout.input_dialog_layout)
                .setTitle("新消费")
                .setView(DialogInputView)//加载自定义布局
                .setPositiveButton("完成", (dialog,whichButton)->{
                    ConsumeItem NewItem=new ConsumeItem(null,0,0,sdf.format(Calendar.getInstance().getTime()));
                    int type_get=InputType.getSelectedItemPosition();
                    String text_get=InputText.getText().toString();


                    if(!"".equals(text_get)&&!InputValue.getText().toString().equals("")) {
                        float value_get=Float.valueOf(InputValue.getText().toString());
                        NewItem.setContent(text_get);
                        NewItem.setType(type_get);
                        NewItem.setValue(value_get);
                        consumeItemList.add(NewItem);
                        adapter.notifyItemInserted(consumeItemList.size()-1);
                        MainListView.scrollToPosition(consumeItemList.size()-1);
                        InputText.setText("");
                        consumeItemList.get(consumeItemList.size()-1).save();
                    }
                    //更新recyclerView
                })
                .setNegativeButton("取消",(DialogInterface dialog, int whichButton)->{
                }).setCancelable(false).create().show();

    }
//显示编辑框
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
        } );
        EditYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dataDay.clear();
                Calendar cal = Calendar.getInstance();//利用calender类计算每个月天数
                cal.set(Calendar.YEAR, Integer.valueOf(EditYear.getSelectedItem().toString()));
                cal.set(Calendar.MONTH, Integer.valueOf(EditMonth.getSelectedItem().toString()));
                cal.set(Calendar.DATE, 1);
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
        EditText.setText(consumeItemList.get(position).getContent());
        EditTypes.setSelection(consumeItemList.get(position).getType());
        EditValue.setText(String.valueOf(consumeItemList.get(position).getValue()));
        //

        AlertDialog.Builder alterDiaglog = new AlertDialog.Builder(MainActivity.this);
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
                        consumeItemList.get(position).setContent(text_get);
                        consumeItemList.get(position).setValue(value_get);
                        consumeItemList.get(position).setType(type_get);
                        consumeItemList.get(position).setTime(Time);
                        Log.d("AAA",String.valueOf(consumeItemList.get(position).isSaved()) );
                        consumeItemList.get(position).save();
                        adapter.notifyItemRangeChanged(0, adapter.getItemCount());
                        MainListView.scrollToPosition(position);
                        EditText.setText("");
                        EditValue.setText("");;
                    }
                    //更新recyclerView
                })
                .setNegativeButton("取消",(DialogInterface dialog, int whichButton)->{
                }).setCancelable(false).create();
                AlertDialog dialog=alterDiaglog.show();
            DeleteButton.setOnClickListener((v)->{
                consumeItemList.get(position).delete();
                consumeItemList.remove(position);
                adapter.notifyItemRemoved(position);
                dialog.dismiss();
             });

    }

}
