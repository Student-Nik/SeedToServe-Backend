package com.seedtoserve.util;


import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HmacSHA256Util {
	
	/*
	 Concept	                       Meaning
     HMAC	             Hash-based Message Authentication Code
     SHA-256	         Secure hashing algorithm (256-bit)
     Purpose	         Ensure message authenticity (proof it’s really from Razorpay)
     Secret key	         Shared only between you and Razorpay — never public
     Result	             Prevents fake payments and data tampering
	 */
	
	public static String hmacSha256Hex(String data, String secret) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256");
        sha256_HMAC.init(secret_key);

        byte[] hash = sha256_HMAC.doFinal(data.getBytes("UTF-8"));

        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b)); // convert byte to hex
        }
        return sb.toString();
    }
}
