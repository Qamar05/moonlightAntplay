package com.antplay.ui.intrface;

import android.widget.AbsListView;

public interface AdapterFragmentCallbacks {
    int getAdapterFragmentLayoutId();
    void receiveAbsListView(AbsListView gridView);
}
