package com.github.mikephil.charting.formatter;

import com.github.mikephil.charting.components.AxisBase;

public class MyXFormatter extends ValueFormatter {
/*
    private String[] mValues;

    public MyXFormatter(String[] values) {
        this.mValues = values;
    }
    private static final String TAG = "MyXFormatter";

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        // "value" represents the position of the label on the axis (x or y)
        Log.d(TAG, "----->getFormattedValue: "+value);
        return mValues[(int) value % mValues.length];
    }*/
private String[] xStrs = new String[]{"0点", "2点", "6点", "10点","14点","18点","22点"};

    @Override

    public String getFormattedValue(float value, AxisBase axis) {
        int position = (int) value;
        if (position > 7) {
            position = 0;
        }
        return xStrs[position];
    }
}
