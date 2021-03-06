package com.interview.iso.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;

import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.interview.iso.R;
import com.interview.iso.base.MenuItem;
import com.interview.iso.fragments.BaseFragment;
import com.interview.iso.fragments.ListNameFragment;
import com.interview.iso.fragments.NewQuestionnaireFragment;
import com.interview.iso.fragments.QuestionFragment;
import com.interview.iso.utils.AppData;
import com.interview.iso.utils.Constants;
import com.interview.iso.utils.DataPreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends CameraActivity implements FragmentManager.OnBackStackChangedListener {

    DrawerLayout mDrawerLayout;
    ListView mMenuListView;
    private ActionBarDrawerToggle mDrawerToggle;
    int mSelectedMenuIndex = -1;
    Toolbar mToolbar;
    TextView mTitle;
    ImageButton mSearch,mTick,mBack;

    public static MainActivity shareActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        shareActivity = this;
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        mTitle = (TextView)findViewById(R.id.title);
        mSearch = (ImageButton)findViewById(R.id.button_search);
        mTick = (ImageButton)findViewById(R.id.button_tick);
        mBack = (ImageButton)findViewById(R.id.button_back);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            // enabling action bar app icon and behaving it as toggle button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            toolbar.setNavigationIcon(R.drawable.icon_nav);
            mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                    R.string.app_name,
                    R.string.app_name) {

                /** Called when a drawer has settled in a completely closed state. */
                @Override
                public void onDrawerClosed(View view) {
                    super.onDrawerClosed(view);
                }

                /** Called when a drawer has settled in a completely open state. */
                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                }
            };
            mDrawerLayout.setDrawerListener(mDrawerToggle);
            mDrawerToggle.syncState();
        }

        setupMenu();
    }
    MenuAdapter mMainMenuAdapter;
    public void setupMenu() {

        // Build menu items
        ArrayList<MenuItem> menuItems = new ArrayList<>();

        menuItems.add(new MenuItem("功能选择",R.drawable.icon_selectfunc, "FunctionSelectFragment","function_select"));
        menuItems.add(new MenuItem("添加新问卷", R.drawable.icon_candidate, "NewQuestionnaireFragment", "add_new"));
        menuItems.add(new MenuItem("问卷列表", R.drawable.icon_list, "ListNameFragment","list_interviewer" ));
        menuItems.add(new MenuItem("打拐指南", R.drawable.icon_tut, "BookTutorialFragment","Tutorial"));
        menuItems.add(new MenuItem("COMMIT指标产出", R.drawable.icon_tut, "CommitHelpFragment","Help"));
        mMainMenuAdapter = new MenuAdapter(this, menuItems);
        mMenuListView = (ListView)findViewById(R.id.left_menu);
        mMenuListView.setAdapter(mMainMenuAdapter);

        mMenuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> av, View view, int position, long l) {
                MenuItem menuItem = (MenuItem) mMainMenuAdapter.getItem(position);
                mSelectedMenuIndex = position;
                MainActivity.this.didSelectMenuItem(menuItem);

                mDrawerLayout.closeDrawers();
            }

        });

        didSelectMenuItem(new MenuItem("添加新问卷", R.drawable.icon_newlist,"NewQuestionnaireFragment","add_new"));

    }
    public void didSelectMenuItem(MenuItem item){
        final Fragment fragment = item.fragment(this);
        if(QuestionFragment.mPlayer!=null) {
            QuestionFragment.mPlayer.pause();
            QuestionFragment.mPlayer = null;
        }
        if (fragment != null) {
            //getSupportActionBar().setTitle(item.getTitle());
            if(item.identifier.equals("add_new")){
                mTick.setVisibility(View.VISIBLE);
                mSearch.setVisibility(View.GONE);
                mBack.setVisibility(View.GONE);
                mTick.setBackgroundResource(R.drawable.icon_tick);
                mTick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NewQuestionnaireFragment frAddNew = (NewQuestionnaireFragment)fragment;
                        frAddNew.storeInterviewer();
                    }
                });
            }else if(item.identifier.equals("list_interviewer")){
                mTick.setVisibility(View.GONE);
                mBack.setVisibility(View.GONE);
                mSearch.setVisibility(View.VISIBLE);
                mSearch.setBackgroundResource(R.drawable.icon_search);
                mSearch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ListNameFragment frListName = (ListNameFragment)fragment;
                        frListName.updateSearch();
                    }
                });
            }else if(item.identifier.equals("question")) {
                mSearch.setVisibility(View.GONE);
                mTick.setVisibility(View.GONE);
                mBack.setVisibility(View.GONE);
                mBack.setBackgroundResource(R.drawable.ic_back);
                mBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //show
                        QuestionFragment frListName = (QuestionFragment)fragment;
                        frListName.doBackQuestion();
                    }
                });

            }else {
                mSearch.setVisibility(View.GONE);
                mTick.setVisibility(View.GONE);
                mBack.setVisibility(View.GONE);
            }
            mTitle.setText(item.getTitle());
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (fragmentManager.getBackStackEntryCount() > 0) {
                fragmentManager.popBackStackImmediate(fragmentManager.getBackStackEntryAt(0).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, "root").commit();
            mDrawerToggle.setDrawerIndicatorEnabled(true);
        }
    }
    public void UpdateFragment(String fr){

        Fragment fragment = MenuItem.getFragment(this,fr);

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStackImmediate(fragmentManager.getBackStackEntryAt(0).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, "root").commit();
    }
    /*
        update action bar custom
     */
    public void updateActionBar(boolean isBackShow){
        if(isBackShow)
            mBack.setVisibility(View.VISIBLE);
        else
            mBack.setVisibility(View.GONE);
    }

    public void loadWebview(int id){
        Intent itent = new Intent(this,ChapterViewActivity.class);
        itent.putExtra("id",id);
        startActivity(itent);
    }
    @Override
    public void onBackStackChanged() {
        boolean canback = getSupportFragmentManager().getBackStackEntryCount()>0;
        mDrawerToggle.setDrawerIndicatorEnabled(!canback);
        BaseFragment currentFragment = (BaseFragment)getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (currentFragment != null) {
            if (BaseFragment.class.isAssignableFrom(currentFragment.getClass())) {
                getSupportActionBar().setTitle(currentFragment.getTitle());
            }
        }
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggle
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
            getSupportFragmentManager().popBackStack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    class MenuAdapter extends BaseAdapter {

        private List<MenuItem> menuItems;
        private Context mContext;
        MenuAdapter(Context context,ArrayList<MenuItem> items){
            mContext = context;
            menuItems = items;

        }

        @Override
        public int getCount() {
            return menuItems!=null? menuItems.size(): 0;
        }

        @Override
        public Object getItem(int position) {
            return menuItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            ViewHolder holder;
            MenuItem menuItem = menuItems.get(position);
            if (v == null) {
                holder = new ViewHolder();
                // Inflate the layout according to the view type
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.menu_item, parent, false);
                holder.ivIcon = (ImageView)v.findViewById(R.id.icon);
                holder.tvName = (TextView)v.findViewById(R.id.menu_name);
                v.setTag(holder);
            }else {
                holder =(ViewHolder) v.getTag();
            }
            holder.ivIcon.setImageResource(menuItem.mIconResource);
            holder.tvName.setText(menuItem.mTitle);
            return v;
        }

        class ViewHolder {
            ImageView ivIcon;
            TextView tvName;
        }
    }



}
