/*
 * Copyright 2019 yt120@student.le.ac.uk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.uol.yt120.lecampus.view.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.uol.yt120.lecampus.view.NavigationActivity;
import com.uol.yt120.lecampus.R;
import com.uol.yt120.lecampus.model.domain.User;
import com.uol.yt120.lecampus.model.domain.UserEvent;
import com.uol.yt120.lecampus.utility.DateTimeCalculator;
import com.uol.yt120.lecampus.utility.DateTimeFormatter;
import com.uol.yt120.lecampus.viewModel.FootprintViewModel;
import com.uol.yt120.lecampus.viewModel.UserEventViewModel;
import com.uol.yt120.lecampus.viewModel.UserViewModel;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import timber.log.Timber;

public class AccountFragment extends Fragment {

    public WebView webView;

    private Activity mActivity;
    private Context mContext;

    private String loginAddress;
    private String loginAddressFailed;
    private String prefixAddress;
    private String detailAddress;
    private String timetableAddress;

    private UserEventViewModel userEventViewModel;
    private UserViewModel userViewModel;
    private FootprintViewModel footprintViewModel;
    private DateTimeCalculator dateTimeCalculator = new DateTimeCalculator();
    private DateTimeFormatter dateTimeFormatter = new DateTimeFormatter();

    volatile boolean userAccountObtained = false;
    volatile boolean loginSuccessful = false;
    volatile boolean timetableSuccessful = false;
    volatile boolean detailSuccessful = false;

    volatile boolean externalFileFound = false;
    volatile boolean internalFileFound = false;

    //private ProgressDialog loadingDialog;
    String profileFolderName = "profile";
    String profileFileName = "profile.json";
    String profileContent = "";

    private TextView text_username;
    private TextView text_useremail;
    private TextView text_footprintNum;
    private TextView text_footprintDistance;
    private TextView text_eventNum;
    private TextView text_header_name;
    private TextView text_header_email;

//    private static final String default_username = "NO LOGIN";
//    private static final String default_useremail = "";
//    private String login_username;
//    private String login_email;

    LoadingDialog loadingDialog = new LoadingDialog();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkFilePath();

    }


    @SuppressLint("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //loadingDialog = new ProgressDialog(mContext);
        //LoadingDialog loadingDialog = new LoadingDialog();
        userViewModel = ViewModelProviders.of(getActivity()).get(UserViewModel.class);
        userEventViewModel = ViewModelProviders.of(getActivity()).get(UserEventViewModel.class);
        loginAddress = getString(R.string.login_web_address);
        loginAddressFailed = getString(R.string.login_web_address_failed);
        prefixAddress = getString(R.string.prefix_web_address);

        getActivity().setTitle(getString(R.string.title_fragment_account));
        //checkIfloggedIn(1);

        Log.i("[Account Fragmt]","User Account Obtained: "+userAccountObtained);
        if (userAccountObtained) {
            setHasOptionsMenu(true);
            //final SimpleAdapter contentAdapter = loadUserProfileIntoAdapter(profileContent);

            View userProfileView = inflater.inflate(R.layout.fragment_account, container, false);
            ImageView avatar = userProfileView.findViewById(R.id.avatar_account);
            avatar.setImageResource(R.drawable.sample_avatar);
            text_username = userProfileView.findViewById(R.id.text_account_username);
            text_useremail = userProfileView.findViewById(R.id.text_account_useremail);
            text_footprintNum = userProfileView.findViewById(R.id.label_account_footprint_num);
            text_footprintDistance = userProfileView.findViewById(R.id.label_account_user_footprint_total_distance);
            text_eventNum = userProfileView.findViewById(R.id.label_account_user_event_interest);

            text_header_name = getActivity().findViewById(R.id.header_account_name);
            text_header_email = getActivity().findViewById(R.id.header_account_email);

            //ListView profileListView = userProfileView.findViewById(R.id.account_item_list);
            userViewModel.getUserLiveDataById(1).observe(this, new Observer<User>() {
                @Override
                public void onChanged(@Nullable User user) {
                    if (user != null) {
                        text_username.setText(user.getRealname());
                        text_useremail.setText(user.getUolEmail());
//                        text_header_name.setText(user.getRealname());
//                        text_header_name.setText(user.getUolEmail());
                        syncNavHeader(user.getRealname(), user.getUolEmail());
                    }
                }
            });



            return userProfileView;

        } else {
            setHasOptionsMenu(false);
            View loginWebView = inflater.inflate(R.layout.fragment_account_web, container, false);
            webView = loginWebView.findViewById(R.id.accountWebView);
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
                }

                /**
                 * An alternative method which can catch url's on hash change.
                 * @param view
                 * @param url
                 * @param isReload
                 */
                @SuppressLint("LogNotTimber")
                @Override
                public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                    super.doUpdateVisitedHistory(view, url, isReload);

                    Log.i("[Account Fragmt]","1. LoginSuccessful: "+loginSuccessful);
                    Log.i("[Account Fragmt]","2. TimetableSuccessful: "+timetableSuccessful);
                    Log.i("[Account Fragmt]","3. DetailSuccessful: "+detailSuccessful);

                    Log.i("[Account Fragmt]","Hash/URL changed, current URL: "+url);

                    if(!loginSuccessful) {
                        //webView.setVisibility(View.VISIBLE);

                    } else {

                        while (!timetableSuccessful && !detailSuccessful && loginSuccessful) {

                            Log.i("[Account Fragmt]","Fetching timetable...");
                            Timber.tag("[Account Fragmt]").i("Fetching timetable...");

                            try {
                                Thread.currentThread().sleep(5000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
//                                handler2=new Handler();
//                                Runnable r2=new Runnable() {
//                                    public void run() {
//                                        //Will be done after 10 seconds delay.
//                                    }
//                                };
//                                handler2.postDelayed(r2, 3000);

                            Log.i("[Account Fragmt]","Hash changed, current URL for timetable: "+url);
                            view.loadUrl("javascript:window.java_obj.showTimetableSource('<head>'+" +
                                    "document.getElementsByTagName('html')[0].innerHTML+'</head>');");

                            try {
                                Thread.currentThread().sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }


                        if (!detailAddressloaded) {

                            //loadingDialog.setMessage(mActivity.getString(R.string.progress_dialog_fetch_user_detail));
                            loadingDialog.setTitle(mActivity.getString(R.string.progress_dialog_fetch_user_detail));

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
//                                handler3=new Handler();
//                                Runnable r3=new Runnable() {
//                                    public void run() {
//                                        //Will be done after 10 seconds delay.
//                                    }
//                                };
//                                handler3.postDelayed(r3, 3000);
                                view.loadUrl("javascript:window.java_obj.showDetailSource('<head>'+" +
                                        "document.getElementsByTagName('html')[0].innerHTML+'</head>');");

                                try {
                                    Thread.currentThread().sleep(3000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                //loadingDialog.setMessage(mActivity.getString(R.string.progress_dialog_config_account));
                                loadingDialog.setTitle(mActivity.getString(R.string.progress_dialog_config_account));

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


                @SuppressLint("LogNotTimber")
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);

                    CookieManager.getInstance().flush();

                    Log.i("[Account Fragmt]","Current URL: "+url+", Login address: "+loginAddress);

                    if(url.equals(loginAddress) || url.equals(loginAddressFailed)) {
                        loginSuccessful = false;
                        // Login failed
                        Log.i("[Account Fragmt]","Not Logged in");

                    } else {

                        if (!loginSuccessful) {

                            loadingDialog.init(mContext);
                            loadingDialog.setTitle(mActivity.getString(R.string.progress_dialog_check_login_status));
                            loadingDialog.run();
                            //webView.setVisibility(View.INVISIBLE);

                            view.loadUrl("javascript:window.java_obj.showWelcomeSource('<head>'+" +
                                    "document.getElementsByTagName('html')[0].innerHTML+'</head>');");
//                        view.loadUrl("javascript:window.onhashchange = function(){java_obj.showWelcomeSource('<head>'+" +
//                                "document.getElementsByTagName('html')[0].innerHTML+'</head>');};");

                            Log.i("[Account Fragmt]","Processing login...");
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

                            loadingDialog.setTitle(mActivity.getString(R.string.progress_dialog_retrieve_timetable));

                            view.loadUrl(prefixAddress + timetableAddress);

                        }

                    }

                }

            });

            return loginWebView;
        }

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
     * Getting welcome page content to verify if successfully logged in
     * @param html string content of html page
     */
    @SuppressLint("LogNotTimber")
    private void getWelcomeContent(final String html){
        Log.i("[Account Fragmt]", "1. Start getting welcome page content");
        Timber.tag("[Account Fragmt]").i("1. Start getting welcome page content");

        Document document = Jsoup.parse(html);
        String welcomeInfo = document.select("div#CON_PORT_RECP_TITLE").get(0).text();
        Log.i("[Account Fragmt]", welcomeInfo);
        if (welcomeInfo.contains("Welcome to MyStudentRecord")) {

            Log.i("[Account Fragmt]", "Login successful");

            timetableAddress = document.select("a[id=smTTABLE]").get(0).attr("href");
            Log.i("[Account Fragmt]", "Timetable Address: " + timetableAddress);

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
    @SuppressLint("LogNotTimber")
    private void getTimetableContent(final String html){
        Log.i("[Account Fragmt]", "2. Start getting timetable info");

        Document document = Jsoup.parse(html);
        String timetableInfo = document.select("h1#sitsportalpagetitle").get(0).text();
        Log.i("[Account Fragmt]", timetableInfo);

        String script = document.select("script").last().data();
        String eventList = "{ \"timetable\": " +
                StringUtils.substringBetween(script, "events: ", "});") + "}";
        Log.i("[Account Fragmt]", eventList);

        detailAddress = document.select("a[id=PORT_1]").get(0).attr("href");
        Log.i("[Account Fragmt]", "Detail Address: " + detailAddress);

        try {
            JSONObject json = new JSONObject(eventList);
            JSONArray jArray = json.getJSONArray("timetable");

            for (int i=0; i<jArray.length(); i++) {
                JSONObject eventJSON = jArray.getJSONObject(i);

                /*
                    Some value are hard-coded because the timetable
                    data are from university website and can only be
                    retrieved by logging to student account.

                    This seems to be the only way to add some info before
                    saving them to local SQLite database.
                 */

                String holdBy = "University";
                String eventType = eventJSON.getString("moduleType");
                String eventTitle = eventJSON.getString("moduleName");
                String eventDesc = eventJSON.getString("description");
                String eventCode = eventJSON.getString("moduleID");

                String location = eventJSON.getString("building");
                String address = eventJSON.getString("moduleRoom");
                String postCode = "LE1 7RH";
                String city = "Leicester";
                String region = "East Midland";
                String country = "United Kingdom";

                String lat = eventJSON.getString("buildingLatitude");
                String lon = eventJSON.getString("buildingLongitude");

                String startTime =
                        dateTimeFormatter.formatDateToString(dateTimeCalculator.getNewDateBy(5, "month",
                                dateTimeFormatter.parseStringToDate(eventJSON.getString("start"),"default")), "default");

                String endTime =
                        dateTimeFormatter.formatDateToString(dateTimeCalculator.getNewDateBy(5, "month",
                                dateTimeFormatter.parseStringToDate(eventJSON.getString("end"),"default")), "default");

                String duration = eventJSON.getString("eventDuration");
                String weekDay = eventJSON.getString("moduleWeekDay");

                String host = eventJSON.getString("lecturer");
                String email = "Not provided";

                UserEvent userEvent =
                        new UserEvent(holdBy, eventType, eventTitle, eventDesc, eventCode,
                                    location, address, postCode, city, region, country,
                                lat, lon, startTime, endTime, duration, weekDay, host, email);

                userEventViewModel.insert(userEvent);
            }

            //writeIntoFile(mContext, eventList, "timetable.json", "timetable");
            timetableSuccessful = true;

        } catch (Exception e) {
//            e.printStackTrace();
            timetableSuccessful = false;
        }

    }

    /**
     * Getting detail content
     * @param html
     */
    @SuppressLint("LogNotTimber")
    private void getDetailContent(final String html){
        Log.i("[Account Fragmt]", "3. Start getting user detail");

        Document document = Jsoup.parse(html);
        Map<String, String> detailHashmap = new HashMap<String, String>();

        String detailInfo = document.select("h1#sitsportalpagetitle").get(0).text();
        Log.i("[Account Fragmt]", detailInfo);

        Element infoBox = document.select("div.container").first();

        String studentNum = infoBox.select("span.data").get(0).text();
        Log.i("[Account Fragmt]", "Student Number: "+studentNum);

        String ucasNum = document.select("span.data").get(1).text();
        String surName = document.select("span.data").get(2).text();
        String foreName = document.select("span.data").get(3).text();
        String prefName = document.select("span.data").get(4).text();
        String dob = document.select("span.data").get(5).text();
        String uolEmail = document.select("span.data").get(6).text();

        User user = new User(studentNum, ucasNum, foreName+" "+surName, prefName, dob, uolEmail);

        userViewModel.deleteAllUser();
        userViewModel.insert(user);
        userAccountObtained = true;

        detailHashmap.put("Student_Number", studentNum);
        detailHashmap.put("UCAS_Number", ucasNum);
        detailHashmap.put("Name", foreName+" "+surName);
        detailHashmap.put("Preferred_First_Name", prefName);
        detailHashmap.put("Date_of_Birth", dob);
        detailHashmap.put("UoL_Email", uolEmail);

        try {
            JSONObject detailJSON = new JSONObject(detailHashmap);
            String userDetail = "{ \"user\": ["+detailJSON.toString()+"]}";
            Log.i("[Account Fragmt]", "User JSON detail: "+userDetail);
            writeIntoFile(mContext, userDetail, profileFileName, profileFolderName);

            //webView.setVisibility(View.VISIBLE);
            loadingDialog.hide();
            detailSuccessful = true;
            showTimetable();

        } catch (Exception e) {
            Log.i("[Account Fragmt]", "Webpage is not fully loaded, retry");
            //e.printStackTrace();
            detailSuccessful = false;
        }

    }


    /**
     * Read timetable info from timetable.json
     * @param fileName The file path of timetable.json
     * @return a string "result"
     */
    public String readFromFile(String fileName) {
        Log.i("[Account Fragmt]", "Read user profile from: "+fileName);
        String result = "";
        BufferedReader reader = null;

        try {
            //FileInputStream fis = mContext.openFileInput(fileName);
            FileInputStream fis = new FileInputStream (new File(fileName));
            StringBuffer buffer = new StringBuffer();
            reader = new BufferedReader(new InputStreamReader(fis));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            result = buffer.toString();
            Log.i("[Account Fragmt]", "Read JSON: "+result);

            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Write timetable into local file with JSON format
     * @param context App context, stored as global var when fragment attached
     * @param content Content to be stored locally (JSON string here)
     * @param fileName File name
     * @param folderName Name of the folder the file will be stored in.
     * @return
     */
    @SuppressLint("LogNotTimber")
    public File writeIntoFile (Context context, String content, String fileName, String folderName) {

        String filePath = getFilePath(context, folderName);
        File folder = new File(filePath);
        File file = new File(folder, fileName);

        try {
            if (!file.exists()) {
                Log.i("[Account Fragmt]", "File '" + file + "' doesn't exist, creating now...");
                file.createNewFile();
            }

            FileWriter fileWriter = new FileWriter(filePath+File.separator+fileName);
            fileWriter.write(content);
            fileWriter.flush();
            fileWriter.close();

            Log.i("[Account Fragmt]", fileName+" has been saved to '" + file.getPath() + "'");

        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;

    }

    /**
     *  Using viewModel instead of this
     * @param profileContent
     * @return
     */
    public SimpleAdapter loadUserProfileIntoAdapter(String profileContent) {

        SimpleAdapter finalAdapter = null;
        String[] from = {"Name", "UoL_Email"};
        //int[] to = {R.id.user_name, R.id.user_email};
        ArrayList<HashMap<String, String>> profileArrayList = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> profileHashmap;

        try {
            JSONObject json = new JSONObject(profileContent);
            JSONArray jArray = json.getJSONArray("user");

            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jsonProfile = jArray.getJSONObject(i);

                String studentNum = jsonProfile.getString("Student_Number");
                //Log.i("[Account Fragmt]", "Student Num: "+studentNum);

                String ucasNum = jsonProfile.getString("UCAS_Number");
                //Log.i("[Account Fragmt]", "UCAS Num: "+ucasNum);

                String userName = jsonProfile.getString("Name");
                //Log.i("[Account Fragmt]", "Name: "+userName);

                String prefName = jsonProfile.getString("Preferred_First_Name");
                //Log.i("[Account Fragmt]", "Pref. Name: "+prefName);

                String dob = jsonProfile.getString("Date_of_Birth");
                //Log.i("[Account Fragmt]", "DoB: "+dob);

                String UoLemail = jsonProfile.getString("UoL_Email");
                //Log.i("[Account Fragmt]", "email: "+UoLemail);

                profileHashmap = new HashMap<String, String>();
                profileHashmap.put("Student_Number", "" + studentNum);
                profileHashmap.put("UCAS_Number", "" + ucasNum);
                profileHashmap.put("Name", "" + userName);
                profileHashmap.put("Preferred_First_Name", "" + prefName);
                profileHashmap.put("Date_of_Birth", "" + dob);
                profileHashmap.put("UoL_Email", "" + UoLemail);
                profileArrayList.add(profileHashmap);

            }

            //SimpleAdapter adapter = new SimpleAdapter(mContext, profileArrayList, R.layout.fragment_account_item, from, to);
            //finalAdapter = adapter;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return finalAdapter;
    }


    /**
     * Determine if the current device has External Storage (SDCard)
     * if no, use Internal path
     * @param context App context, stored as global var when fragment attached
     * @param subDir The indicated folder name under 'files' dir
     * @return
     */
    @SuppressLint("LogNotTimber")
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
        super.onDestroy();
        if (webView != null) {
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView.clearHistory();

            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.destroy();
            webView = null;
        }

    }

    public void showTimetable() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.fragment_container, new TimetableFragment(), TimetableFragment.TAG)
                .commit();

    }

    @SuppressLint("LogNotTimber")
    public void logoutAccount() {
        String finalResult = "";
        String resultEx1 = "";
        String resultEx2 = "";
        String resultIn1 = "";
        String resultIn2 = "";

        String internalTimetableFilePath = mContext.getFilesDir()+ File.separator+"timetable";
        String internalProfileFilePath = mContext.getFilesDir()+ File.separator+profileFolderName;
        String externalTimetableFilePath = Objects.requireNonNull(mContext.getExternalFilesDir("timetable")).getAbsolutePath();
        String externalProfileFilePath = Objects.requireNonNull(mContext.getExternalFilesDir(profileFolderName)).getAbsolutePath();

        File internalTimetableFolder = new File(internalTimetableFilePath);
        File internalProfileFolder = new File(internalProfileFilePath);
        File externalTimetableFolder = new File(externalTimetableFilePath);
        File externalProfileFolder = new File(externalProfileFilePath);

        File internalTimetableFile = new File(internalTimetableFolder, "timetable.json");
        File internalProfileFile = new File(internalProfileFolder, profileFileName);
        File externalTimetableFile = new File(externalTimetableFolder, "timetable.json");
        File externalProfileFile = new File(externalProfileFolder, profileFileName);

        if (externalTimetableFile.exists())
            externalTimetableFile.delete();
            resultEx1 = "Timetable file in External Storage deleted";
            //Log.i("[Account Fragmt]", resultEx1);

        if (externalProfileFile.exists())
            externalProfileFile.delete();
            resultEx2 = "User Profile in External Storage deleted";
            //Log.i("[Account Fragmt]", resultEx2);

        if (internalTimetableFile.exists()) {
            internalTimetableFile.delete();
            resultIn1 = "Timetable file in Internal Storage deleted";
            //Log.i("[Account Fragmt]", resultIn1);
        }

        if (internalProfileFile.exists()) {
            internalProfileFile.delete();
            resultIn2 = "User Profile in Internal Storage deleted";
            //Log.i("[Account Fragmt]", resultIn2);
        }

        userViewModel.deleteAllUser();
        userEventViewModel.deleteAll();

        externalFileFound = false;
        internalFileFound = false;
        userAccountObtained = false;

    }

    /**
     *
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_fragment_account, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_log_out_account:
                logoutAccount();

                Toast.makeText(getActivity(), "You have been logged out.", Toast.LENGTH_SHORT).show();
                Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (currentFragment instanceof AccountFragment) {
                    FragmentTransaction fragTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragTransaction.detach(currentFragment);
                    fragTransaction.attach(currentFragment);

                    fragTransaction.commit();}

                return true;

            case R.id.action_settings_account:
                Toast.makeText(getActivity(), "This function is temporarily unavailable", Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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

    class LoadingDialog extends Thread implements Runnable {
        private ProgressDialog loadingDialog;

        public void init(Context context) {
            Log.i("[Account Fragmt Thread]", "Loading Dialog initialised");
            loadingDialog = new ProgressDialog(context);
        }

        public void setTitle(String title) {
            Log.i("[Account Fragmt Thread]", "Title of Loading Dialog changed");
            loadingDialog.setMessage(title);
        }

        @Override
        public void run() {
            //while (!userAccountObtained) {
                loadingDialog.setCancelable(false);
                loadingDialog.setCanceledOnTouchOutside(false);
                loadingDialog.show();
            //}
        }

        public void hide() {
            loadingDialog.dismiss();
        }
    }

    private void checkFilePath() {
        String internalFilePath = mContext.getFilesDir()+ File.separator+profileFolderName;
        String externalFilePath = Objects.requireNonNull(mContext.getExternalFilesDir(profileFolderName)).getAbsolutePath();

        File internalFolder = new File(internalFilePath);
        File externalFolder = new File(externalFilePath);

        File internalFile = new File(internalFolder, profileFileName);
        File externalFile = new File(externalFolder, profileFileName);

        if (!externalFile.exists() || externalFile==null) {
            //Log.i("[Account Fragmt]", "User Profile in external storage not found");
            externalFileFound = false;
        } else {
            //Log.i("[Account Fragmt]", "Found User Profile in external storage");
            externalFileFound = true;
        }

        if (!internalFile.exists() || internalFile==null) {
            //Log.i("[Account Fragmt]", "User Profile in internal storage not found");
            internalFileFound = false;
        } else {
            //Log.i("[Account Fragmt]", "Found User Profile in internal storage");
            internalFileFound = true;
        }

        if (externalFileFound) {
            userAccountObtained = true;
            //profileContent = readFromFile(externalFilePath+File.separator+profileFileName);

        } else if (internalFileFound) {
            userAccountObtained = true;
            //profileContent = readFromFile(internalFilePath+File.separator+profileFileName);

        } else {
            userAccountObtained = false;
            //profileContent = "";
        }
    }

    private void checkIfloggedIn(int userId) {
        try {
            LiveData<User> user = userViewModel.getUserLiveDataById(userId);
            if (user != null) {
                Log.w("[Login status]", "found user logged in: ["+user.getValue().getRealname()+"]");
                userAccountObtained = true;
            } else {
                Log.w("[Login status]", "No user logged in");
                userAccountObtained = false;
            }
        } catch (Exception e) {
            Log.w("[Login status]", "==================== Exception ========================");
            e.printStackTrace();
            userAccountObtained = false;
        }
    }

    public void syncNavHeader(String name, String email) {
        try {
            ((NavigationActivity)getActivity()).setupHeaderInfo(name, email);
            Log.i("[Account Fragment]", "User Info updated: "+name+", "+email);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
