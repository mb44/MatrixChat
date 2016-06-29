package matrixchat.client;
/**
 * @author Morten Beuchert
 */
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Locale;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;

public class ClientGUI extends JFrame {
	private static final long serialVersionUID = 1L;

	static volatile boolean keepRunning = true;

	private static ChatClient chatClient;
	private JPanel panelMain;
	private JTextField textFieldServerAddr;
	private JLabel labelUsername;
	private JTextField textFieldUsername;
	private JTextField textFieldInput;
	private JTextArea textAreaChat;
	private JScrollPane scrollPaneChat;
	private JLabel labelServerAddr;
	private JButton buttonConnect;
	private JButton buttonSendMsg;
	private JList<String> listOnline;
	private DefaultListModel<String> listModel;
	private JScrollPane scrollPaneOnline;

	public ClientGUI() {
		super("Chat (disconnected)");
		chatClient = new ChatClient(this);
		createComponents();
		initializeComponents();
		registerEventHandlers();
		addComponentsToFrame();
	}

	private void createComponents() {
		panelMain = new JPanel();
		labelServerAddr = new JLabel("Server address:");
		Color labelColor = new Color(255, 102, 102);
		Font labelFont = new Font("Dialog", Font.BOLD, 18);
		Font textFieldFont = new Font("Dialog", Font.PLAIN, 14);
		labelServerAddr.setForeground(labelColor);
		labelServerAddr.setFont(labelFont);
		textFieldServerAddr = new JTextField(12);
		textFieldServerAddr.setBackground(Color.black);
		textFieldServerAddr.setForeground(Color.green);
		textFieldServerAddr.setFont(textFieldFont);
		labelUsername = new JLabel("Username:");
		labelUsername.setFont(labelFont);
		labelUsername.setForeground(labelColor);
		textFieldUsername = new JTextField(12);
		textFieldUsername.setBackground(Color.black);
		textFieldUsername.setForeground(Color.green);
		textFieldUsername.setFont(textFieldFont);
		textFieldInput = new JTextField(20);
		textFieldInput.setLocale(new Locale("da", "DK"));
		textFieldInput.setBackground(Color.black);
		textFieldInput.setForeground(Color.green);
		textFieldInput.setFont(textFieldFont);
		textAreaChat = new JTextArea(18, 35);
		textAreaChat.setLocale(new Locale("da", "DK"));
		textAreaChat.setFont(new Font("Dialog", Font.PLAIN, 16));
		textAreaChat.setLineWrap(true);
		textAreaChat.setWrapStyleWord(true);
		textAreaChat.setEditable(false);
		textAreaChat.setBackground(Color.black);
		textAreaChat.setForeground(Color.green);
		DefaultCaret caret = (DefaultCaret) textAreaChat.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		scrollPaneChat = new JScrollPane(textAreaChat,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		buttonConnect = new JButton("Connect");
		buttonConnect.setPreferredSize(new Dimension(120, 20));
		buttonConnect.setBackground(Color.green);
		buttonSendMsg = new JButton("Send");
		buttonSendMsg.setBackground(Color.green);

		listModel = new DefaultListModel<String>();
		listOnline = new JList<>(listModel);
		listOnline.setBackground(Color.black);
		listOnline.setForeground(Color.white);
		scrollPaneOnline = new JScrollPane(listOnline);
		scrollPaneOnline.setPreferredSize(new Dimension(120, 380));
		listOnline.setVisibleRowCount(18);
	}

	private void initializeComponents() {
		setSize(700, 600);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	private void registerEventHandlers() {
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (buttonConnect.getText().equals("Disconnect"))
					chatClient.disconnect();
			}
		});

