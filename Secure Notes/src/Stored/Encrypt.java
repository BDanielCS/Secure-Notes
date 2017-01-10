/*
 * Encrpyt.java
 */
package Stored;

import java.math.BigInteger;

/**
 * Given some form of string information, into secured information
 * defined by the formula
 * arr[x] = (text[x] - primes[x])
 * and each unique code represented by
 * II(text[x] - primes[x])
 */
public final class Encrypt {

	/**
	 * no instances of this class are to be made
	 */
	private Encrypt() {}

	/**
	 * List of primes for user in formula calculation
	 */
	private static int[] primes = { 2, 3, 5, 7, 11, 13, 17, 23, 27, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83,
			89, 97 };

	/**
	 * Given some form of data, go character by character manipulating the string
	 * and return resulting integer
	 */
	public static int[] cipher(String input) {
		return characterManip(input);
	}

	/**
	 * Generate the unique user code from as a result of prime factorization
	 * theorem
	 */
	public static BigInteger userCode(String user) {
		BigInteger code = BigInteger.ONE;

		for (int i = 0; i < user.length(); i++) {
			code = code.multiply(BigInteger.valueOf((long) Math.pow(primes[i], (int) user.charAt(i))));
		}

		return code;
	}

	/**
	 * Apply primes to the characters in the input to get the unique code
	 * represented as an array
	 */
	private static int[] characterManip(String input) {

		if (input.equals("")) {
			return null;
		}

		int[] stringRep = new int[input.length()];

		// increasing the ascii val of each char by a prime
		for (int i = 0; i < input.length(); i++) {
			stringRep[i] = ((int) input.charAt(i) - primes[i]);
		}

		return stringRep;
	}

	/**
	 * Given integer, return the input back to its normal state of a string
	 */
	public static String decipher(int[] input) {

		if (input.equals("")) {
			return null;
		}

		StringBuffer stringTransform = new StringBuffer();

		for (int i = 0; i < input.length; i++) {
			stringTransform.append((char) ((int) (input[i] + primes[i])));
		}

		return stringTransform.toString();
	}
}
