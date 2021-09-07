package com.liucj.ppjoke.ui.sofa;


import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.liucj.libnavannotation.FragmentDestination;
import com.liucj.ppjoke.R;
import com.liucj.ppjoke.model.SofaTab;
import com.liucj.ppjoke.ui.home.HomeFragment;
import com.liucj.ppjoke.ui.view.BaseListFragment;
import com.liucj.ppjoke.utils.AppConfig;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@FragmentDestination(pageUrl = "main/tabs/sofa", asStarter = false)
public class SofaFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private View rootView;
    private List<SofaTab.Tabs> tabs;
    private SofaTab tabConfig;
    private Map<Integer, Fragment> mFragmentMap = new HashMap<>();
    private TabLayoutMediator mediator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_sofa, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        tabLayout = rootView.findViewById(R.id.tab_layout);
        viewPager = rootView.findViewById(R.id.view_pager);
        tabConfig = getTabConfig();
        tabs = new ArrayList<>();
        for (SofaTab.Tabs tab : tabConfig.tabs) {
            if (tab.enable) {
                tabs.add(tab);
            }
        }
        viewPager.setOffscreenPageLimit(ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT);
        viewPager.setAdapter(new FragmentStateAdapter(getChildFragmentManager(), this.getLifecycle()) {
            @NonNull
            @NotNull
            @Override
            public Fragment createFragment(int position) {
                Fragment fragment = mFragmentMap.get(position);
                if (fragment == null) {
                    fragment = getTabFragment(position);
                }
                return fragment;
            }

            @Override
            public int getItemCount() {
                return tabs.size();
            }
        });
        mediator = new TabLayoutMediator(tabLayout, viewPager, false, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull @NotNull TabLayout.Tab tab, int position) {
                tab.setCustomView(makeTabView(position));
            }
        });
        mediator.attach();
        viewPager.registerOnPageChangeCallback(mPageChangeCallback);
        viewPager.post(new Runnable() {
            @Override
            public void run() {
                viewPager.setCurrentItem(tabConfig.select);
            }
        });
    }

    ViewPager2.OnPageChangeCallback mPageChangeCallback = new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageSelected(int position) {
            int tabCount = tabLayout.getTabCount();
            for (int i = 0; i < tabCount; i++) {
                TabLayout.Tab tab = tabLayout.getTabAt(i);
                TextView customView = (TextView) tab.getCustomView();
                if (tab.getPosition() == position) {
                    customView.setTextSize(tabConfig.activeSize);
                    customView.setTypeface(Typeface.DEFAULT_BOLD);
                } else {
                    customView.setTextSize(tabConfig.normalSize);
                    customView.setTypeface(Typeface.DEFAULT);
                }
            }
        }
    };

    private View makeTabView(int position) {
        TextView tabView = new TextView(getContext());
        int[][] states = new int[2][];
        states[0] = new int[]{android.R.attr.state_selected};
        states[1] = new int[]{};
        int[] colors = new int[]{Color.parseColor(tabConfig.activeColor), Color.parseColor(tabConfig.normalColor)};
        ColorStateList stateList = new ColorStateList(states, colors);
        tabView.setTextColor(stateList);
        tabView.setText(tabs.get(position).title);
        tabView.setTextSize(tabConfig.normalSize);
        return tabView;
    }

    private Fragment getTabFragment(int position) {
        return HomeFragment.newInstance(tabs.get(position).tag);
    }

    private SofaTab getTabConfig() {
        return AppConfig.getSofaTabConfig();
    }

    @Override
    public void onDestroy() {
        mediator.detach();
        viewPager.unregisterOnPageChangeCallback(mPageChangeCallback);
        super.onDestroy();
    }
}