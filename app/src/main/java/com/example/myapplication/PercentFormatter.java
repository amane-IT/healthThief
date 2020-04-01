package com.example.myapplication;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;

public class PercentFormatter implements IValueFormatter {

    private DecimalFormat percentFormat;

    public PercentFormatter(){
        percentFormat = new DecimalFormat(("##.#"));
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler){
        return percentFormat.format(value)+"%";
    }

}
