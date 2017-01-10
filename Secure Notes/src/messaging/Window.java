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
import java.util.HashMap;
import java.util.Map;

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

/**
 * create the window for which the log in feature and the secured
 * notepad will be displayed.  The main chat window which is generated 
 * by a separate thread will consist
 * of a frame while the login window will be a dialog box
 * 
 * @author Brandon Daniel bjd
 */
public class Window {

	/**
	 * Main window for the note-pad
	 */
	private JFrame chatWindow;
	
	/**
	 * login screen that is displayed first.
	 */
	private JDialog loginWindow;
	
	/**
	 * preferred dimensions of the chat and login screens 
	 */
	private Dimension chatDim, loginDim;
	
	/**
	 * preferred height of each text box for the login screen
	 */
	private final int TEXT_BOX_HEIGHT_FACTOR = 2;
	
	/**
	 * sets the constraints/positioning for the login and chat windows
	 */
	private GridBagConstraints gbLogin, gbChat;
	
	/**
	 * Instance of database which will store all user information in a secure
	 * manner. 
	 */
	private Users infoNexus;
	
	/**
	 * current state of the user
	 */
	private boolean loggedIn = false;
	
	/**
	 * Dedicated font size for all non-header text in the program
	 */
	private final float FONT = 32f;
	
	/**
	 * supporting thread which builds the main notepad window
	 */
	private Thread chatWindowLoad;
	
	/**
	 * longest username or password value that is allowed
	 */
	public final static int MAX_STRING_LENGTH = 25;
	
	/**
	 * default major background color for the entire program
	 */
	private final Color BACKGROUND_COLOR = Color.decode("#5CB3FF");
	
	/**
	 * instance which saves all information regarding notes taking by
	 * each user to the notes file
	 */
	private Save notes;
	
	/**
	 * two major text areas on the noteWindow screen.  display is where any text
	 * can be inputed while the infoArea is non-editable and will only be useds
	 * to display analytics when prompted.
	 */
	private JTextArea display, infoArea;

