package com.example.esatgozcu.introsliderkullanimi;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;



public class AnaEkran extends AppCompatActivity{
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private Button btnSkip, btnNext;
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ana_ekran);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        btnSkip = (Button) findViewById(R.id.btn_skip);
        btnNext = (Button) findViewById(R.id.btn_next);

        // Daha önce çalışıp çalışmadığı kontrol ediliyor
        prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLaunch()) {
            // IntroSlider daha önce gösterilmiş ise tekrardan gösterilmiyor
            launchHomeScreen();
            finish();
        }

        // SDK versiyonu 21'den büyükse Notification bar'ı transparan hale getiriyoruz
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }


        //Slider sayfalarını ekliyoruz
        layouts = new int[]{
                R.layout.ekran1,
                R.layout.ekran2,
                R.layout.ekran3,
                R.layout.ekran4};

        // Noktaları ekliyoruz
        addBottomDots(0);

        changeStatusBarColor();

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        //Geç butonuna tıklanırsa..
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchHomeScreen();
            }
        });

        //İleri butonuna tıklanırsa..
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Son sayfa olup olmadığı kontrol ediliyor
                int current = getItem(+1);
                if (current < layouts.length) {
                    // Son sayfa değilse mevcut sayfa güncelleniyor
                    viewPager.setCurrentItem(current);
                } else {
                    // Son sayfa ise else bloğu çalışıyor
                    launchHomeScreen();
                }
            }
        });
    }

    //Mevcut sayfaya göre noktaların aktif ve pasif renklerini ayarlıyoruz
    private void addBottomDots(int currentPage) {

        dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    // viewPager'daki mevcut sayfayı döndürüyor
    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    // AnaEkran sayfasına geçiş yapıyoruz
    private void launchHomeScreen() {
        prefManager.setFirstTimeLaunch(false);
        startActivity(new Intent(AnaEkran.this, MainActivity.class));
        finish();
    }

    //	Viewpager listener değişiklik olduğunda harekete geçecek
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            // Eğer son sayfa ise..
            if (position == layouts.length - 1) {
                // İleri butonu başla olarak değişecek
                btnNext.setText("BAŞLA");

                btnSkip.setVisibility(View.GONE);
            } else {
                btnNext.setText("İLERİ");
                btnSkip.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    /**
        SDK versiyonuna göre Notification bar'ı transparan hale getiriyoruz
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }
    /**
        View pager adapter
     */
    public class MyViewPagerAdapter extends PagerAdapter {

        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}
