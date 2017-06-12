package org.pokenet.server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.pokenet.server.network.MySqlManager;
import org.pokenet.server.network.TcpProtocolHandler;

/**
 * Represents a game server.
 * 
 * Starting a server requires a parameter to be passed in, i.e. java GameServer -s low -p 500
 * Here are the different settings:
 * -low
 * 		< 1.86ghz
 * 		< 256MB Ram
 * 		< 1mbps Up/Down Connection
 * 		75 Players
 * -medium
 * 		< 2ghz
 * 		512MB - 1GB Ram
 * 		1mbps Up/Down Connection
 * 		200 Players
 * -high
 * 		> 1.86ghz
 * 		> 1GB Ram
 * 		> 1mbps Up/Down Connection
 * 		> 500 Players
 * 
 * @author shadowkanji
 * @author Nushio
 *
 */
public class GameServer {
	private static GameServer m_instance;
	private static ServiceManager m_serviceManager;
	private static int m_maxPlayers, m_movementThreads;
	private static String m_dbServer, m_dbName, m_dbUsername, m_dbPassword, m_serverName, m_dbPort;
	private static boolean m_boolGui;
	private JTextField m_dbS, m_dbN, m_dbU, m_name, m_port;
	private JPasswordField m_dbP;
	private JButton m_start, m_stop, m_set, m_exit;
	private int m_highest;
	private JLabel m_pAmount, m_pHighest;
	private JFrame m_gui;
	/* The revision of the game server */ 
	// would be more helpful if this was the SVN version.. so it auto updated

//	public static int REVISION = getSVNRev();//1786;
	public static String REVISION = getRev();
	