		textFieldInput.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == e.VK_ENTER) {
					sendMessage();
				}
			}
		});

		EventHandler handler = new EventHandler();
		buttonConnect.addActionListener(handler);
		buttonSendMsg.addActionListener(handler);

	}

	private void addComponentsToFrame() {
		setLayout(new BorderLayout());
		URL resource = ClientGUI.class.getResource("/matrixchat/client/resources/matrix.jpg");
		
		JLabel background = new JLabel(new ImageIcon(resource));
		add(background);
		background.setLayout(new FlowLayout());

		panelMain.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		// 1st row
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		c.insets = new Insets(0, 10, 0, 0);
		c.gridx = 0;
		c.gridy = 0;
		panelMain.add(labelServerAddr, c);

		// 2nd row
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.LINE_END;
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		panelMain.add(textFieldServerAddr, c);

		// 3nd row
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		c.insets = new Insets(2, 10, 0, 0);
		c.gridx = 0;
		c.gridy = 2;

		panelMain.add(labelUsername, c);

		// 4th row
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 1;
		panelMain.add(textFieldUsername, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 3;
		c.gridwidth = 1;
		panelMain.add(buttonConnect, c);

		// 4th row
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(10, 10, 0, 0);
		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = 1;
		panelMain.add(scrollPaneChat, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 4;
		c.gridwidth = 1;
		panelMain.add(scrollPaneOnline, c);

		// 5th row
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 5;
		c.gridwidth = 1;
		panelMain.add(textFieldInput, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		c.gridx = 1;
		c.gridy = 5;
		panelMain.add(buttonSendMsg, c);

		panelMain.setOpaque(false);
		background.add(panelMain);
	}

	public void appendToChat(String msg) {
		Thread t = new Thread(new PlayNewMessageSound());
		t.start();
		textAreaChat.append(msg + "\n");
	}

	private void sendMessage() {
		if (!textFieldInput.getText().equals("")) {
			chatClient.sendToServer(0, textFieldInput.getText());
			textFieldInput.setText("");
		}
	}

	public void updateUsersOnline(String users) {
		users = users.substring(1, users.length() - 1);
		String[] u = users.split(",");

		if (u.length > listModel.getSize()) {
			Thread t = new Thread(new PlayEnterSound());
			t.start();
		} else {
			Thread t = new Thread(new PlayExitSound());
			t.start();
		}

		listModel.removeAllElements();
		for (int i = 0; i < u.length; i++) {
			listModel.addElement(u[i].trim());
		}
	}

	public void restart(String message) {
		JOptionPane.showMessageDialog(null, message);
		listModel.removeAllElements();
		textFieldServerAddr.setEditable(true);
		textFieldUsername.setEditable(true);
		setTitle("Chat (disconnected)");
		buttonConnect.setText("Connect");
	}

	public static void main(String[] args) {
		ClientGUI frame = new ClientGUI();
		frame.setVisible(true);

		final Thread mainThread = Thread.currentThread();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				keepRunning = false;
				try {
					mainThread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					chatClient.disconnect();
				}
			}
		});
	}

	public class EventHandler implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			if (event.getSource() == buttonConnect) {
				if (buttonConnect.getText().equals("Connect")) {
					if (textFieldServerAddr.getText().equals("")
							|| textFieldUsername.getText().equals("")) {
						JOptionPane.showMessageDialog(null,
								"Please input server address and username");
					} else {
						chatClient.connect(textFieldServerAddr.getText());
						chatClient.sendToServer(1, textFieldUsername.getText());
						textFieldServerAddr.setEditable(false);
						textFieldUsername.setEditable(false);
						setTitle("Connected to "
								+ textFieldServerAddr.getText() + " as "
								+ textFieldUsername.getText());
						buttonConnect.setText("Disconnect");
					}
				} else if (buttonConnect.getText().equals("Disconnect")) {
					Thread t = new Thread(new PlayExitSound());
					t.start();
					chatClient.disconnect();
					listModel.removeAllElements();
					textFieldServerAddr.setEditable(true);
					textFieldUsername.setEditable(true);
					setTitle("Chat (disconnected)");
					buttonConnect.setText("Connect");
				}
			} else if (event.getSource() == buttonSendMsg) {
				sendMessage();
			}
		}
	}
	
	public class PlayEnterSound implements Runnable {
		// Sound clip 1
		private AudioInputStream audioInputStream;
		private Clip audioClipEnter;
		private boolean playEnterCompleted;

		public PlayEnterSound() {
			// Sound 1 (Enter)
			playEnterCompleted = false;
			
			URL soundURL = getClass().getResource("/matrixchat/client/resources/chat_enter.wav");
			//InputStream is = 
			try {
				audioInputStream = AudioSystem.getAudioInputStream(soundURL);
				AudioFormat formatEnter = audioInputStream.getFormat();
				DataLine.Info infoEnter = new DataLine.Info(Clip.class,
						formatEnter);

				audioClipEnter = (Clip) AudioSystem.getLine(infoEnter);
				audioClipEnter.addLineListener(new LineListenerAdapter() {
					public void update(LineEvent event) {
						LineEvent.Type type = event.getType();

						if (type == LineEvent.Type.START) {
						} else if (type == LineEvent.Type.STOP) {
							playEnterCompleted = true;
						}
					}
				});
			} catch (UnsupportedAudioFileException e) {
				System.out.println("Audio file unsupported.");
			} catch (LineUnavailableException e) {
				System.out.println("Line unavailable.");
			} catch (IOException e) {
				System.out.println("Could not open file.");
			}
		}

		public void run() {
			playEnterCompleted = false;
			try {
				audioClipEnter.open(audioInputStream);
			} catch (LineUnavailableException e) {
				System.out.println("Line is unavailable");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("Could not open audioStream");
				e.printStackTrace();
			}
			audioClipEnter.start();
			while (!playEnterCompleted) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}
			audioClipEnter.close();
		}
	}

	public class PlayExitSound implements Runnable {
		// Sound clip 2
		private AudioInputStream audioStream2;
		private Clip audioClipExit;
		private boolean playExitCompleted;

		public PlayExitSound() {
			// Sound 2 (Exit)
			playExitCompleted = false;
			
			
			URL soundURL = getClass().getResource("/matrixchat/client/resources/chat_exit2.wav");
			try {
				audioStream2 = AudioSystem.getAudioInputStream(soundURL);
				AudioFormat formatExit = audioStream2.getFormat();
				DataLine.Info infoExit = new DataLine.Info(Clip.class,
						formatExit);

				audioClipExit = (Clip) AudioSystem.getLine(infoExit);

				audioClipExit.addLineListener(new LineListenerAdapter() {
					public void update(LineEvent event) {
						LineEvent.Type type = event.getType();

						if (type == LineEvent.Type.START) {
						} else if (type == LineEvent.Type.STOP) {
							playExitCompleted = true;
						}
					}
				});
			} catch (UnsupportedAudioFileException e) {
				System.out.println("Audio file unsupported.");
			} catch (LineUnavailableException e) {
				System.out.println("Line unavailable.");
			} catch (IOException e) {
				System.out.println("Could not open file.");
			}
		}

		public void run() {
			playExitCompleted = false;
			try {
				audioClipExit.open(audioStream2);
			} catch (LineUnavailableException e) {
				System.out.println("Line not available");
			} catch (IOException e) {
				System.out.println("Could not open audiostream");
			}
			audioClipExit.start();
			while (!playExitCompleted) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}
			audioClipExit.close();
		}
	}
	
	public class PlayNewMessageSound implements Runnable {
		// Sound clip 2
		private AudioInputStream audioStream2;
		private Clip audioClipExit;
		private boolean playSoundCompleted;

		public PlayNewMessageSound() {
			// Sound 2 (Exit)
			playSoundCompleted = false;
			
			
			URL soundURL = getClass().getResource("/matrixchat/client/resources/message1.wav");
			//InputStream is= getClass().getResourceAsStream("/matrixchat/client/resources/message1.wav");
				try {
				audioStream2 = AudioSystem.getAudioInputStream(soundURL);
				AudioFormat formatExit = audioStream2.getFormat();
				DataLine.Info infoExit = new DataLine.Info(Clip.class,
						formatExit);

				audioClipExit = (Clip) AudioSystem.getLine(infoExit);

				audioClipExit.addLineListener(new LineListenerAdapter() {
					public void update(LineEvent event) {
						LineEvent.Type type = event.getType();

						if (type == LineEvent.Type.START) {
						} else if (type == LineEvent.Type.STOP) {
							playSoundCompleted = true;
						}
					}
				});
			} catch (UnsupportedAudioFileException e) {
				System.out.println("Audio file unsupported.");
			} catch (LineUnavailableException e) {
				System.out.println("Line unavailable.");
			} catch (IOException e) {
				System.out.println("Could not open file.");
			}
		}

		public void run() {
			playSoundCompleted = false;
			try {
				audioClipExit.open(audioStream2);
				FloatControl volControl = (FloatControl)audioClipExit.getControl(FloatControl.Type.MASTER_GAIN);
				volControl.setValue(-3.0f);
			} catch (LineUnavailableException e) {
				System.out.println("Line not available");
			} catch (IOException e) {
				System.out.println("Could not open audiostream");
			}
			audioClipExit.start();
			while (!playSoundCompleted) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}
			audioClipExit.close();
		}
	}
}