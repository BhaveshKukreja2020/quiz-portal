package com.quizportal.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility for hashing and verifying passwords using BCrypt.
 * Add jbcrypt dependency to pom.xml:
 *   <dependency>
 *     <groupId>org.mindrot</groupId>
 *     <artifactId>jbcrypt</artifactId>
 *     <version>0.4</version>
 *   </dependency>
 */
public class PasswordUtil {

    private static final int SALT_ROUNDS = 10;

    /** Hashes a plain-text password. */
    public static String hash(String plainText) {
        return BCrypt.hashpw(plainText, BCrypt.gensalt(SALT_ROUNDS));
    }

    /** Returns true if plainText matches the stored hash. */
    public static boolean verify(String plainText, String hashed) {
        return BCrypt.checkpw(plainText, hashed);
    }

    private PasswordUtil() {}
}
