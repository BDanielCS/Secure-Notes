/*
 * Exec.java
 */
package messaging;

import javax.swing.SwingUtilities;

/*
 * Create a chatting interface that is able to communate with
 * varying number of users remote logging in
 */
public class Exec {

	public static void main(String[] args) {

		SwingUtilities.invokeLater(() -> {
			new Window();
		});

	}

}
