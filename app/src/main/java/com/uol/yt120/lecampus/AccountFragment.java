package com.uol.yt120.lecampus;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import timber.log.Timber;

import static android.os.Environment.MEDIA_MOUNTED;

public class AccountFragment extends Fragment {

    public WebView webView;

    private Activity mActivity;
    private Context mContext;

    private String loginAddress;
    private String loginAddressFailed;
    private String prefixAddress;
    private String detailAddress;
    private String timetableAddress;

    volatile boolean loginSuccessful = false;
    volatile boolean timetableSuccessful = false;
    volatile boolean detailSuccessful = false;


    @SuppressLint("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        loginAddress = getString(R.string.login_web_address);
        loginAddressFailed = getString(R.string.login_web_address_failed);
        prefixAddress = getString(R.string.prefix_web_address);

        getActivity().setTitle(getString(R.string.title_fragment_account));
        //getActivity().setContentView(R.layout.fragment_timetable);
        //ListView timetableListView = getActivity().findViewById(R.id.timetable_item_list);

        View view=inflater.inflate(R.layout.fragment_account_web, container, false);
        webView = view.findViewById(R.id.accountWebView);
        webView.loadUrl(loginAddress);

        webView.addJavascriptInterface(new InJavaScriptLocalObj(), "java_obj");

        // Disable page zoom
        webView.getSettings().setSupportZoom(false);

        // Disable built in zoom control
        webView.getSettings().setBuiltInZoomControls(false);

        // Disable cache
//        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
//        webView.getSettings().setAppCacheEnabled(false);

        // Enable DOM Storage API
        webView.getSettings().setDomStorageEnabled(true);

        webView.requestFocus();
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);

        // Enable Javascript
        webView.getSettings().setJavaScriptEnabled(true);

