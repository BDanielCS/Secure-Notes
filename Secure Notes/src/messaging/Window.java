/*
 * Window.java
 */
package messaging;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;

import Stored.Save;
import Stored.Users;

/*
 * create the window for which the log in feature and the chat 
 * will be displayed
 */
public class Window {

	private JFrame chat_window;
	private JDialog login_window;
	private Dimension chat_dim, login_dim;
	private final int TEXT_BOX_HEIGHT_FACTOR = 2;
	private GridBagConstraints gblogin, gbchat;
	private Users info_nexus;
	private final float FONT = 28f;
	private Thread chatWindowLoad;
	public final static int MAX_STRING_LENGTH = 25;
	private final Color BACKGROUND_COLOR = Color.decode("#5CB3FF");
	private Save notes;
	private JTextArea display;

	/*
	 * initialize some starting values for the frame
	 */
	public Window() {

		// high dpi screen acclimation by using system feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			System.out.println("System DPI Alterations Failure");
			e.printStackTrace();
		}

		chat_window = new JFrame();
		chat_window.setVisible(false);

		login_window = new JDialog(null, "Login", Dialog.ModalityType.APPLICATION_MODAL);

		chat_dim = new Dimension(1920, 1080);
		login_dim = new Dimension(940, 540);
		gblogin = new GridBagConstraints();
		gbchat = new GridBagConstraints();
		info_nexus = new Users();
		notes = new Save();

		// load up main window in parallel
		Runnable load_chat = () -> {
			init_chat_app();
		};
		chatWindowLoad = new Thread(load_chat);
		chatWindowLoad.start();

		init_login();

		// Ensure thread joins in case no successful logins
		try {
			chatWindowLoad.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/*
	 * set up the application front end itself including the text windows with
	 * menu bar and active members
	 */
	private void init_chat_app() {

		// chat application window initialization
		chat_window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		// saving state of info_nexus on closure
		chat_window.addWindowListener(new WindowListener() {

			@Override
			public void windowActivated(WindowEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowClosed(WindowEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowClosing(WindowEvent arg0) {
				info_nexus.close();
				chat_window.dispose();

			}

			@Override
			public void windowDeactivated(WindowEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeiconified(WindowEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowIconified(WindowEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowOpened(WindowEvent arg0) {
				// TODO Auto-generated method stub

			}

		});

		chat_window.setPreferredSize(chat_dim);
		chat_window.setMinimumSize(chat_dim);
		chat_window.setLocationRelativeTo(null);
		chat_window.getContentPane().setBackground(BACKGROUND_COLOR);
		chat_window.setLayout(new BorderLayout());

		// CENTER Text area
		JPanel center = new JPanel(new GridBagLayout());

		gbchat.fill = GridBagConstraints.HORIZONTAL;
		gbchat.gridwidth = GridBagConstraints.REMAINDER;

		JScrollPane centerScroll = new JScrollPane();

		display = new JTextArea(300, 40);
		display.setFont(display.getFont().deriveFont(FONT));
		display.setLineWrap(true);
		center.setBackground(Color.decode("#99ebff"));
		centerScroll.setViewportView(display);
		centerScroll.setPreferredSize(new Dimension(1200, 800));

		center.add(centerScroll);

		chat_window.add(center, BorderLayout.CENTER);

		// BEGIN MENU BAR
		JMenuBar menu = new JMenuBar();
		JMenu pageMenu = new JMenu("General");
		pageMenu.addSeparator();
		JMenu advanced = new JMenu("Advanced");
		advanced.addSeparator();
		JMenu analysis = new JMenu("Analysis");
		analysis.setToolTipText("Enabled settings display in sidebars");

		JMenuItem saveChat = new JMenuItem("Save Chat");
		saveChat.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					notes.write(chat_window.getTitle(), display.getText());
				} catch (IOException e) {
					JOptionPane.showMessageDialog(chat_window, "Save Location Corrupted");
				}

			}

		});
		pageMenu.add(saveChat);

		JMenuItem clear = new JMenuItem("Clear Window");
		clear.addActionListener(action -> {
			display.setText("");
		});
		pageMenu.add(clear);

		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int response = JOptionPane.showConfirmDialog(chat_window, "Are you sure you want to quit?");

				if (response == JOptionPane.YES_OPTION) {
					// save the text somewhere
					saveChat.doClick();
					chat_window.dispose();
				}
			}

		});
		pageMenu.add(exit);

		menu.add(pageMenu);

		JMenuItem find = new JMenuItem("Find");
		find.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				String toFind = JOptionPane.showInputDialog("What word would you like to find?");
				String allText = display.getText();
				int pos = -1;

