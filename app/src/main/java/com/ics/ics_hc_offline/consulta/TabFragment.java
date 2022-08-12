package com.ics.ics_hc_offline.consulta;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.ics.ics_hc_offline.R;

public class TabFragment extends Fragment {
    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static int int_items = 2;
    public static Context CONTEXT;
    public static Bundle bundle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        /**
         *Inflate tab_layout and setup Views.
         */
        bundle = getArguments();

        View v = inflater.inflate(R.layout.fragment_tab_layout, container, false);
        tabLayout = (TabLayout) v.findViewById(R.id.tabs);
        viewPager = (ViewPager) v.findViewById(R.id.viewpager);
        /**
         *Set an Apater for the View Pager
         */
        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));

        /**
         * Now , this is a workaround ,
         * The setupWithViewPager dose't works without the runnable .
         * Maybe a Support Library Bug .
         */

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });
        CONTEXT = v.getContext();
        return v;
    }

    class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Return fragment with respect to Position .
         */

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    // The first section of the app is the most interesting -- it offers
                    // a launchpad into the other demonstrations in this example application.
                    return new CSintomasTabFragment(bundle);
                case 1:
                    return  new CExamenesTabFragment(bundle);
                case 2:
                    return  new CDiagnosticoTabFragment(bundle);
                case 3:
                    return  new CCierreTabFragment(bundle);
            }
            return null;
        }

        @Override
        public int getCount() {
            return 4;
        }

        /**
         * This method returns the title of the tab according to the position.
         */

        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    // The first section of the app is the most interesting -- it offers
                    // a launchpad into the other demonstrations in this example application.
                    return CONTEXT.getResources().getString(R.string.title_tab_sintomas);

                case 1:
                    return CONTEXT.getResources().getString(R.string.title_tab_examenes);
                case 2:
                    return CONTEXT.getResources().getString(R.string.title_tab_diagnostico);
                case 3:
                    return CONTEXT.getResources().getString(R.string.title_tab_cierre);
            }
            return "Section " + (position + 1);
        }
    }
}
