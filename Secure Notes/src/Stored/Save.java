package Stored;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigInteger;

/**
 * Given the string and the username, save the text in
 * its proper location.  The location is organized so that the
 * username leads with all of the notes following directly after. The
 * ending notes character will denote the end of input
 */
public class Save {

	/**
	 * predefined strings to mark the start and end of each area where user information
	 * is located
	 */
	private final String END = "<END#----#USER>\n", START = "<START#----#USER>\n";

	/**
	 * file accessor
	 */
	private RandomAccessFile readWrite;

	/**
	 * creates instance which is able to read and write to the file given the active 
	 * username and the text to be added
	 */
	public Save() {}

	/**
	 * write the text to the file. If the user has written before, move to the
	 * location for that user and place their information into their
	 * location. Otherwise append
	 */
	public void write(String user, String text) throws IOException {

		readWrite = new RandomAccessFile("Notes.txt", "rw");
		BigInteger userCode = Encrypt.userCode(user);
		String codeAsString = (userCode.toString() + START).replaceAll("\\n", ""), line,
				newText = text.trim() + "\n" + END;
		StringBuffer saveFromOverwrite = new StringBuffer();
		long startPoint = 0, endPoint = 0;

		while ((line = readWrite.readLine()) != null && endPoint == 0) {
			line = removeStreamSpaces(line.trim());

			if (line.equals(codeAsString)) {
				startPoint = readWrite.getFilePointer();
			}

			if (line.equals(END.replaceAll("\\n", "")) && startPoint != 0) {
				endPoint = readWrite.getFilePointer();
			}
		}

		// nothing has been entered by this user before. go to the end
		if (startPoint == 0) {
			readWrite.seek(readWrite.length());
			readWrite.writeChars(userCode.toString() + START + newText);
		} else {
			// go to the end position and save everything after it in case of
			// overwrite
			readWrite.seek(endPoint);

			while ((line = readWrite.readLine()) != null) {
				saveFromOverwrite.append(line + "\n");
			}

			readWrite.seek(startPoint);
			readWrite.writeChars(newText + removeStreamSpaces(saveFromOverwrite.toString().trim()));

		}
		readWrite.close();
	}

	/**
	 * given the username, find and return the text that was previously saved by
	 * this user. if the user is not found in the saved file, then return null
	 */
	public String read(String user) throws IOException {

		readWrite = new RandomAccessFile("Notes.txt", "r");
		String line;
		boolean save = false;
		StringBuffer toReturn = new StringBuffer();
		BigInteger code = Encrypt.userCode(user);
		String codeAsStartString = (code.toString() + START).replaceAll("\\n", "");

		while ((line = readWrite.readLine()) != null) {
			line = removeStreamSpaces(line.trim());

			// check if we found the user
			if (line.equals(codeAsStartString)) {
				save = true;
				continue;
			}

			// found the ending of the user's notes
			if (line.equals(END.trim()) && save) {
				save = false;
				break;
			}

			if (save) {
				toReturn.append(line + "\n");
			}

		}
		readWrite.close();

		// if nothing has been saved then the user has not saved text here
		// before
		if (toReturn.length() == 0) {
			return null;
		} else {
			return toReturn.toString();
		}
	}

	/**
	 * removing odd number indexed spaces from the string which
	 * where created as a result of reading from different utf encoding
	 * that was being written from randomAccesswriter
	 */
	private String removeStreamSpaces(String input) {
		StringBuffer toRet = new StringBuffer();

		for (int i = 0; i < input.length(); i++) {
			if (i % 2 == 0) {
				toRet.append(input.charAt(i));
			}
		}

		return toRet.toString();
	}

}