	/**
	 * Instance of this class generates the gui for the secure notes window
	 * which also includes the log in screen.
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

		chatWindow = new JFrame();
		chatWindow.setVisible(false);

		loginWindow = new JDialog(null, "Login", Dialog.ModalityType.APPLICATION_MODAL);

		chatDim = new Dimension(1920, 1080);
		loginDim = new Dimension(940, 540);
		gbLogin = new GridBagConstraints();
		gbChat = new GridBagConstraints();
		infoNexus = new Users();
		notes = new Save();
		infoArea = new JTextArea(80, 20);

		// load up main window in parallel
		Runnable loadChat = () -> {
			initChatApp();
		};
		chatWindowLoad = new Thread(loadChat);
		chatWindowLoad.start();

		initLogin();

		// Ensure thread joins in case no successful logins
		try {
			chatWindowLoad.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Initialize the chat window including the menu bars, displays for input and output
	 * and any action listeners associated with each interactable item.
	 */
	private void initChatApp() {

		// chat application window initialization
		chatWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		// saving state of infoNexus on closure
		chatWindow.addWindowListener(new WindowListener() {

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
				infoNexus.close();
				try {
					notes.write(chatWindow.getName(), display.getText());
				} catch (IOException e) {
					JOptionPane.showMessageDialog(chatWindow, "Unsuccessful Save");
				}
				chatWindow.dispose();

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

		chatWindow.setPreferredSize(chatDim);
		chatWindow.setMinimumSize(chatDim);
		chatWindow.setLocationRelativeTo(null);
		chatWindow.getContentPane().setBackground(BACKGROUND_COLOR);
		chatWindow.setLayout(new BorderLayout());

		// Left information panel
		JPanel west = new JPanel(new GridBagLayout());
		west.setBackground(BACKGROUND_COLOR);
		west.setPreferredSize(new Dimension(560, 900));
		infoArea.setBackground(BACKGROUND_COLOR);
		infoArea.setEditable(false);
		infoArea.setPreferredSize(new Dimension(260, 60));
		infoArea.setFont(infoArea.getFont().deriveFont(FONT));

		JScrollPane infoScroller = new JScrollPane(infoArea);
		infoScroller.setPreferredSize(new Dimension(520, 800));

		gbChat.anchor = GridBagConstraints.PAGE_START;
		gbChat.fill = GridBagConstraints.HORIZONTAL;
		gbChat.gridwidth = GridBagConstraints.REMAINDER;

		JLabel analytics = new JLabel("Analytics");
		analytics.setFont(analytics.getFont().deriveFont(42f));
		analytics.setBackground(BACKGROUND_COLOR);
		analytics.setPreferredSize(new Dimension(260, 80));
		analytics.setHorizontalAlignment(SwingConstants.CENTER);

		west.add(analytics, gbChat);
		gbChat.gridy = 1;
		west.add(infoScroller, gbChat);

		// CENTER Text area
		JPanel center = new JPanel(new GridBagLayout());

		JScrollPane centerScroll = new JScrollPane();

		display = new JTextArea(300, 40);
		display.setFont(display.getFont().deriveFont(FONT));
		display.setLineWrap(true);
		center.setBackground(Color.decode("#99ebff"));
		centerScroll.setViewportView(display);
		centerScroll.setPreferredSize(new Dimension(1200, 800));

		center.add(centerScroll);

		chatWindow.add(center, BorderLayout.CENTER);

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
					notes.write(chatWindow.getName(), display.getText());
				} catch (IOException e) {
					JOptionPane.showMessageDialog(chatWindow, "Save Location Corrupted");
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
				int response = JOptionPane.showConfirmDialog(chatWindow, "Are you sure you want to quit?");

				if (response == JOptionPane.YES_OPTION) {
					// save the text somewhere
					saveChat.doClick();
					chatWindow.dispose();
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
					JOptionPane.showMessageDialog(chatWindow, "Invalid Search");
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
		replaceAll.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String input = display.getText();
				String toReplace = JOptionPane.showInputDialog("Type the word to replace");
				String replaceWith = JOptionPane.showInputDialog("Type the word to replace \"" + toReplace + "\" with");

				display.setText(input.replaceAll(toReplace, replaceWith));

			}

		});
		advanced.add(replaceAll);

		menu.add(advanced);

		JCheckBoxMenuItem wordLetterFreq = new JCheckBoxMenuItem("Word/Letter Analytics");
		wordLetterFreq.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				infoArea.setText("");

				if (wordLetterFreq.isSelected()) {
					Map<Character, Integer> hist = new HashMap<>();
					String text = display.getText();
					StringBuffer stats = new StringBuffer();

					// get info about occurrences of chars
					for (int i = 0; i < text.length(); i++) {
						char curr = text.charAt(i);
						if (!(curr == '\n' || curr == ' ')) {
							if (!hist.containsKey(curr)) {
								hist.put(curr, 1);
							} else {
								hist.put(curr, hist.get(curr) + 1);
							}
						}

					}

					// convert all info to string
					stats.append("Character -- Appearances\n");
					for (Character c : hist.keySet()) {
						stats.append(c + " -- " + hist.get(c) + "\n");
					}

					infoArea.setText(stats.toString());
				} else {
					// clears the panel
					infoArea.setText("");
				}
			}

		});
		analysis.add(wordLetterFreq);

		JCheckBoxMenuItem topWords = new JCheckBoxMenuItem("Word Pairs");
		topWords.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (topWords.isSelected()) {
					infoArea.setText("");
					String[] text = display.getText().replaceAll("\\n", "").split(" ");
					String curr;
					Map<String, Integer> hist = new HashMap<String, Integer>();
					StringBuffer output = new StringBuffer();

					// remove any punctuation and add to space
					for (int indx = 0; indx < text.length; indx++) {
						curr = text[indx];
						for (int i = 0; i < text[indx].length(); i++) {
							if (!Character.isAlphabetic(curr.charAt(i)) && !Character.isDigit(curr.charAt(i))) {
								// remove this character because it is
								// punctuation or invalid symbol
								text[indx] = curr.replaceAll(Character.toString(curr.charAt(i)), "");
							}
						}
					}

					// determine occurrences of pairs
					for (int prev = 0, current = 1; current < text.length; prev++, current++) {
						String entry = text[prev] + " " + text[current];
						if (hist.containsKey(entry)) {
							hist.put(entry, hist.get(entry) + 1);
						} else {
							hist.put(text[prev] + " " + text[current], 1);
						}

					}

					// format output for display and display
					output.append("Pair -- Appearances\n");

					for (String pair : hist.keySet()) {
						output.append(pair + " -- " + hist.get(pair) + "\n");
					}

					infoArea.setText(output.toString());

				} else {
					infoArea.setText("");
				}

			}
		});
		analysis.add(topWords);

		menu.add(analysis);

		chatWindow.add(west, BorderLayout.WEST);
		chatWindow.setJMenuBar(menu);
		chatWindow.pack();

	}

	/*
	 * prompt the login window before bring the user to the application screen
	 */
	private void initLogin() {

		// set up the the basic frame
		loginWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		loginWindow.setMinimumSize(loginDim);
		loginWindow.setPreferredSize(loginDim);
		loginWindow.setLocationRelativeTo(chatWindow);
		loginWindow.setBackground(BACKGROUND_COLOR);
		
		loginWindow.addWindowListener(new WindowListener(){

			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowClosed(WindowEvent e) {
				if(!loggedIn){
					System.exit(0);
				}
				
			}

			@Override
			public void windowClosing(WindowEvent e) {
				loginWindow.dispose();
				
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		

		// creating area for username and password
		JPanel panel = new JPanel();
		panel.setBackground(loginWindow.getBackground());
		panel.setLayout(new GridBagLayout());
		gbLogin.insets = new Insets(5, 15, 5, 15);

		// Titles for the fields
		JLabel username_title = new JLabel("Username");
		JLabel password_title = new JLabel("Password");
		username_title.setHorizontalAlignment(JLabel.CENTER);
		username_title.setFont(username_title.getFont().deriveFont(FONT));
		password_title.setHorizontalAlignment(JLabel.CENTER);
		password_title.setFont(password_title.getFont().deriveFont(FONT));

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
		JButton signIn = new JButton("Log In");
		signIn.setFont(signIn.getFont().deriveFont(FONT));

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
					signIn.doClick();
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
		signIn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent action) {
				if (infoNexus.userPassValid(username.getText(), new String(password.getPassword()))) {
					// go to the chatwindow and load previous text
					loggedIn = true;
					chatWindow.setTitle("Hello " + username.getText());
					chatWindow.setName(username.getText());
					username.setText("");
					password.setText("");

					// loading up saved text from previous session if any was
					// saved
					String text;
					try {
						if ((text = notes.read(chatWindow.getName())) != null) {
							display.setText(text);
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					loginWindow.dispose();

					// ensure the window is fully loaded by other thread before
					// allowing visibility
					try {
						chatWindowLoad.join();
						chatWindow.setVisible(true);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (infoNexus.hasAccount(username.getText())) {
					// test if username is at least in the database
					JOptionPane.showMessageDialog(chatWindow, "Incorrect Password");
					password.setText("");
				} else {
					JOptionPane.showMessageDialog(chatWindow, " No Username " + username.getText()
							+ " exists in system\n" + "To make an account. Click \" New Account\"");
				}

			}

		});

		create_account.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String username = JOptionPane.showInputDialog("Please type your desired username.");
				String pass = JOptionPane.showInputDialog("Please type desired password.");

				if (infoNexus.addUser(username, pass)) {
					JOptionPane.showMessageDialog(loginWindow, "Account Creation Successful!");
				} else {
					JOptionPane.showMessageDialog(loginWindow, "Username " + username + " already in use. Try Again.");
				}
			}

		});

		// setting constraints for positioning for each component
		gbLogin.anchor = GridBagConstraints.PAGE_START;
		panel.add(username_title, gbLogin);

		gbLogin.gridy = 1;
		panel.add(username, gbLogin);

		gbLogin.gridy = 2;
		panel.add(password_title, gbLogin);

		gbLogin.gridy = 3;
		panel.add(password, gbLogin);

		// sub panel for log in and create account buttons
		JPanel button_pane = new JPanel(new GridBagLayout());
		GridBagConstraints button_constraints = new GridBagConstraints();

		button_pane.setBackground(loginWindow.getBackground());
		button_pane.add(signIn, button_constraints);

		button_constraints.gridx = 1;
		button_pane.add(create_account, gbLogin);

		// add pane to main pane
		gbLogin.gridy = 4;
		panel.add(button_pane, gbLogin);

		loginWindow.add(panel, BorderLayout.CENTER);
		loginWindow.pack();
		loginWindow.setVisible(true);
	}

}
