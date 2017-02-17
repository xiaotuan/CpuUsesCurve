package com.android.settings.xunhu;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.view.ViewGroup.LayoutParams;
import android.view.View;
import android.widget.GridLayout;

import com.android.settings.R;

public class CpuUsagePreference extends Preference {

    private Context mContext;
    private GridLayout mCpuUsageContainer;
    private CpuUsageView[] mCpuUsageViews;

    private int mCpuCores;

    public CpuUsagePreference(Context context) {
        this(context, null);
    }

    public CpuUsagePreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CpuUsagePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mCpuCores = CpuUtils.getCpuCore(getContext());
        StringBuilder summary = new StringBuilder();
        if (mCpuCores > 0) {
            summary.append(getCpuCoreDiscription(mCpuCores));
        }
        long freq = CpuUtils.getMaxCpuFreq(getContext());
        Log.d(this, "CpuUsagePreference=>freq: " + freq);
        if (freq > 0) {
            if (summary.length() > 0) {
                summary.append("\n");
            }
            summary.append(CpuUtils.formatFrequency(freq));
        }
        setSummary(summary.toString());
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        mCpuUsageContainer = (GridLayout) holder.findViewById(R.id.gird_containt);
        int columnCount = mCpuCores;
        if (mCpuCores > 4) {
            columnCount = (mCpuCores % 2 == 0 ? mCpuCores / 2 : mCpuCores / 2 + 1);
        }
        Log.d(this,"onBindViewHolder=>columnCount: " + columnCount);
        mCpuUsageContainer.setColumnCount(columnCount);
        Log.d(this, "onBindViewHolder=>core: " + mCpuCores);
        if (mCpuCores > 0) {
            mCpuUsageViews = new CpuUsageView[mCpuCores];
            for (int i = 0; i < mCpuUsageViews.length; i++) {
                mCpuUsageViews[i] = new CpuUsageView(getContext());
                LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                mCpuUsageViews[i].setLayoutParams(lp);
                mCpuUsageViews[i].setCore(i);
                mCpuUsageViews[i].setVisibility(View.VISIBLE);
                Log.d(this, "onBindViewHolder=>i: " + i);
                mCpuUsageContainer.addView(mCpuUsageViews[i]);
            }
        }

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
