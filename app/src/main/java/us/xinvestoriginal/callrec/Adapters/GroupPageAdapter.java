package us.xinvestoriginal.callrec.Adapters;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import us.xinvestoriginal.callrec.Fragments.RecFragment;
import us.xinvestoriginal.callrec.R;


/**
 * Created by x-invest on 11.07.2016.
 */

public class GroupPageAdapter extends FragmentPagerAdapter {

    private  Context context;

    public GroupPageAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        return RecFragment.getInstance(position == 1);
    }

    @Override
    public int getCount() { return 2; }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }


    @Override
    public CharSequence getPageTitle(int pos){
        int id = pos == 0 ? R.string.show_all : R.string.show_liked;
        return context.getString(id);
    }
}
