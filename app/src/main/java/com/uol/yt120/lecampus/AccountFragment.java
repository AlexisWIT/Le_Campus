package com.uol.yt120.lecampus;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.Objects;

import timber.log.Timber;

public class AccountFragment extends Fragment {

    public WebView webView;
    private String loginAddress;
    private String loginAddressFailed;
    private String prefixAddress;
    private String detailAddress;
    private String timetableAddress;
    private boolean loginSuccessful;

    @SuppressLint("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        loginAddress = getString(R.string.login_web_address);
        loginAddressFailed = getString(R.string.login_web_address_failed);
        prefixAddress = getString(R.string.prefix_web_address);

        getActivity().setTitle(getString(R.string.title_fragment_account));

        View view=inflater.inflate(R.layout.fragment_account_web, container, false);
        webView = view.findViewById(R.id.accountWebView);
        webView.loadUrl(loginAddress);

        webView.addJavascriptInterface(new InJavaScriptLocalObj(), "java_obj");

        // Disable page zoom
        webView.getSettings().setSupportZoom(false);

        // Disable built in zoom control
        webView.getSettings().setBuiltInZoomControls(false);

        // Enable DOM Storage API
        webView.getSettings().setDomStorageEnabled(true);

        webView.requestFocus();
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);

        // Enable Javascript
        webView.getSettings().setJavaScriptEnabled(true);

        // Force links and redirects to open in the WebView instead of in a browser
        webView.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.i("[Account Fragmt]","Loading...");
                Timber.tag("[Account Fragmt]").i("Loading...");
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();    //Wait for the certificate
                // handler.cancel();      //Suspend connection
                // handler.handleMessage(null);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Timber.tag("[Account Fragmt]").i("Current URL: "+url+", Login address: "+loginAddress);
                if(url.equals(loginAddress) || url.equals(loginAddressFailed)) {
                    loginSuccessful = false;
                    // Login failed
                    Log.i("[Account Fragmt]","Not Logged in");
                    Timber.tag("[Account Fragmt]").i("Not Logged in");

                } else {
                    view.loadUrl("javascript:window.java_obj.showWelcomeSource('<head>'+" +
                            "document.getElementsByTagName('html')[0].innerHTML+'</head>');");

                    Log.i("[Account Fragmt]","Processing...");
                    Timber.tag("[Account Fragmt]").i("Processing...");

                    int i = 1;
                    while (loginSuccessful = false) {
                        i += 1;
                        if (i%30==0) {
                            System.out.print(">");
                        }
                    }

                    view.loadUrl(prefixAddress+detailAddress);
                    view.loadUrl("javascript:window.java_obj.showDetailSource('<head>'+" +
                            "document.getElementsByTagName('html')[0].innerHTML+'</head>');");

                    view.loadUrl(prefixAddress+timetableAddress);
                    view.loadUrl("javascript:window.java_obj.showTimetableSource('<head>'+" +
                            "document.getElementsByTagName('html')[0].innerHTML+'</head>');");

                    super.onPageFinished(view, url);

                }

            }

        });

        return view;

    }

    public final class InJavaScriptLocalObj
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
        Log.i("[Account Fragmt]", "Start getting web content");
        Timber.tag("[Account Fragmt]").i("Start getting web content");

        Document document = Jsoup.parse(html);
        String welcomeInfo = document.select("div[id=CON_PORT_RECP_TITLE]").get(0).text();

        if (welcomeInfo.contains("Welcome to MyStudentRecord")) {
            loginSuccessful = true;

            Log.i("[Account Fragmt]", "Login successful");
            Timber.tag("[Account Fragmt]").i("Login successful");

            detailAddress = document.select("a[id=PORT_1]").get(0).attr("href");
            Log.i("[Account Fragmt]", "Detail Address: " + detailAddress);
            Timber.tag("[Account Fragmt]").i("Detail Address: " + detailAddress);

            timetableAddress = document.select("a[id=smTTABLE]").get(0).attr("href");
            Log.i("[Account Fragmt]", "Timetable Address: " + timetableAddress);
            Timber.tag("[Account Fragmt]").i("Timetable Address: " + timetableAddress);

        }

    }

    /**
     * Getting detail content
     * @param html
     */
    private void getDetailContent(final String html){
        Log.i("[Account Fragmt]", "Start getting user detail");
        Timber.tag("[Account Fragmt]").i("Start getting user detail");

        Document document = Jsoup.parse(html);
        String studentNum = document.select("span[class=data]").get(0).text();
        Log.i("[Account Fragmt]", "Student Number: "+studentNum);
        Timber.tag("[Account Fragmt]").i("Student Number: "+studentNum);

        String ucasNum = document.select("span[class=data]").get(1).text();
        Log.i("[Account Fragmt]", "UCAS Number: "+ucasNum);
        Timber.tag("[Account Fragmt]").i("UCAS Number: "+ucasNum);

        String surName = document.select("span[class=data]").get(2).text();
        Log.i("[Account Fragmt]", "Surname: "+surName);
        Timber.tag("[Account Fragmt]").i("Surname: "+surName);

        String foreName = document.select("span[class=data]").get(3).text();
        Log.i("[Account Fragmt]", "Forename: "+foreName);
        Timber.tag("[Account Fragmt]").i("Forename: "+foreName);

        String prefName = document.select("span[class=data]").get(4).text();
        Log.i("[Account Fragmt]", "Perffered First Name: "+prefName);
        Timber.tag("[Account Fragmt]").i("Perffered First Name: "+prefName);

        String dob = document.select("span[class=data]").get(5).text();
        Log.i("[Account Fragmt]", "Date of Birth: "+dob);
        Timber.tag("[Account Fragmt]").i("Date of Birth: "+dob);

        String uolEmail = document.select("span[class=data]").get(6).text();
        Log.i("[Account Fragmt]", "UoL Email: "+uolEmail);
        Timber.tag("[Account Fragmt]").i("UoL Email: "+uolEmail);
    }

    /**
     * Getting timetable content
     * @param html
     */
    private void getTimetableContent(final String html){
        Document document = Jsoup.parse(html);
        Element timetableElement = document.select("div[class=sv-list-group-item]:has(script)").first();

        String script = timetableElement.select("script").get(0).text();
        String eventList = "{ 'timetable': " +
                StringUtils.substringBetween(script, "events: ", "});") +
                "}";

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

}
