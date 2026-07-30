package com.ohms;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class HashGenerator {
    public static void main(String[] args) {
        String hash = BCrypt.withDefaults()
                .hashToString(12, "Admin@1234".toCharArray());
        System.out.println("=== Run this SQL in MySQL Workbench ===");
        System.out.println();
        System.out.println("UPDATE users SET password_hash = '"
                + hash
                + "' WHERE email = 'admin@ohms.com';");
    }
}
