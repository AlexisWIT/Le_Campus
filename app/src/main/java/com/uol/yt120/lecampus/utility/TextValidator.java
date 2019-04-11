package com.uol.yt120.lecampus.utility;

import android.text.TextUtils;

public class TextValidator {

    public boolean isEmptyString(String string) {
        return string == null ||
                string.equalsIgnoreCase("null") ||
                (TextUtils.equals(string, "null")) ||
                (TextUtils.isEmpty(string));
    }
}
