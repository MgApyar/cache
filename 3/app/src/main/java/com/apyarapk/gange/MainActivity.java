package com.apyarapk.gange;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.Browser;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.apyarapk.gange.Adapter.ApyarAdapter;
import com.apyarapk.gange.Browser.MyBrowser;
import com.apyarapk.gange.Utils.CheckInternet;
import com.apyarapk.gange.Utils.CheckUpdate;
import com.apyarapk.gange.Utils.FontChanger;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> book_file = new ArrayList<>();
    ArrayList<String> book_name = new ArrayList<>();
    ListView listView;
    AdView adView;
    ImageView imageView;
    InterstitialAd interstitialAd;
    int showAds_code = 0000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try{
            if (getIntent()!=null){
                startActivity(new Intent(this, Browser.class).putExtra("url",getIntent().getStringExtra("url")));
            }
        }catch (Exception e){}

        setTitle("သင္ခန္းစာ ("+getString(R.string.app_name)+")");
        new FontChanger(this).setFont(MainActivity.this,"font/mm.ttf",true);



        init();
        listView = findViewById(R.id.listView);
        ApyarAdapter apyarAdapter = new ApyarAdapter(MainActivity.this,book_name);
        listView.setAdapter(apyarAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent read = new Intent(MainActivity.this,ReadActivity.class);
                read.putExtra("url","file:///android_asset/book/"+book_file.get(i));
                startActivity(read);
                showADS();
                }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (new CheckInternet(MainActivity.this).isInternetOn()) {
            new CheckUpdate(MainActivity.this, false).check();
        }
    }

    @Override
    public void onBackPressed() {
        wantToExit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        new CheckUpdate(MainActivity.this,true).onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==showAds_code){
            showADS();
        }
    }

    public void getTitles(){
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open("source/title")));
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                book_name.add(mLine);
            }
        } catch (IOException e) {
            //log the exception
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }
    }

    public void clickedAD(){
        Intent intent = new Intent(this, MyBrowser.class);
        intent.putExtra("url",getString(R.string.all_link));
        startActivityForResult(intent,showAds_code);
        showADS();
    }

    public void wantToExit(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Attention!")
                .setIcon(R.drawable.icon)
                .setCancelable(false)
                .setMessage("Do you want to exit ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showADS();
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showADS();
                    }
                })
                .setNeutralButton("Rate", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        rate();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void init(){
        getTitles();
        initAds();
        List<String> strings;
        try {
            strings = getAllFileFromAssetFolder("book");
            for (int i =0;i<strings.size();i++){
                String f = strings.get(i);
                if (f.endsWith(".html")) {
                    book_file.add(f);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initAds(){
        interstitialAd = new InterstitialAd(this);
        adView = findViewById(R.id.adView);
        adView.loadAd(new AdRequest.Builder().build());

        imageView = findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               clickedAD();
            }
        });
        Glide.with(MainActivity.this)
                .load(R.drawable.ad)
                .asGif()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .crossFade().into(imageView);

        adView.setAdListener(new AdListener(){
            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                imageView.setVisibility(View.VISIBLE);
                adView.setVisibility(View.GONE);
            }
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                imageView.setVisibility(View.GONE);
                adView.setVisibility(View.VISIBLE);
            }
        });

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-2780984156359274/2458962792");
        interstitialAd.loadAd(new AdRequest.Builder().build());
        interstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                loadADS();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                loadADS();
            }
        });
        showADS();
    }

    public void loadADS(){
        if (!interstitialAd.isLoaded()){
            interstitialAd.loadAd(new AdRequest.Builder().build());
        }
    }

    public void showADS(){
        if (!interstitialAd.isLoaded()){
            interstitialAd.loadAd(new AdRequest.Builder().build());
        }else{
            interstitialAd.show();
        }
    }

    public void showAbout(){
        String version = "1.0";
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(),0);
            version = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("About App")
                .setMessage("Name: "+getString(R.string.app_name)+"\nVersion: "+version+"\nBooks are copyrighted by their owners.")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showADS();
                    }
                })
                .setNegativeButton("Feedback", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNeutralButton("Check Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new CheckUpdate(MainActivity.this,true).check();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private List<String> getAllFileFromAssetFolder(String folder) throws IOException {
        AssetManager assetManager = getAssets();
        String[] files = assetManager.list(folder);
        List<String> it= Arrays.asList(files);
        Collections.sort(it, new Comparator<String>() {
            public int compare(String o1, String o2) {
                return extractInt(o1) - extractInt(o2);
            }
            int extractInt(String s) {
                String num = s.replaceAll("\\D", "");
                return num.isEmpty() ? 0 : Integer.parseInt(num);
            }
        });
        return it;
    }

    public void rate(){
        View view = getLayoutInflater().inflate(R.layout.rate,null);
        TextView tv = view.findViewById(R.id.tv);
        tv.setTypeface(Typeface.createFromAsset(getAssets(),"font/mm.ttf"));
        tv.setText("ႀကိဳက္တယ္ဆို ၾကယ္ ၅ လုံးေပးၿပီး\n" +
                "ထင္ျမင္ခ်က္ေလးေရးခဲ့ေပးပါ။");
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)),showAds_code);
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)),showAds_code);
                }
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("ံHelp Us")
                .setView(view)
                .setCancelable(false)
                .setPositiveButton("Rate Now", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                        try {
                            startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)),showAds_code);
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)),showAds_code);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showADS();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Typeface span = Typeface.createFromAsset(getAssets(),"font/mm.ttf");
        String items [] = {"ျပန္လည္မွ်ေဝရန္","ဗားရွင္းအသစ္စစ္ေဆးရန္","အမွတ္ေပးရန္","ဆက္သြယ္အႀကံျပဳရန္","ေဆာ့ဝဲႏွင့္ပတ္သက္၍","အျခားစာအုပ္မ်ားရယူရန္"};
        for (int i=0;i<items.length;i++){
            SpannableStringBuilder title = new SpannableStringBuilder(items[i]);
            title.setSpan(span, 0, title.length(), 0);
            menu.add(title);
        }



        for (int i = 0; i < menu.size(); i++) {
            MenuItem mi = menu.getItem(i);
            //for aapplying a font to subMenu ...
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem, Typeface.createFromAsset(getAssets(),"font/mm.ttf"));
                }
            }
            //the method we have create in activity
            applyFontToMenuItem(mi, Typeface.createFromAsset(getAssets(),"font/mm.ttf"));
        }

        return super.onCreateOptionsMenu(menu);
    }

    private void applyFontToMenuItem(MenuItem mi, Typeface font) {
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    public class CustomTypefaceSpan extends TypefaceSpan {

        private final Typeface newType;

        public CustomTypefaceSpan(String family, Typeface type) {
            super(family);
            newType = type;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            applyCustomTypeFace(ds, newType);
        }

        @Override
        public void updateMeasureState(TextPaint paint) {
            applyCustomTypeFace(paint, newType);
        }

        public   void applyCustomTypeFace(Paint paint, Typeface tf) {
            int oldStyle;
            Typeface old = paint.getTypeface();
            if (old == null) {
                oldStyle = 0;
            } else {
                oldStyle = old.getStyle();
            }

            int fake = oldStyle & ~tf.getStyle();
            if ((fake & Typeface.BOLD) != 0) {
                paint.setFakeBoldText(true);
            }

            if ((fake & Typeface.ITALIC) != 0) {
                paint.setTextSkewX(-0.25f);
            }

            paint.setTypeface(tf);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getTitle().toString()){
            case "ျပန္လည္မွ်ေဝရန္":
                share();
                break;
            case "ဆက္သြယ္အႀကံျပဳရန္":
                startActivityForResult(new Intent(MainActivity.this,Feedback.class),showAds_code);
                showADS();
                break;
            case "ေဆာ့ဝဲႏွင့္ပတ္သက္၍":
                showAbout();
                break;
            case "အျခားစာအုပ္မ်ားရယူရန္":
                Intent intent = new Intent(this, MyBrowser.class);
                intent.putExtra("url",getString(R.string.more_link));
                startActivityForResult(intent,showAds_code);
                showADS();
                break;
            case "ဗားရွင္းအသစ္စစ္ေဆးရန္":
                new CheckUpdate(MainActivity.this,true).check();
                break;
            case "အမွတ္ေပးရန္":
                rate();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void share() {
        String shareText = "အျပာစာအုပ္ဖတ္ရႈ႕ႏိုင္မယ့္ေဆာ့ဝဲေလးပါ။\n" +
                "အခုေဆာ့ဝဲေလးကေတာ့ သင္ခန္းစာ("+getString(R.string.app_name)+") ျဖစ္ၿပီး\n" +
                "ဒီကေနေဒါင္းယူႏိုင္ပါတယ္ \n\uD83D\uDC49\uD83D\uDC49 "+getString(R.string.app_link)+" \uD83D\uDC48\uD83D\uDC48\n\n" +
                "အျခားသင္ခန္းစာမ်ားကို ဒီမွာ ရယူႏိုင္ပါတယ္ \n\uD83D\uDC49\uD83D\uDC49 "+getString(R.string.more_link)+" \uD83D\uDC48\uD83D\uDC48\n" +
                "#ApyarApp";
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,shareText);
        startActivityForResult(Intent.createChooser(intent,"Share App..."),showAds_code);
    }
}
