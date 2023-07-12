package com.antplay.binding;

import android.content.Context;

import com.antplay.binding.crypto.AndroidCryptoProvider;
import com.antplay.nvstream.http.LimelightCryptoProvider;

public class PlatformBinding {
    public static LimelightCryptoProvider getCryptoProvider(Context c) {
        return new AndroidCryptoProvider(c);
    }
}
