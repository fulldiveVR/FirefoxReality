package org.mozilla.vrbrowser.ui.views.home;

import static org.mozilla.gecko.util.ThreadUtils.runOnUiThread;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.vive.browser.wave.R;
import org.mozilla.vrbrowser.VRBrowserActivity;
import org.mozilla.vrbrowser.VRBrowserApplication;
import org.mozilla.vrbrowser.addons.views.AddonsView;
import com.vive.browser.wave.databinding.HomeBinding;

import org.mozilla.vrbrowser.browser.engine.Session;
import org.mozilla.vrbrowser.browser.engine.SessionStore;
import org.mozilla.vrbrowser.ui.delegates.HomeNavigationDelegate;
import org.mozilla.vrbrowser.ui.delegates.LibraryNavigationDelegate;
import org.mozilla.vrbrowser.ui.widgets.WidgetManagerDelegate;
import org.mozilla.vrbrowser.ui.widgets.WindowWidget;
import org.mozilla.vrbrowser.ui.widgets.Windows;
import org.mozilla.vrbrowser.utils.InternalPages;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.Executor;


import android.webkit.WebSettings;
import android.webkit.WebView;

public class HomePanel extends FrameLayout {

    private WindowWidget mWindowWidget = null;
    private HomeBinding mBinding;
    protected WidgetManagerDelegate mWidgetManager;
    protected Executor mUIThreadExecutor;
    private @Windows.PanelType int mCurrentPanel;

    public HomePanel(@NonNull Context context, WindowWidget windowWidget) {
        super(context);
        mWindowWidget = windowWidget;
        initialize();
    }