        // Force links and redirects to open in the WebView instead of in a browser
        webView.setWebViewClient(new WebViewClient(){

            boolean detailAddressloaded = false;

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.i("[Account Fragmt]","Start loading, current URL: "+url);
                Timber.tag("[Account Fragmt]").i("Start loading, current URL: "+url);

            }

            /**
             * An alternative method which can catch url's on hash change.
             * @param view
             * @param url
             * @param isReload
             */
            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                super.doUpdateVisitedHistory(view, url, isReload);

                Log.i("[Account Fragmt]","1. LoginSuccessful: "+loginSuccessful);
                Log.i("[Account Fragmt]","2. TimetableSuccessful: "+timetableSuccessful);
                Log.i("[Account Fragmt]","3. DetailSuccessful: "+detailSuccessful);

                Log.i("[Account Fragmt]","Hash/URL changed, current URL: "+url);

                if(!loginSuccessful) {


                } else {

                    while (!timetableSuccessful && !detailSuccessful && loginSuccessful) {

                        Log.i("[Account Fragmt]","Fetching timetable...");
                        Timber.tag("[Account Fragmt]").i("Fetching timetable...");

                        try {
                            Thread.currentThread().sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        Log.i("[Account Fragmt]","Hash changed, current URL for timetable: "+url);

//                        view.loadUrl("javascript:window.onhashchange = function(){java_obj.showTimetableSource('<head>'+" +
//                                "document.getElementsByTagName('html')[0].innerHTML+'</head>');};");
                        view.loadUrl("javascript:window.java_obj.showTimetableSource('<head>'+" +
                                "document.getElementsByTagName('html')[0].innerHTML+'</head>');");

                        try {
                            Thread.currentThread().sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }


                    if (!detailAddressloaded) {
                        // Load user detail
                        view.loadUrl(prefixAddress+detailAddress);
                        Log.i("[Account Fragmt]","Loading user detail, URL: "+prefixAddress+detailAddress);
                        detailAddressloaded = true;

                    } else {
                        Log.i("[Account Fragmt]","Fetching user detail...");
                        Timber.tag("[Account Fragmt]").i("Fetching user detail...");

                        while (!detailSuccessful && loginSuccessful && timetableSuccessful) {

                            try {
                                Thread.currentThread().sleep(10000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            Log.i("[Account Fragmt]","Current URL for user detail: "+url);

//                        view.loadUrl("javascript:window.onhashchange = function(){java_obj.showDetailSource('<head>'+" +
//                                "document.getElementsByTagName('html')[0].innerHTML+'</head>');};");
                            view.loadUrl("javascript:window.java_obj.showDetailSource('<head>'+" +
                                    "document.getElementsByTagName('html')[0].innerHTML+'</head>');");

                            try {
                                Thread.currentThread().sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }

                    }

                }

            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();    //Wait for the certificate
                // handler.cancel();      //Suspend connection
                // handler.handleMessage(null);
            }

//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                view.loadUrl(url);
//                return true; // load url in current webview
//            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) { // >= API 22
                    CookieManager.getInstance().flush();
                } else {
                    CookieSyncManager.getInstance().sync();
                }

                Log.i("[Account Fragmt]","Current URL: "+url+", Login address: "+loginAddress);
                Timber.tag("[Account Fragmt]").i("Current URL: "+url+", Login address: "+loginAddress);

                if(url.equals(loginAddress) || url.equals(loginAddressFailed)) {
                    loginSuccessful = false;
                    // Login failed
                    Log.i("[Account Fragmt]","Not Logged in");
                    Timber.tag("[Account Fragmt]").i("Not Logged in");

                } else {
//                    Log.i("[Account Fragmt]","LoginSuccessful: "+loginSuccessful);
//                    Log.i("[Account Fragmt]","DetailSuccessful: "+detailSuccessful);
//                    Log.i("[Account Fragmt]","TimetableSuccessful: "+timetableSuccessful);

                    if (!loginSuccessful) {
                        view.loadUrl("javascript:window.java_obj.showWelcomeSource('<head>'+" +
                                "document.getElementsByTagName('html')[0].innerHTML+'</head>');");
//                        view.loadUrl("javascript:window.onhashchange = function(){java_obj.showWelcomeSource('<head>'+" +
//                                "document.getElementsByTagName('html')[0].innerHTML+'</head>');};");

                        Log.i("[Account Fragmt]","Processing login...");
                        Timber.tag("[Account Fragmt]").i("Processing login...");
                        super.onPageFinished(view, url);
                    }

                    while (!loginSuccessful) {
                        try {
                            Thread.currentThread().sleep(3000);
                            Log.i("[Account Fragmt]","Waiting for login successful");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    if ((!timetableSuccessful && !detailSuccessful && loginSuccessful)) {
                        view.loadUrl(prefixAddress + timetableAddress);

                    }

                }

            }

        });

        return view;

    }

    private class InJavaScriptLocalObj
    {
        @JavascriptInterface
        public void showWelcomeSource(String html) {
            getWelcomeContent(html);
        }

        @JavascriptInterface
        public void showDetailSource(String html) {
            getDetailContent(html);
        }

        @JavascriptInterface
        public void showTimetableSource(String html) {
            getTimetableContent(html);
        }

        @JavascriptInterface
        public void showDescription(String str) {
            System.out.println("====>html=" + str);
        }
    }

    /**
     * Getting welcome page content
     * @param html
     */
    private void getWelcomeContent(final String html){
        Log.i("[Account Fragmt]", "1. Start getting welcome page content");
        Timber.tag("[Account Fragmt]").i("1. Start getting welcome page content");

        Document document = Jsoup.parse(html);
        String welcomeInfo = document.select("div#CON_PORT_RECP_TITLE").get(0).text();
        Log.i("[Account Fragmt]", welcomeInfo);
        if (welcomeInfo.contains("Welcome to MyStudentRecord")) {

            Log.i("[Account Fragmt]", "Login successful");
            Timber.tag("[Account Fragmt]").i("Login successful");

//            detailAddress = document.select("a[id=PORT_1]").get(0).attr("href");
//            Log.i("[Account Fragmt]", "Detail Address: " + detailAddress);
//            Timber.tag("[Account Fragmt]").i("Detail Address: " + detailAddress);

            timetableAddress = document.select("a[id=smTTABLE]").get(0).attr("href");
            Log.i("[Account Fragmt]", "Timetable Address: " + timetableAddress);
            Timber.tag("[Account Fragmt]").i("Timetable Address: " + timetableAddress);

            loginSuccessful = true;

        } else {
            Log.i("[Account Fragmt]", "Error occured: Login page");
            Timber.tag("[Account Fragmt]").i("Error occured: Login page");
            loginSuccessful = false;
        }


    }

    /**
     * Getting timetable content and save it to local file
     * @param html
     */
    private void getTimetableContent(final String html){
        Log.i("[Account Fragmt]", "2. Start getting timetable info");
        Timber.tag("[Account Fragmt]").i("2. Start getting timetable info");

        Document document = Jsoup.parse(html);
        String timetableInfo = document.select("h1#sitsportalpagetitle").get(0).text();
        Log.i("[Account Fragmt]", timetableInfo);

        String script = document.select("script").last().data();
        String eventList = "{ \"timetable\": " +
                StringUtils.substringBetween(script, "events: ", "});") +
                "}";
        Log.i("[Account Fragmt]", eventList);

        detailAddress = document.select("a[id=PORT_1]").get(0).attr("href");
        Log.i("[Account Fragmt]", "Detail Address: " + detailAddress);
        Timber.tag("[Account Fragmt]").i("Detail Address: " + detailAddress);

        try {
            JSONObject json = new JSONObject(eventList);
            JSONArray jArray = json.getJSONArray("timetable");
            writeIntoFile(mContext, eventList, "timetable.json", "timetable");
            timetableSuccessful = true;

        } catch (JSONException e) {
            e.printStackTrace();
            timetableSuccessful = false;
        }

    }

    /**
     * Getting detail content
     * @param html
     */
    private void getDetailContent(final String html){
        Log.i("[Account Fragmt]", "3. Start getting user detail");
        Timber.tag("[Account Fragmt]").i("3. Start getting user detail");

        Document document = Jsoup.parse(html);
//        Elements studentNum = document.select("div > p > span.data");
//        for (Element e: studentNum) {
//            Log.i("[Account Fragmt]", "Element: "+e.text());
//        }
        String detailInfo = document.select("h1#sitsportalpagetitle").get(0).text();
        Log.i("[Account Fragmt]", detailInfo);

        Element infoBox = document.select("div.container").first();
//        Log.i("[Account Fragmt]", "User Info Box: "+infoBox.html());

        String studentNum = infoBox.select("span.data").get(0).text();
        Log.i("[Account Fragmt]", "Student Number: "+studentNum);
        Timber.tag("[Account Fragmt]").i("Student Number: "+studentNum);

        String ucasNum = document.select("span.data").get(1).text();
        Log.i("[Account Fragmt]", "UCAS Number: "+ucasNum);
        Timber.tag("[Account Fragmt]").i("UCAS Number: "+ucasNum);

        String surName = document.select("span.data").get(2).text();
        Log.i("[Account Fragmt]", "Surname: "+surName);
        Timber.tag("[Account Fragmt]").i("Surname: "+surName);

        String foreName = document.select("span.data").get(3).text();
        Log.i("[Account Fragmt]", "Forename: "+foreName);
        Timber.tag("[Account Fragmt]").i("Forename: "+foreName);

        String prefName = document.select("span.data").get(4).text();
        Log.i("[Account Fragmt]", "Perffered First Name: "+prefName);
        Timber.tag("[Account Fragmt]").i("Perffered First Name: "+prefName);

        String dob = document.select("span.data").get(5).text();
        Log.i("[Account Fragmt]", "Date of Birth: "+dob);
        Timber.tag("[Account Fragmt]").i("Date of Birth: "+dob);

        String uolEmail = document.select("span.data").get(6).text();
        Log.i("[Account Fragmt]", "UoL Email: "+uolEmail);
        Timber.tag("[Account Fragmt]").i("UoL Email: "+uolEmail);

        final TextView tv = mActivity.findViewById(R.id.accountNameTextView);
        //tv.setText(studentNum);
        detailSuccessful = true;

    }

    /**
     * Write timetable into local file with JSON format
     * @param context App context, stored as global var when fragment attached
     * @param content Content to be stored locally (JSON string here)
     * @param fileName File name
     * @param folderName Name of the folder the file will be stored in.
     * @return
     */
    public File writeIntoFile (Context context, String content, String fileName, String folderName) {

        String filePath = getFilePath(context, folderName);
        File folder = new File(filePath);
        File file = new File(folder, fileName);

        try {
            if (!file.exists()) {
                Log.i("[Account Fragmt]", "File '" + file + "' doesn't exist, creating now...");
                file.createNewFile();
            }

            // Empty the file for new coming contents
//            FileWriter fileEraser = new FileWriter(filePath+File.separator+fileName);
//            fileEraser.write("");
//            fileEraser.flush();
//            fileEraser.close();

            FileWriter fileWriter = new FileWriter(filePath+File.separator+fileName);
            fileWriter.write(content);
//            FileOutputStream fos = new FileOutputStream(file);
//            DataOutputStream dos = new DataOutputStream(fos);
            fileWriter.flush();
            fileWriter.close();

            Log.i("[Account Fragmt]", "Timetable has been saved to '" + file.getPath() + "'");

        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;

    }


    /**
     * Determine if the current device has External Storage (SDCard)
     * if no, use Internal path
     * @param context App context, stored as global var when fragment attached
     * @param subDir The indicated folder name under 'files' dir
     * @return
     */
    public static String getFilePath(Context context, String subDir) {
        String filePath = "";
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ) {
            Log.i("[Account Fragmt]", "External Storage is available.");
            filePath = Objects.requireNonNull(context.getExternalFilesDir(subDir)).getAbsolutePath();

        }else{
            Log.i("[Account Fragmt]", "External Storage is unavailable, try Internal Storage.");
            filePath = context.getFilesDir()+ File.separator+subDir;

        }

        File folder = new File(filePath);

        try {
            if(!folder.exists())
                Log.i("[Account Fragmt]", "Directory '"+folder.getPath()+"' doesn't exist, creating now...");
            folder.mkdirs();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return filePath;
    }


    @Override
    public void onDestroy() {
        if (webView != null) {
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView.clearHistory();

            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }

    /**
     * Due to the different lifecycle, Activity may be recycled
     * by the system with the Fragment still existed. To prevent the
     * return value 'null' from getActivity() when the Activity is
     * recycled...
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
        this.mActivity = getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
        mContext = null;
    }

}