				Highlighter highlighter = display.getHighlighter();
				HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);

				if (toFind.equals("")) {
					JOptionPane.showMessageDialog(chat_window, "Invalid Search");
				} else {
					try {
						// keep working here
						while ((pos = allText.indexOf(toFind, pos + 1)) != -1) {
							highlighter.addHighlight(pos, pos + toFind.length(), painter);

						}
					} catch (BadLocationException e) {
						e.printStackTrace();
					}

				}

				display.revalidate();

			}

		});
		advanced.add(find);

		JMenuItem clearFind = new JMenuItem("Clear Selections");
		clearFind.addActionListener(action -> {
			display.getHighlighter().removeAllHighlights();
		});
		advanced.add(clearFind);

		JMenuItem replaceAll = new JMenuItem("Replace All");
		// action
		advanced.add(replaceAll);

		menu.add(advanced);

		JCheckBoxMenuItem wordLetterFreq = new JCheckBoxMenuItem("Word/Letter Histogram");
		// act
		analysis.add(wordLetterFreq);

		JCheckBoxMenuItem topWords = new JCheckBoxMenuItem("Word Pairs/Triplets");
		// act
		analysis.add(topWords);

		menu.add(analysis);

		// Left information panel
		JPanel west = new JPanel(new GridBagLayout());
		west.setBackground(BACKGROUND_COLOR);

		gbchat.anchor = GridBagConstraints.PAGE_START;

		JLabel analytics = new JLabel("Analytics");
		analytics.setBackground(BACKGROUND_COLOR);
		analytics.setPreferredSize(new Dimension(260, 40));
		analytics.setHorizontalAlignment(SwingConstants.CENTER);

		west.add(analytics, gbchat);

		chat_window.add(west, BorderLayout.WEST);
		chat_window.setJMenuBar(menu);
		chat_window.pack();

	}

	/*
	 * prompt the login window before bring the user to the application screen
	 */
	private void init_login() {

		// set up the the basic frame
		login_window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		login_window.setMinimumSize(login_dim);
		login_window.setPreferredSize(login_dim);
		login_window.setLocationRelativeTo(chat_window);
		login_window.setBackground(BACKGROUND_COLOR);

		// creating area for username and password
		JPanel panel = new JPanel();
		panel.setBackground(login_window.getBackground());
		panel.setLayout(new GridBagLayout());
		gblogin.insets = new Insets(5, 15, 5, 15);

		// Titles for the fields
		JLabel username_title = new JLabel("Username");
		JLabel password_title = new JLabel("Password");
		username_title.setHorizontalAlignment(JLabel.CENTER);
		username_title.setFont(username_title.getFont().deriveFont(28f));
		password_title.setHorizontalAlignment(JLabel.CENTER);
		password_title.setFont(password_title.getFont().deriveFont(28f));

		// Username and password Fields
		JTextField username = new JTextField(MAX_STRING_LENGTH);
		username.setFocusTraversalKeysEnabled(false);
		username.setHorizontalAlignment(JTextField.CENTER);
		username.setMaximumSize(
				new Dimension(Integer.MAX_VALUE, TEXT_BOX_HEIGHT_FACTOR * username.getMinimumSize().height));
		username.setFont(username.getFont().deriveFont(FONT));

		JPasswordField password = new JPasswordField(MAX_STRING_LENGTH);
		password.setHorizontalAlignment(JTextField.CENTER);
		password.setMaximumSize(
				new Dimension(Integer.MAX_VALUE, TEXT_BOX_HEIGHT_FACTOR * password.getMinimumSize().height));
		password.setFont(password.getFont().deriveFont(FONT));

		// Log in button
		JButton sign_in = new JButton("Log In");
		sign_in.setFont(sign_in.getFont().deriveFont(FONT));

		JButton create_account = new JButton("New Account");
		create_account.setFont(create_account.getFont().deriveFont(FONT));

		// press enter or tab will move to the password field
		username.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent pressed) {
				if (pressed.getKeyCode() == KeyEvent.VK_TAB || pressed.getKeyCode() == KeyEvent.VK_ENTER) {
					password.requestFocus();
				}

			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub

			}

		});

		password.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					sign_in.doClick();
				}

			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub

			}

		});

		// attempt to log in
		sign_in.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent action) {
				if (info_nexus.user_pass_valid(username.getText(), new String(password.getPassword()))) {
					// go to the chatwindow and load previous text
					chat_window.setTitle("Hello " + username.getText());
					username.setText("");
					password.setText("");

					// loading up saved text from previous session if any was
					// saved
					String text;
					try {
						if ((text = notes.read(chat_window.getTitle())) != null) {
							display.setText(text);
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					login_window.dispose();

					// ensure the window is fully loaded by other thread before
					// allowing visibility
					try {
						chatWindowLoad.join();
						chat_window.setVisible(true);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (info_nexus.has_account(username.getText())) {
					// test if username is at least in the database
					JOptionPane.showMessageDialog(chat_window, "Incorrect Password");
					password.setText("");
				} else {
					JOptionPane.showMessageDialog(chat_window, " No Username " + username.getText()
							+ " exists in system\n" + "To make an account. Click \" New Account\"");
				}

			}

		});

		create_account.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String username = JOptionPane.showInputDialog("Please type your desired username.");
				String pass = JOptionPane.showInputDialog("Please type desired password.");

				if (info_nexus.add_user(username, pass)) {
					JOptionPane.showMessageDialog(login_window, "Account Creation Successful!");
				} else {
					JOptionPane.showMessageDialog(login_window, "Username " + username + " already in use. Try Again.");
				}
			}

		});

		// setting constraints for positioning for each component
		gblogin.anchor = GridBagConstraints.PAGE_START;
		panel.add(username_title, gblogin);

		gblogin.gridy = 1;
		panel.add(username, gblogin);

		gblogin.gridy = 2;
		panel.add(password_title, gblogin);

		gblogin.gridy = 3;
		panel.add(password, gblogin);

		// sub panel for log in and create account buttons
		JPanel button_pane = new JPanel(new GridBagLayout());
		GridBagConstraints button_constraints = new GridBagConstraints();

		button_pane.setBackground(login_window.getBackground());
		button_pane.add(sign_in, button_constraints);

		button_constraints.gridx = 1;
		button_pane.add(create_account, gblogin);

		// add pane to main pane
		gblogin.gridy = 4;
		panel.add(button_pane, gblogin);

		login_window.add(panel, BorderLayout.CENTER);
		login_window.pack();
		login_window.setVisible(true);
	}

}