    public HomePanel(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public HomePanel(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    protected void initialize() {
        mWidgetManager = ((VRBrowserActivity) getContext());


        mUIThreadExecutor = ((VRBrowserApplication)getContext().getApplicationContext()).getExecutors().mainThread();

        mCurrentPanel = Windows.HOME;

        updateUI();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void updateUI() {
        removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(getContext());
        //WebView webView = (WebView) findViewById(R.id.webview);
        //webView.loadUrl("https://www.rbc.ru");

        mBinding = DataBindingUtil.inflate(inflater, R.layout.home, this, true);
        mBinding.setLifecycleOwner((VRBrowserActivity) getContext());

        mBinding.setDelegate(new HomeNavigationDelegate() {
            @Override
            public void onClose(@NonNull View view) {
                requestFocus();
                mWidgetManager.getFocusedWindow().hidePanel();
            }

            @Override
            public void onBack(@NonNull View view) {
                requestFocus();
                //mCurrentView.onBack();
                mBinding.setCanGoBack(false);
            }

            @Override
            public void onButtonClick(@NonNull View view) {
                requestFocus();
                //selectTab(view);
            }
        });


        WebSettings settings = mBinding.webview.getSettings();
        //settings.setDomStorageEnabled(true);
        //settings.setJavaScriptEnabled(true);
        //settings.setDatabaseEnabled(true);
        //settings.setAllowFileAccess(true);

        //String mimeType = "text/html";
        //String encoding = "utf-8";
        //String injection = "<script type='text/javascript'>localStorage.setItem('key', 'val');window.location.replace('file:///android_asset/html/home.html');</script>";
        //mBinding.webview.loadDataWithBaseURL("file:///android_asset/html/home.html", injection, mimeType, encoding, null);



        //add the JavaScriptInterface so that JavaScript is able to use LocalStorageJavaScriptInterface's methods when calling "LocalStorage"
        mBinding.webview.addJavascriptInterface(new LocalStorageJavaScriptInterface(getContext().getApplicationContext()), "LocalStorage");
        mBinding.webview.addJavascriptInterface(new FulldiveJavaScriptInterface(getContext().getApplicationContext()), "Fulldive");


        WebSettings webSettings = mBinding.webview.getSettings();
        //enable JavaScript in webview
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccessFromFileURLs(true); //Maybe you don't need this rule
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);

        mBinding.webview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });

        mBinding.webview.setVerticalScrollBarEnabled(false);
        mBinding.webview.setHorizontalScrollBarEnabled(false);

        mBinding.webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                view.loadUrl("javascript:document.getElementsByName('q_searc_textfield')[0].focus();");
            }
        });

        //Enable and setup JS localStorage
        //webSettings.setDomStorageEnabled(true);
        //those two lines seem necessary to keep data that were stored even if the app was killed.
        //webSettings.setDatabaseEnabled(true);
        //webSettings.setDatabasePath(getContext().getFilesDir().getParentFile().getPath()+"/databases/");

        //mBinding.webview.loadUrl("file:///android_asset/html/home.html");
        mBinding.webview.loadUrl(InternalPages.makeHomePageURL(getContext()));
        //mBinding.webview.loadUrl("file:///android_asset/html/test/index.html");




        mBinding.executePendingBindings();

        selectPanel(mCurrentPanel);

        //setOnTouchListener((v, event) -> {
        //    v.requestFocusFromTouch();
        //    return false;
        //});
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        updateUI();
    }

    public void onShow() {
        mBinding.webview.loadUrl(InternalPages.makeHomePageURL(getContext()));
        //updateLayout();
        //onViewUpdated(getContext().getString(R.string.bookmarks_title));
    }

    public void onHide() {
    }

    public boolean onBack() {
        return false;
    }

    public void onDestroy() {
    }

    public @Windows.PanelType int getSelectedPanelType() {
        return Windows.HOME;
    }

    private void selectTab(@NonNull View view) {
    }

    public void selectPanel(@Windows.PanelType int panelType) {
        mCurrentPanel = Windows.HOME;

    }

    public void onViewUpdated(@NonNull String title) {

    }


    private class LocalStorageJavaScriptInterface {
        private Context mContext;
        private LocalStorage localStorageDBHelper;
        private SQLiteDatabase database;

        LocalStorageJavaScriptInterface(Context c) {
            mContext = c;
            localStorageDBHelper = LocalStorage.getInstance(mContext);
        }

        /**
         * This method allows to get an item for the given key
         * @param key : the key to look for in the local storage
         * @return the item having the given key
         */
        @JavascriptInterface
        public String getItem(String key)
        {
            String value = null;
            if(key != null)
            {
                database = localStorageDBHelper.getReadableDatabase();
                Cursor cursor = database.query(LocalStorage.LOCALSTORAGE_TABLE_NAME,
                        null,
                        LocalStorage.LOCALSTORAGE_ID + " = ?",
                        new String [] {key},null, null, null);
                if(cursor.moveToFirst())
                {
                    value = cursor.getString(1);
                }
                cursor.close();
                database.close();
            }
            return value;
        }

        /**
         * set the value for the given key, or create the set of datas if the key does not exist already.
         * @param key
         * @param value
         */
        @JavascriptInterface
        public void setItem(String key,String value)
        {
            if(key != null && value != null)
            {
                String oldValue = getItem(key);
                database = localStorageDBHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(LocalStorage.LOCALSTORAGE_ID, key);
                values.put(LocalStorage.LOCALSTORAGE_VALUE, value);
                if(oldValue != null)
                {
                    database.update(LocalStorage.LOCALSTORAGE_TABLE_NAME, values, LocalStorage.LOCALSTORAGE_ID + "='" + key + "'", null);
                }
                else
                {
                    database.insert(LocalStorage.LOCALSTORAGE_TABLE_NAME, null, values);
                }
                database.close();
            }
        }

        /**
         * removes the item corresponding to the given key
         * @param key
         */
        @JavascriptInterface
        public void removeItem(String key)
        {
            if(key != null)
            {
                database = localStorageDBHelper.getWritableDatabase();
                database.delete(LocalStorage.LOCALSTORAGE_TABLE_NAME, LocalStorage.LOCALSTORAGE_ID + "='" + key + "'", null);
                database.close();
            }
        }

        /**
         * clears all the local storage.
         */
        @JavascriptInterface
        public void clear()
        {
            database = localStorageDBHelper.getWritableDatabase();
            database.delete(LocalStorage.LOCALSTORAGE_TABLE_NAME, null, null);
            database.close();
        }
    }

    private class FulldiveJavaScriptInterface {
        private Context mContext;

        FulldiveJavaScriptInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void openURL(String url)
        {
            Session session = SessionStore.get().getActiveSession();
            //session.loadUri(url);
            mWindowWidget.getSession().loadUri(url);
        }

        @JavascriptInterface
        public void getFavicon(String page_url)
        {
            runOnUiThread(new Runnable() {
                public void run2() {
                    WebView wv = new WebView(getContext());
                    wv.setWebChromeClient(new WebChromeClient() {
                        @Override
                        public void onReceivedIcon(WebView view, Bitmap icon) {
                            super.onReceivedIcon(view, icon);
                            Bitmap favicon  = icon;
                            if (favicon != null) {
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                favicon.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                byte[] byteArray = stream.toByteArray();
                                favicon.recycle();
                                String favicon_data_string = "data:image/png;base64," + Base64.encodeToString(byteArray, Base64.NO_WRAP);
                                final String js_code = "onFaviconLoaded(\""+page_url+"\", \""+favicon_data_string+"\");";

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mBinding.webview.evaluateJavascript(js_code, null);
                                    }
                                });

                            }

                        }
                    });
                    wv.loadUrl(page_url);
                }

                private boolean processIcon(WebView view) {
                    Bitmap favicon  = view.getFavicon();
                    if (favicon != null) {
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        favicon.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArray = stream.toByteArray();
                        favicon.recycle();
                        String favicon_data_string = "data:image/png;base64," + Base64.encodeToString(byteArray, Base64.NO_WRAP);
                        final String js_code = "onFaviconLoaded(\""+page_url+"\", \""+favicon_data_string+"\");";

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mBinding.webview.evaluateJavascript(js_code, null);
                            }
                        });
                        return true;
                    }
                    else {
                        return false;
                    }
                }


                @Override
                public void run() {
                    WebView wv = new WebView(getContext());
                    wv.setWebViewClient(new WebViewClient() {
                        @Override
                        public void onPageFinished(WebView view, String url) {
                            if (!processIcon(view)) {
                                Handler h = new Handler();
                                h.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!processIcon(view)) {
                                            Handler h = new Handler();
                                            h.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    processIcon(view);
                                                }
                                            }, 2000);
                                        }
                                    }
                                }, 500);
                            }
                        }
                    });
                    wv.loadUrl(page_url);
                }
            });
        }

    }

}
