package com.example.quotes;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.quotes.authors.AuthorsFragment;
import com.example.quotes.quotes.AddQuoteActivity;
import com.example.quotes.quotes.QuotesFragment;
import com.example.quotes.settings.SettingsActivity;


public class MainActivity extends ThemedActivity {

    private int currentPosition = 0;
    private SectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager viewPager;
    private FloatingActionButton floatingAddButton;
    private int[] tabIcons = {R.drawable.quotes, R.drawable.authors};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        floatingAddButton = (FloatingActionButton) findViewById(R.id.fab);

        setUpFloatingAddButton();
        setUpViewPager();
        setUpTabLayout();
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        ((QuotesFragment)getFragment(0)).initFragment();
        ((AuthorsFragment)getFragment(1)).initFragment();
    }

    private void setUpFloatingAddButton(){
        floatingAddButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddQuoteActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setUpViewPager(){
        viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                if (position == 0){
                    floatingAddButton.show();
                }
                else if (position == 1){
                    floatingAddButton.hide();
                }
                invalidateOptionsMenu();
                setActionBarTitle();
            }

            @Override
            public void onPageScrollStateChanged(int state) {}

        });
        viewPager.setAdapter(sectionsPagerAdapter);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        if(currentPosition == 0)
            menu.findItem(R.id.search).setVisible(true);
        else
            menu.findItem(R.id.search).setVisible(false);

        return super.onPrepareOptionsMenu(menu);
    }

    private void setUpTabLayout() {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(0).setText("");
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(1).setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                ((QuotesFragment)getFragment(0)).initFragment();
                return true;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                ((QuotesFragment)getFragment(0)).findQuotesAndAuthorsFromQuery(s);
                return false;
            }
        });

        return true;
    }

    private Fragment getFragment(int i){
        String fragmentName = makeFragmentName(R.id.container, i);
        return getSupportFragmentManager().findFragmentByTag(fragmentName);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private Fragment[] fragments = {new QuotesFragment(), new AuthorsFragment()};
        private String[] fragmentTitles = {getString(R.string.quotes_tab), getString(R.string.authors_tab)};

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return fragments.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitles[position];
        }
    }

    private void setActionBarTitle(){
        String title = sectionsPagerAdapter.fragmentTitles[currentPosition];
        getSupportActionBar().setTitle(title);
    }

    private static String makeFragmentName(int viewPagerId, int index) {
        return "android:switcher:" + viewPagerId + ":" + index;
    }

}
