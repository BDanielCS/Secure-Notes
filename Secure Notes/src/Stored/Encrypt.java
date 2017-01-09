/*
 * Encrpyt.java
 */
package Stored;

import java.math.BigInteger;

/*
 * Given some form of string information, convert it using the
 * formula defined below
 */
public final class Encrypt {

	private Encrypt() {}

	private static int[] primes = { 2, 3, 5, 7, 11, 13, 17, 23, 27, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83,
			89, 97 };

	/*
	 * Givensome form of data, go character by character manipulating the string
	 * and return resulting integer
	 */
	public static int[] cipher(String input) {
		return character_manip(input);
	}

	/*
	 * Generate the unique user code from as a result of prime factorization
	 * theorem
	 */
	public static BigInteger user_code(String user) {
		BigInteger code = BigInteger.ONE;

		for (int i = 0; i < user.length(); i++) {
			code = code.multiply(BigInteger.valueOf((long) Math.pow(primes[i], (int) user.charAt(i))));
		}

		return code;
	}

	/*
	 * Apply primes to the characters in the input to get the unique code
	 * represented as an array
	 */
	private static int[] character_manip(String input) {

		if (input.equals("")) {
			return null;
		}

		int[] string_rep = new int[input.length()];

		// increasing the ascii val of each char by a prime
		for (int i = 0; i < input.length(); i++) {
			string_rep[i] = ((int) input.charAt(i) - primes[i]);
		}

		return string_rep;
	}

	/*
	 * Given integer, return the input back to its normal state of a string
	 */
	public static String decipher(int[] input) {

		if (input.equals("")) {
			return null;
		}

		StringBuffer string_transform = new StringBuffer();

		for (int i = 0; i < input.length; i++) {
			string_transform.append((char) ((int) (input[i] + primes[i])));
		}

		return string_transform.toString();
	}
}
