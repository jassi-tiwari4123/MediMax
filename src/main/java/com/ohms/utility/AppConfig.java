package com.ohms.utility;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AppConfig — loads application.properties once at startup (Singleton pattern).
 *
 * INTERVIEW POINTS:
 *   Singleton     — only one instance exists; guaranteed by static initializer.
 *   Abstraction   — callers don't know where config comes from (file, env, etc.)
 *   Static block  — properties loaded once when class is first loaded by JVM.
 */
public final class AppConfig {

    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);

    /** The loaded properties — immutable after class init */
    private static final Properties PROPS = new Properties();

    // ── Static initializer — runs once when class is loaded ──────
    static {
        try (InputStream is = AppConfig.class
                .getClassLoader()
                .getResourceAsStream("application.properties")) {

            if (is == null) {
                throw new ExceptionInInitializerError(
                    "application.properties not found on classpath");
            }
            PROPS.load(is);
            logger.info("application.properties loaded successfully.");

        } catch (IOException e) {
            throw new ExceptionInInitializerError(
                "Failed to load application.properties: " + e.getMessage());
        }
    }

    // Private constructor — prevent instantiation
    private AppConfig() {}

    // ── Accessor methods ─────────────────────────────────────────

    public static String get(String key) {
        return PROPS.getProperty(key);
    }

    public static String get(String key, String defaultValue) {
        return PROPS.getProperty(key, defaultValue);
    }

    public static int getInt(String key, int defaultValue) {
        String val = PROPS.getProperty(key);
        if (val == null) return defaultValue;
        try { return Integer.parseInt(val.trim()); }
        catch (NumberFormatException e) { return defaultValue; }
    }

    public static long getLong(String key, long defaultValue) {
        String val = PROPS.getProperty(key);
        if (val == null) return defaultValue;
        try { return Long.parseLong(val.trim()); }
        catch (NumberFormatException e) { return defaultValue; }
    }

    // ── Convenience getters for frequently used config ───────────

    public static String getDbUrl()      { return get("db.url"); }
    public static String getDbUsername() { return get("db.username"); }
    public static String getDbPassword() { return get("db.password"); }
    public static String getDbDriver()   { return get("db.driver"); }

    public static String getJwtSecret()  { return get("jwt.secret"); }
    public static long   getJwtExpiry()  { return getLong("jwt.expiry.ms", 1800000L); }

    public static String getMailHost()   { return get("mail.smtp.host"); }
    public static int    getMailPort()   { return getInt("mail.smtp.port", 587); }
    public static String getMailFrom()   { return get("mail.from.address"); }
    public static String getMailPass()   { return get("mail.from.password"); }
    public static String getMailName()   { return get("mail.from.name"); }

    public static String getAppName()    { return get("app.name", "OHMS"); }
    public static String getBaseUrl()    { return get("app.base.url"); }
    public static String getHospitalName() { return get("app.hospital.name", "City Hospital"); }
}
