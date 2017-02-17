CPU使用率曲线首选项

说明：
======================================================================
版本：V1.0
日期：2016-12-07
======================================================================

功能说明（Function）：
======================================================================
1. 在首选项中显示各个CPU的使用率曲线图。
2. 显示CPU核数
3. 显示CPU频率
======================================================================

功能实现说明（Functional implementation description）：
======================================================================
（1）. 实现CPU使用率曲线首选项（CpuUsagePreference.java):
	1. CpuUsagePreference继承于Preference。
	2. CpuUsagePreference使用自定义的布局文件，实际布局文件是通过修改Android源码中的preference.xml，只是在后面添加一个GridLayout。
	3. 读取手机根目录下的/sys/devices/system/cpu/文件，通过判断cpu+数字的文件夹的方法获取cpu的核数。
	4. 读取手机根目录下的/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq文件内容获取cpu的频率。
	5. 通过cpu的核数向GridLayout中添加cpu使用率曲线图，如果cpu核数大于4个，就分成两行显示。代码如下：
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
        ............
	}

（2）CPU使用率曲线图的实现方法：
	1. 通过读取手机根目录下的/proc/stat文件获取每个CPU的使用率，获取方法请查看CpuUsageView中的refreshCpuUsage方法。
实际计算算式是参照Android源码中/frameworks/base/core/java/com/android/internal/os/ProcessCpuTracker.java类中的update()的cpu使用率计算方法。
	2. 计算View的大小，如果cpu核数小于等于4核，则view的高度为match_parent，否则为可用高度的一半。如果cpu的核数小于等于4核，则view的宽度为可用宽度的核数n等分；
否则为可用宽度的(core % 2 == 0 ? core / 2 : core / 2 + 1)等分。
	3. 绘制曲线，实际使用绘制路径方法进行绘制的，具体方法如下：
		首先获取当前cpu使用率。
		其次计算当前使用率换算成在view的高度。
		最后将该点移至view的右边，即x为view的宽度。
=========================================================================

已发现BUG：
=========================================================================
（1）偶尔在开始显示时出现一条从左到右的直线横线，特别是在cpu频率获取失败时。
=========================================================================

		