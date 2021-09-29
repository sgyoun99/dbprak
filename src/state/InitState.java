package state;

import java.io.File;
import java.util.Scanner;

import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import main.App;
import main.Config;

public class InitState implements State {

	//Scanner for user input
	Scanner sc = new Scanner(System.in);
	//it stores user input as string
	String inputString = "";
	//it stores the designated hibernate cfg file under src directory
	private String hibernateFileLocation = "";
	//flag for standard hibernate.cfg.xml
	private boolean isStandardConfig = true;

	/**
	 * Method for entry point of the State
	 */
	@Override
	public void runState() {
		printStateMessage();
		requestInput();
		executeCommand();
	}


	/**
	 * Method to initiate hibernate according to the user input
	 */
	@Override
	public void executeCommand() {
		
		switch (this.inputString) {
		case "home":
			new HomeState().runState();
			break;
		case "1":
			if(App.isDbInitiallized) {
				System.out.println("Database connection is already made.");
				new HomeState().runState();
			} else {
				this.isStandardConfig = true;
				
				try {
					initDB();
					App.isDbInitiallized = true;
					App.isMediaLoaded = false;
				} catch(Exception e) {
					App.isDbInitiallized = false;
					App.isMediaLoaded = false;
					e.printStackTrace();
				}
				
				runNextState();
			}
			break;
		case "2":
			if(App.isDbInitiallized) {
				System.out.println("Database connection is already made.");
				new HomeState().runState();
			} else {
				this.isStandardConfig = false;
				System.out.println("Please enter hibernate cfg xml file name under src/ .");
				System.out.print(">>");
				this.inputString = sc.nextLine();
//				this.hibernateFileLocation = "src/" + this.inputString;
				this.hibernateFileLocation = Config.SRC_LOCATION + "/"+ this.inputString;
				if(!isValidInput()) {
					System.out.println("File does not exist.");
					new InitState().runState();
				} else {
					try {
						initDB();
						App.isDbInitiallized = true;
					} catch(Exception e) {
						App.isDbInitiallized = false;
						e.printStackTrace();
					}
					runNextState();
				}
			}
			break;
		case "testmode":
			this.isStandardConfig = false;
			this.hibernateFileLocation = Config.SRC_LOCATION + "/"+"hibernate_update.cfg.xml";
			try {
				this.initDB();
				App.isDbInitiallized = true;
				App.isMediaLoaded = true;
			} catch (Exception e) {
				App.isDbInitiallized = false;
				App.isMediaLoaded = false;
				e.printStackTrace();
			}
			System.out.print("\033[32m");
			System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			System.out.println("@@@ Now initiallized hibernate with hibernate_update.cfg.xml   @@@");
			System.out.println("@@@ DB should have been loaded before running testmode         @@@");
			System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			System.out.print("\033[0m");
			new HomeState().runState();
			break;
		default:
			System.out.println("Invalid input.");
			runState();
			break;
		}
	}


	/**
	 * Method to show state message
	 */
	@Override
	public void printStateMessage() {
		System.out.println("[Initiallize Database]");
		
	}

	/**
	 * Method to request user input
	 */
	@Override
	public void requestInput() {
		System.out.println("*** Available commands ***");
		System.out.println("home: To Home");
		System.out.println("1: Use hibernate.cfg.xml (create mode)");
		System.out.println("2: To specify hibernate cfg xml under src/");
		System.out.println("testmode: To use exsiting DB with hibernate_update.cfg.xml");
		System.out.print(">>");

		this.inputString = sc.nextLine();

	}

	/**
	 * Method to run next state
	 */
	@Override
	public void runNextState() {
		new HomeState().runState();
	}

	/**
	 * Method to set up Configuration file for hibernate
	 * @throws Exception When it is not possible to initiate hibernate with the given configuration
	 */
	private void setUpProperty() throws Exception {
		try {
			if(this.isStandardConfig) {
				App.registry = new StandardServiceRegistryBuilder()
					.configure() // configures settings from hibernate.cfg.xml
					.build();
			} else if(!this.isStandardConfig) {
				App.registry = new StandardServiceRegistryBuilder()
					.configure(new File(this.hibernateFileLocation)) // configures settings from user specific file.
					.build();
			}
			App.sessionFactory = new MetadataSources(App.registry).buildMetadata().buildSessionFactory();
		} catch (Exception e) {
			System.out.println("Failed to build SessionFactory.");
			StandardServiceRegistryBuilder.destroy(App.registry);
			throw new Exception(e);
		}
	}
	
	/**
	 * Method to invoke the method setUpProperty, only when it is not initialized yet.
	 * @throws Exception when failed to init.
	 */
	private void initDB() throws Exception {
		if(App.sessionFactory == null) {
			try {
				this.setUpProperty();
			} catch (Exception e) { 
				System.out.println("Failed to init.");
				throw e;
			}
		} else {
			System.out.println("Database is aleady initialized.");
		}
	}

	/**
	 * Method to validate user input file.
	 * Returns true when the file exists.
	 */
	@Override
	public boolean isValidInput() {
		File file = new File(this.hibernateFileLocation);
		return file.exists();
	}

}
