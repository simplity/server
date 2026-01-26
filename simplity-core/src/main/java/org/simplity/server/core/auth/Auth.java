package org.simplity.server.core.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Auth related utilities
 *
 * @author simplity.org
 */
public class Auth {
	private static final SecureRandom secureRandom = new SecureRandom();

	private Auth() {
		//
	}

	// Cost factor: 10–14 depending on performance
	private static final int WORK_FACTOR = 12;

	/**
	 * Generate a secure random token encoded in URL-safe base64 format
	 *
	 * @return the generated token
	 */
	public static String generateToken() {
		byte[] bytes = new byte[32]; // 256 bits
		secureRandom.nextBytes(bytes);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
	}

	/**
	 * Hash a password for storage
	 *
	 * @param password the password to hash
	 * @return the hashed password
	 */
	public static String hash(String password) {

		String salt = BCrypt.gensalt(WORK_FACTOR);
		return BCrypt.hashpw(password, salt);
	}

	/**
	 * Verify a supplied password against stored bcrypt hash
	 *
	 * @param password   the password to verify
	 * @param storedHash the stored hash
	 * @return true if the password matches the hash
	 */
	public static boolean verify(String password, String storedHash) {
		if (storedHash == null || storedHash.isEmpty()) {
			return false;
		}
		return BCrypt.checkpw(password, storedHash);
	}

	/**
	 * to be used for hashing tokens for storage
	 *
	 * @param value
	 * @return sha256 hash of the value, encoded in url-safe base64 format
	 */
	public static String sha256(String value) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] digest = md.digest(value.getBytes(StandardCharsets.UTF_8));
			return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
