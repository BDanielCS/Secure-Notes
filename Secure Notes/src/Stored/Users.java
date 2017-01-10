/*
 * Users.java
 */
package Stored;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.JOptionPane;

/**
 * store the information about the users in a safe manner using
 * the encryption cipher. which the key as the defined code in Encrypt and 
 * the value as the nested class to hold the user/pass information securely in
 * array format
 */
public class Users implements Serializable {

	/**
	 * Serialization version
	 */
	private static final long serialVersionUID = -8814926360755146022L;

	/**
	 * holds user pass information after they have been converted
	 * securely by the cipher
	 */
	private HashMap<BigInteger, UserCipheredInfo> pairs;

	/**
	 * Contains the post transformed information regarding the username and
	 * password of the user
	 */
	protected class UserCipheredInfo implements Serializable {

		/**
		 * Serialization version
		 */
		private static final long serialVersionUID = 2220296435821310205L;

		/**
		 * holds the post ciphered username and passwords
		 */
		private int[] username, password;

		/**
		 * actions as a container to hold the ciphered usernames and
		 * password
		 * @param user
		 * @param pass
		 */
		protected UserCipheredInfo(String user, String pass) {
			username = Encrypt.cipher(user);
			password = Encrypt.cipher(pass);
		}
	}

	/**
	 * create the underlying database. Load up previous accounts from the 
	 * serialization.  
	 */
	public Users() {
		pairs = null;
		open();
	}

	/**
	 * given the username and password. Convert to encrypted value and ensure
	 * the correct log in information in within the system.
	 */
	public boolean userPassValid(String user, String pass) {
		UserCipheredInfo key = pairs.get(Encrypt.userCode(user));

		if (key != null) {
			// check valid password
			if (Arrays.equals(key.password, Encrypt.cipher(pass))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * ensures that the username is within the map
	 */
	public boolean hasAccount(String user) {
		return pairs.containsKey(Encrypt.userCode(user));
	}

	/***
	 * Adds the person to the database. if there is already an account. No
	 * additions/changes are made
	 */
	public boolean addUser(String user, String pass) {
		
		//maintain the 25 charcter maximum
		if(user.length() >= 25 || pass.length() >= 25){
			return false;
		}
		
		if (!pairs.containsKey(Encrypt.userCode(user))) {
			pairs.put(Encrypt.userCode(user), new UserCipheredInfo(user, pass));
			return true;
		}
		return false;

	}

	/**
	 * Assuming the user is already in the system, remove the user from the
	 * database safely
	 */
	public boolean removeUser(String user, String pass) {
		if (pairs.containsKey(Encrypt.userCode(user))) {
			pairs.remove(Encrypt.cipher(user));
			return true;
		}
		return false;
	}

	/**
	 * load up the saved hashmap from the last serialization
	 */
	@SuppressWarnings("unchecked")
	private void open() {

		try {
			FileInputStream fileIn = new FileInputStream("Start.ser");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			pairs = (HashMap<BigInteger, UserCipheredInfo>) in.readObject();
			in.close();
			fileIn.close();
		} catch (IOException | NullPointerException ionpe) {
			// no serialization found so begin a new map
			pairs = new HashMap<BigInteger, UserCipheredInfo>();
		} catch (ClassNotFoundException c) {
			JOptionPane.showMessageDialog(null, "No Database Found");
		}
	}

	/**
	 * On close, serialize the pairs map in order to save its current state for
	 * easy load up on re-opening
	 */
	public void close() {

		try {
			FileOutputStream fileOut = new FileOutputStream("Start.ser");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(pairs);
			out.close();
			fileOut.close();
		} catch (IOException i) {
			JOptionPane.showMessageDialog(null, "File Location Not Found");
		}
	}
}
