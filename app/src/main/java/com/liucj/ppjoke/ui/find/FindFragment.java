package com.liucj.ppjoke.ui.find;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.View;

import com.liucj.libnavannotation.FragmentDestination;
import com.liucj.ppjoke.model.SofaTab;
import com.liucj.ppjoke.ui.event.ChangePageEvent;
import com.liucj.ppjoke.ui.sofa.SofaFragment;
import com.liucj.ppjoke.utils.AppConfig;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

/**
 * 发现
 */
@FragmentDestination(pageUrl = "main/tabs/find")
public class FindFragment extends SofaFragment {
    @Override
    public Fragment getTabFragment(int position) {
        SofaTab.Tabs tab = getTabConfig().tabs.get(position);
        TagListFragment fragment = TagListFragment.newInstance(tab.tag);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public SofaTab getTabConfig() {
        return AppConfig.getFindTabConfig();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ChangePageEvent event) {/* Do something */
        viewPager.setCurrentItem(1);
    }
}