	/**
	 * Load pre-existing settings if any
 	 * NOTE: It loads the database password if available.
 	 * Password is line after serverName
	 */
	private void loadSettings(){
		
		File foo = new File("res/settings.txt");
		if(foo.exists()) {
			try {
				Scanner s = new Scanner(foo);
				m_dbServer = s.nextLine();
				m_dbName = s.nextLine();
				m_dbUsername = s.nextLine();
				m_serverName = s.nextLine();
				m_dbPassword = s.nextLine();
				s.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * .Gets the SVN revision for the server
	 *
	 * @return the value on the third line of .svn/entries
	 */
	private static int getSVNRev() {
		int rev = 0;
		boolean foundRevision = false;
		
	    try {
	    	BufferedReader input =  new BufferedReader(new FileReader(".svn/entries"));
	    	try {
	    		String line = null; 
	     
	    		while (( line = input.readLine()) != null && !foundRevision){
	    			if(line.equals("dir")){
	    				rev = Integer.parseInt(input.readLine()); // this hopefully is the revision number
	    				foundRevision = true;
	    			}
	    		}
	    	} finally {
	    		input.close();
	      	}	
	    }
	    catch (IOException ex){
//	    	ex.printStackTrace();
	    	// probably no svn file... oh well.
	    	rev = 0;
	    }
	    
	    return rev;
	}
	
	private static String getGITRev()
	{
		String rev = "(null)";
		
		try {
			BufferedReader input = new BufferedReader(new FileReader(".git/refs/heads/master"));
			rev = input.readLine(); // this is git revision isn't it?
			input.close();
		} catch (IOException ex)
		{
			rev = "(null)";
		}
		return rev;
	}
	
	private static String getRev() {
		String rev = String.valueOf(getSVNRev());
		if (!rev.equals("0")) //Wow, we're using Subversion
			return rev;
		rev = getGITRev();
		if (!rev.equals("(null)")) //We're using Git
			return rev;
		
		return "1868"; //Return base revision
	}
	
	

	/**
	 * Asks for Database User/Pass, then asks to save
	 * NOTE: It doesnt save the database password
	 */
	private void getConsoleSettings(){
		ConsoleReader r = new ConsoleReader();
		System.out.println("Please enter the required information.");
		System.out.println("Database Server: ");
		m_dbServer = r.readToken();
		System.out.println("Database Name:");
		m_dbName = r.readToken();
		System.out.println("Database Username:");
		m_dbUsername = r.readToken();
		System.out.println("Database Password:");
		m_dbPassword = r.readToken();
		System.out.println("This server's IP or hostname:");
		m_serverName = r.readToken();
		System.out.println("Save info? (y/N)");
		String answer = r.readToken();
		if(answer.contains("y")||answer.contains("Y")){
			saveSettings();
		}
		System.out.println();
		System.err.println("WARNING: When using -nogui, the server should only be shut down using a master client");
	}
	
	/**
	 * Default constructor
	 */
	public GameServer(boolean autorun) {
		System.out.println("Revsion: " + REVISION);
		m_instance = this;
		if(autorun){
			loadSettings();
			start();
		}else{
			if(m_boolGui) {
				loadSettings();
				createGui();
			} else {
				ConsoleReader r = new ConsoleReader();
				System.out.println("Load Settings? y/N");
				String answer = r.readToken();
				if(answer.contains("y")||answer.contains("Y")){
					loadSettings();
				}else{
					getConsoleSettings();
				}
				start();
			}
		}
	}
	
	private boolean testMySQL() {
		if (!m_boolGui)
			return false;
		
		m_dbServer = m_dbS.getText();
		m_dbName = m_dbN.getText();
		m_dbUsername = m_dbU.getText();
		m_dbPassword = new String(m_dbP.getPassword());
		m_serverName = m_name.getText();	
		m_dbPort = m_port.getText();
		
		MySqlManager mngr = new MySqlManager();
		if (!mngr.connect(m_dbServer, m_dbPort, m_dbUsername, m_dbPassword))
			return false;
		
		if (!mngr.selectDatabase(m_dbName))
			return false;
		
		mngr.close();
		return true;
	}
	
	/**
	 * Generates the gui-version of the server
	 */
	private void createGui() {
		m_gui = new JFrame();
		m_gui.setTitle("Pokenet Server");
		m_gui.setSize(270, 390);
		m_gui.setDefaultCloseOperation(0); //DO_NOTHING_ON_CLOSE
		m_gui.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				exit();
			}
		});
		m_gui.getContentPane().setLayout(null);
		m_gui.setResizable(false);
		m_gui.setLocation(32, 32);
		
		/*
		 * Set up the buttons
		 */
		
		
		m_pAmount = new JLabel("0 players online");
		m_pAmount.setSize(160, 16);
		m_pAmount.setLocation(130 - 60, 4);
		m_pAmount.setVisible(true);
		m_gui.getContentPane().add(m_pAmount);
		
		JLabel m_pRecordName = new JLabel("Max Players regstred:");
		m_pRecordName.setSize(160, 16);
		m_pRecordName.setLocation(4, 20 + 4);
		m_pRecordName.setVisible(true);
		m_gui.getContentPane().add(m_pRecordName);
		
		m_pHighest = new JLabel("[No record]");
		m_pHighest.setSize(160, 16);
		m_pHighest.setLocation(160 + 4, 4 + 20);
		m_pHighest.setVisible(true);
		m_gui.getContentPane().add(m_pHighest);
		
		m_start = new JButton("Start Server");
		m_start.setSize(128, 24);
		m_start.setLocation(4, 40 + 28);
		m_start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				start();
			}
		});
		m_gui.getContentPane().add(m_start);
		
		m_stop = new JButton("Stop Server");
		m_stop.setSize(128, 24);
		m_stop.setLocation(128 + 5, 40 + 28);
		m_stop.setEnabled(false);
		m_stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				stop();
			}
		});
		m_gui.getContentPane().add(m_stop);
		
		m_set = new JButton("Save Settings");
		m_set.setSize(128, 24);
		m_set.setLocation(4, 60 + 34);
		m_set.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				saveSettings();
			}
		});
		m_gui.getContentPane().add(m_set);
		
		m_exit = new JButton("Quit");
		m_exit.setSize(128, 24);
		m_exit.setLocation(130 - 60, 296 + 32);
		m_exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				exit();
			}
		});
		m_gui.getContentPane().add(m_exit);
		
		JButton m_test = new JButton("Test MySQL");
		m_test.setSize(128,24);
		m_test.setLocation(128+5, 60 + 34);
		m_test.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (!testMySQL()) {
					JOptionPane.showMessageDialog(null, "ERROR: MySQL test has failed! Please see the console", "Error", JOptionPane.ERROR_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(null, "SUCCESS: MySQL test successfully worked!", "Information", JOptionPane.INFORMATION_MESSAGE);					
				}
			}
		});
		m_gui.getContentPane().add(m_test);
		
		/*
		 * Settings text boxes
		 */
		JLabel m_dbSL = new JLabel();
		m_dbSL.setSize(160,16);
		m_dbSL.setText("MySQL Host:");
		m_dbSL.setLocation(4, 128);
		m_gui.getContentPane().add(m_dbSL);
		
		m_dbS = new JTextField();
		m_dbS.setSize(128, 24);
		m_dbS.setText("127.0.0.1");
		m_dbS.setLocation(128 + 5, 128);
		m_gui.getContentPane().add(m_dbS);
		
		JLabel m_dbNL = new JLabel();
		m_dbNL.setSize(160,16);
		m_dbNL.setText("Database Name:");
		m_dbNL.setLocation(4, 160 );
		m_gui.getContentPane().add(m_dbNL);		
		
		m_dbN = new JTextField();
		m_dbN.setSize(128, 24);
		m_dbN.setText("pokenet");
		m_dbN.setLocation(128 + 5, 160);
		m_gui.getContentPane().add(m_dbN);
	
		JLabel m_dbUL = new JLabel();
		m_dbUL.setSize(160,16);
		m_dbUL.setText("MySQL Username:");
		m_dbUL.setLocation(4, 192 );
		m_gui.getContentPane().add(m_dbUL);			
		
		m_dbU = new JTextField();
		m_dbU.setSize(128, 24);
		m_dbU.setText("root");
		m_dbU.setLocation(128 + 5, 192);
		m_gui.getContentPane().add(m_dbU);
		
		JLabel m_dbPL = new JLabel();
		m_dbPL.setSize(160,16);
		m_dbPL.setText("MySQL Password:");
		m_dbPL.setLocation(4, 224 );
		m_gui.getContentPane().add(m_dbPL);				
		
		m_dbP = new JPasswordField();
		m_dbP.setSize(128, 24);
		m_dbP.setText("");
		m_dbP.setLocation(128 + 5, 224);
		m_gui.getContentPane().add(m_dbP);
		
		JLabel m_dbSN = new JLabel();
		m_dbSN.setSize(160,16);
		m_dbSN.setText("Server name:");
		m_dbSN.setLocation(4, 260);
		m_gui.getContentPane().add(m_dbSN);	
		
		m_name = new JTextField();
		m_name.setSize(128, 24);
		m_name.setText("PokeNet");
		m_name.setLocation(128+5, 260);
		m_gui.getContentPane().add(m_name);
		
		JLabel m_dbPN = new JLabel();
		m_dbPN.setSize(160,16);
		m_dbPN.setText("MySQL Port:");
		m_dbPN.setLocation(4, 296);
		m_gui.getContentPane().add(m_dbPN);	
		
		m_port = new JTextField();
		m_port.setSize(128, 24);
		m_port.setText("3306");
		m_port.setLocation(128+5, 296);
		m_gui.getContentPane().add(m_port);		
		
		/*
		 * Load pre-existing settings if any
		 */
		File f = new File("res/settings.txt");
		if(f.exists()) {
			try {
				Scanner s = new Scanner(f);
				m_dbS.setText(s.nextLine());
				m_dbN.setText(s.nextLine());
				m_dbU.setText(s.nextLine());
				m_name.setText(s.nextLine());
				m_dbP.setText(s.nextLine());
				m_port.setText(s.nextLine());
				s.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		m_gui.setVisible(true);
	}
	
	/**
	 * Starts the game server
	 */
	public void start() {
		/*
		 * Store locally
		 */
		if(m_boolGui) {
			m_dbServer = m_dbS.getText();
			m_dbName = m_dbN.getText();
			m_dbUsername = m_dbU.getText();
			m_dbPassword = new String(m_dbP.getPassword());
			m_serverName = m_name.getText();
			m_dbPort = m_port.getText();
			
			
			m_serviceManager = new ServiceManager();
			if (m_serviceManager.start()) {
				m_start.setEnabled(false);
				m_stop.setEnabled(true);
			}
		} else {
			m_serviceManager = new ServiceManager();
			m_serviceManager.start();
		}
	}
	
	/**
	 * Stops the game server
	 */
	public void stop() {
		m_serviceManager.stop();
		if(m_boolGui){
			m_start.setEnabled(true);
			m_stop.setEnabled(false);
		} else {
			try {
				/* Let threads finish up */
				Thread.sleep(10000);
				/* Exit */
				System.out.println("Exiting server...");
				System.exit(0);
			} catch (Exception e) {}
		}
	}
	
	/**
	 * Exits the game server application
	 */
	private void exit() {
		if(m_boolGui)
			if(m_stop.isEnabled()) {
				JOptionPane.showMessageDialog(null, "You must stop the server before exiting.");
			} else {
				System.exit(0);
			}
		else
			System.exit(0);
	}
	
	/**
	 * Writes server settings to a file
	 * NOTE: It never stores the database password
	 */
	private void saveSettings() {
		try {
			/*
			 * Store globally
			 */
			if(m_boolGui){
				m_dbServer = m_dbS.getText();
				m_dbName = m_dbN.getText();
				m_dbUsername = m_dbU.getText();
				m_dbPassword = new String(m_dbP.getPassword());
				m_serverName = m_name.getText();
				m_dbPort = m_port.getText();
			}
			/*
			 * Write to file
			 */
			File f = new File("res/settings.txt");
			if(f.exists())
				f.delete();
			PrintWriter w = new PrintWriter(f);
			w.println(m_dbServer);
			w.println(m_dbName);
			w.println(m_dbUsername);
			w.println(m_serverName);
			w.println(m_dbPassword);
			w.println(m_dbPort);
			w.flush();
			w.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Updates the player count information
	 * @param amount
	 */
	public void updatePlayerCount() {
		if(m_boolGui){
			int amount = TcpProtocolHandler.getPlayerCount();
			m_pAmount.setText(amount + " players online");
			if(amount > m_highest) {
				m_highest = amount;
				m_pHighest.setText("Highest: " + amount);
			}
		} else {
			int amount = TcpProtocolHandler.getPlayerCount();
			System.out.println(amount + " players online");
			if(amount > m_highest) {
				m_highest = amount;
				System.out.println("Highest: " + amount);
			}
		}
		
	}
	
	/**
	 * Returns the instance of game server
	 * @return
	 */
	public static GameServer getInstance() {
		return m_instance;
	}
	
	/**
	 * If you don't know what this method does, you clearly don't know enough Java to be working on this.
	 * @param args
	 */
	public static void main(String [] args) {
		/*
		 * Pipe errors to a file
		 */
		try {
			PrintStream p = new PrintStream(new File("./errors.txt"));
			System.setErr(p);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/*
		 * Server settings
		 */
		Options options = new Options();
		options.addOption("s","settings", true, "Can be low, medium, or high.");
		options.addOption("p","players", true, "Sets the max number of players.");
		options.addOption("ng", "nogui", false, "Starts server in headless mode.");
		options.addOption("ar", "autorun", false, "Runs without asking a single question.");
		options.addOption("h", "help", false, "Shows this menu.");
		
		if(args.length > 0) {
			
			CommandLineParser parser = new GnuParser();
		    try {
		        // parse the command line arguments
		        CommandLine line = parser.parse( options, args );
		        
		        /*
				 * The following sets the server's settings based on the
				 * computing ability of the server specified by the server owner.
				 */
		        if( line.hasOption( "settings" ) ) {
		        	String settings = line.getOptionValue( "settings" );
		        	if(settings.equalsIgnoreCase("low")) {
						m_movementThreads = 4;
					} else if(settings.equalsIgnoreCase("medium")) {
						m_movementThreads = 8;
					} else if(settings.equalsIgnoreCase("high")) {
						m_movementThreads = 12;
					} else {
						System.err.println("Server requires a settings parameter");
						HelpFormatter formatter = new HelpFormatter();
				        formatter.printHelp( "java GameServer [param] <args>", options );
						System.exit(0);
					}
		        }else{
		        	System.err.println("Server requires a settings parameter");
		        	HelpFormatter formatter = new HelpFormatter();
			        formatter.printHelp( "java GameServer [param] <args>", options );
					System.exit(0);
		        }
		        
		        if(line.hasOption("players")) {
					m_maxPlayers = Integer.parseInt(line.getOptionValue( "players" ));
					if(m_maxPlayers == 0 || m_maxPlayers == -1)
						m_maxPlayers = 99999;
				} else {
					System.err.println("WARNING: No maximum player count provided. Will default to 500 players.");
					m_maxPlayers = 500;
				}
		        
		        if(line.hasOption("help")){
		        	HelpFormatter formatter = new HelpFormatter();
					System.err.println("Server requires a settings parameter");
			        formatter.printHelp( "java GameServer [param] <args>", options );
		        }
		        
		        /*
				 * Create the server gui
				 */
				@SuppressWarnings("unused")
		        GameServer gs;
		        if(line.hasOption("nogui")){
					m_boolGui = false;
					if(line.hasOption("autorun"))
						gs = new GameServer(true);
					else
						gs = new GameServer(false);
				}else{
					m_boolGui = true;
					if(line.hasOption("autorun"))
						System.out.println("autorun doesn't work with GUI");
					gs = new GameServer(false);
				}
		    }
		    catch( ParseException exp ) {
		        // oops, something went wrong
		        System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
		     // automatically generate the help statement
		        HelpFormatter formatter = new HelpFormatter();
		        formatter.printHelp( "java GameServer [param] <args>", options );
		    }

		}else{
			// automatically generate the help statement
			HelpFormatter formatter = new HelpFormatter();
			System.err.println("Server requires a settings parameter");
	        formatter.printHelp( "java GameServer [param] <args>", options );
		}
	}
	
	/**
	 * Returns the service manager of the server
	 * @return
	 */
	public static ServiceManager getServiceManager() {
		return m_serviceManager;
	}
	
	/**
	 * Returns the amount of players this server will allow
	 * @return
	 */
	public static int getMaxPlayers() {
		return m_maxPlayers;
	}
	
	/**
	 * Returns the amount of movement threads running in this server
	 * @return
	 */
	public static int getMovementThreadAmount() {
		return m_movementThreads;
	}
	
	/**
	 * Returns the database host
	 * @return
	 */
	public static String getDatabaseHost() {
		return m_dbServer;
	}
	
	/**
	 * Returns the database port
	 * @return
	 */
	public static String getDatabasePort() {
		return m_dbPort;
	}
	
	/**
	 * Returns the database username
	 * @return
	 */
	public static String getDatabaseUsername() {
		return m_dbUsername;
	}
	
	/**
	 * Returns the database password
	 * @return
	 */
	public static String getDatabasePassword() {
		return m_dbPassword;
	}
	
	/**
	 * Returns the name of this server
	 * @return
	 */
	public static String getServerName() {
		return m_serverName;
	}
	
	/**
	 * Returns the database selected
	 * @return
	 */
	public static String getDatabaseName() {
		return m_dbName;
	}
}
