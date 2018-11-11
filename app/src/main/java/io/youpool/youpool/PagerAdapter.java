package io.youpool.youpool;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {

    int mNoOfTabs;

    public PagerAdapter(FragmentManager fm, int NumerOfTabs) {

        super(fm);
        this.mNoOfTabs = NumerOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                poolStats tab1 = new poolStats();
                return tab1;
            case 1:
                market tab2 = new market();
                return tab2;
            case 2:
                news tab3 = new news();
                return tab3;
            case 3:
                community tab4 = new community();
                return tab4;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {

        return mNoOfTabs;
    }
}
