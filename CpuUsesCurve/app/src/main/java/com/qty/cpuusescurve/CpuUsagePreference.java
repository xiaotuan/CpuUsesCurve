package com.qty.cpuusescurve;

import android.content.Context;
import android.content.res.Resources;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.GridLayout;

public class CpuUsagePreference extends Preference {

    private Context mContext;
    private GridLayout mCpuUsageContainer;
    private CpuUsageView[] mCpuUsageViews;

    public CpuUsagePreference(Context context) {
        this(context, null);
    }

    public CpuUsagePreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CpuUsagePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        final LayoutInflater layoutInflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = layoutInflater.inflate(R.layout.cpu_usage_preference, parent, false);
        mCpuUsageContainer = (GridLayout) layout.findViewById(R.id.gird_containt);
        int core = CpuUtils.getCpuCore(getContext());
        int columnCount = core;
        if (core > 4) {
            columnCount = (core % 2 == 0 ? core / 2 : core / 2 + 1);
        }
        Log.d(this,"onCreateView=>columnCount: " + columnCount);
        mCpuUsageContainer.setColumnCount(columnCount);
        Log.d(this, "onBindView=>core: " + core);
        if (core > 0) {
            mCpuUsageViews = new CpuUsageView[core];
            for (int i = 0; i < mCpuUsageViews.length; i++) {
                mCpuUsageViews[i] = new CpuUsageView(getContext());
                LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                mCpuUsageViews[i].setLayoutParams(lp);
                mCpuUsageViews[i].setCore(i);
                Log.d(this, "onBindView=>i: " + i);
                mCpuUsageContainer.addView(mCpuUsageViews[i]);
            }
        }

        StringBuilder summary = new StringBuilder();
        if (core > 0) {
            summary.append(getCpuCoreDiscription(core));
        }
        long freq = CpuUtils.getMaxCpuFreq(getContext());
        Log.d(this, "onBindView=>freq: " + freq);
        if (freq > 0) {
            if (summary.length() > 0) {
                summary.append("\n");
            }
            summary.append(CpuUtils.formatFrequency(freq));
        }
        setSummary(summary.toString());
        return layout;
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
    }

    @Override
    protected void onAttachedToHierarchy(PreferenceManager preferenceManager) {
        super.onAttachedToHierarchy(preferenceManager);
        Log.d(this, "onAttachedToHierarchy()...");
    }

    @Override
    protected void onPrepareForRemoval() {
        super.onPrepareForRemoval();
        Log.d(this, "onPrepareForRemoval()...");
    }

    private String getCpuCoreDiscription(int core) {
        Resources res = getContext().getResources();
        switch (core) {
            case 1:
                return res.getString(R.string.single_core);

            case 2:
                return res.getString(R.string.double_core);

            case 4:
                return res.getString(R.string.four_core);

            case 8:
                return res.getString(R.string.eight_core);

            case 10:
                return res.getString(R.string.ten_nuclei);

            case 16:
                return res.getString(R.string.sixteen_cores);

            default:
                return res.getString(R.string.four_core);
        }
    }

}
