package com.apyarapk.kagyi;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.apyarapk.kagyi.Browser.MyBrowser;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class ReadActivity extends AppCompatActivity {
    WebView webView;
    AdView adView;
    ImageView imageView;
    InterstitialAd interstitialAd;
    ProgressBar progressBar;
    int showAds_code = 0000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        initAds();
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState);
        } else {
            webView = findViewById(R.id.webView);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebViewClient(new WebViewClient(){
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    progressBar.setVisibility(View.GONE);
                }
            });
            webView.getSettings().setSupportZoom(true);
            webView.loadUrl(getIntent().getStringExtra("url"));
        }
    }

    // Save the state of the web view when the screen is rotated.
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
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
        Glide.with(ReadActivity.this).load(R.drawable.ad).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).crossFade().into(imageView);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==showAds_code){
            showADS();
        }
    }

    @Override
    public void onBackPressed() {
        wantToExit();
    }

    public void clickedAD(){
        Intent intent = new Intent(this, MyBrowser.class);
        intent.putExtra("url","http://www.myanmarapk.net/2018/04/apyar-movies-app_50.html");
        startActivityForResult(intent,showAds_code);
        showADS();
    }

    public void wantToExit(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Attention!")
                .setCancelable(false)
                .setMessage("Do you want to close ?")
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
}
