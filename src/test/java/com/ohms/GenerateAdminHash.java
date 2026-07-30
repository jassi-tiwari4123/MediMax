package com.ohms;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * Run this ONCE to generate the BCrypt hash for the default admin password.
 *
 * HOW TO RUN IN IntelliJ:
 *   Right-click this file → Run 'GenerateAdminHash.main()'
 *
 * THEN copy the printed hash and run this SQL in MySQL:
 *   UPDATE users
 *   SET password_hash = '<paste_hash_here>'
 *   WHERE email = 'admin@ohms.com';
 */
public class GenerateAdminHash {

    public static void main(String[] args) {
        String password = "Admin@1234";
        String hash = BCrypt.withDefaults().hashToString(12, password.toCharArray());

        System.out.println("==============================================");
        System.out.println("Password : " + password);
        System.out.println("BCrypt   : " + hash);
        System.out.println("==============================================");
        System.out.println();
        System.out.println("Run this SQL in MySQL Workbench:");
        System.out.println();
        System.out.println("UPDATE users");
        System.out.println("SET password_hash = '" + hash + "'");
        System.out.println("WHERE email = 'admin@ohms.com';");
    }
}
