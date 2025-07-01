package com.LongChau.HealthMateLC.service;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class OtpStorage {
    private final ConcurrentMap<String, OtpData> otpStore = new ConcurrentHashMap<>();

    public void storeOtp(String username, String otp) {
        otpStore.put(username, new OtpData(otp, System.currentTimeMillis() + 5 * 60 * 1000));
    }

    public boolean verifyOtp(String username, String otp) {
        OtpData data = otpStore.get(username);
        if (data == null || System.currentTimeMillis() > data.expiryTime) {
            otpStore.remove(username);
            return false;
        }
        boolean isValid = data.otp.equals(otp);
        if (isValid) otpStore.remove(username);
        return isValid;
    }

    private static class OtpData {
        String otp;
        long expiryTime;

        OtpData(String otp, long expiryTime) {
            this.otp = otp;
            this.expiryTime = expiryTime;
        }
    }
}