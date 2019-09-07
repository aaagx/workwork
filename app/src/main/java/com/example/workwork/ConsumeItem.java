package com.example.workwork;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.litepal.crud.DataSupport;

public class ConsumeItem extends DataSupport {

    private String Content;
    private int Type;
    private float Value;
    private String time;
    static SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
    public ConsumeItem(String Contnet,int Type,int Value,String time)
    {
        this.Content=Contnet;
        this.Type=Type;
        this.Value=Value;
        this.time=time;
    }
    public String getContent()
    {
        return Content;
    }
    public float getValue()
    {
        return Value;
    }
    public int getType()
    {
        return Type;
    }
    public Calendar getTime()
    {
        Calendar Time=Calendar.getInstance();
        try {
            Time.setTime(sdf.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return Time;
    }
    public void setContent(String Content)
    {
        this.Content=Content;
    }
    public void setType(int Type)
    {
        this.Type=Type;
    }
    public void setValue(float Value) { this.Value=Value; }
    public void setTime(Calendar Time) {time=sdf.format(Time.getTime()); }
    public  void settime(String time){this.time=time;}
    public  String  gettime(){return time;}

}
