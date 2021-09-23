package state;

import java.io.File;
import java.util.Scanner;

import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import main.App;
import main.Config;
import main.DataLoader;

public class InitState implements State {

	private Scanner sc = new Scanner(System.in);
	private String inputString = "";
	private String hibernateFileLocation = "";
	private boolean isStandardConfig = true;

	@Override
	public void runState() {
		printStateMessage();
		requestInput();
		executeCommand();
	}


	@Override
	public void executeCommand() {
		
		switch (this.inputString) {
		case "1":
			new HomeState().runState();
			break;
		case "2":
			if(App.isDbInitiallized) {
				System.out.println("Database connection is already made.");
				new HomeState().runState();
			} else {
				this.isStandardConfig = true;
				initDB();
				App.isDbInitiallized = true;
				App.isMediaLoaded = false;
				runNextState();
			}
			break;
		case "3":
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
					initDB();
					App.isDbInitiallized = true;
					runNextState();
				}
			}
			break;
		case "testmode":
			this.isStandardConfig = false;
			this.hibernateFileLocation = Config.SRC_LOCATION + "/"+"hibernate_update.cfg.xml";
			this.initDB();
			App.isDbInitiallized = true;
			App.isMediaLoaded = true;
			System.out.println("@@@ Now initiallized hibernate with hibernate_update.cfg.xml   @@@");
			System.out.println("@@@ DB should have been loaded before running testmode @@@");
			new HomeState().runState();
			break;
		default:
			System.out.println("Invalid input.");
			runState();
			break;
		}
	}

	@Override
	public void responseResult() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void printStateMessage() {
		System.out.println("[Initiallize Database]");
		
	}

	@Override
	public void requestInput() {
		System.out.println("*** Available commands ***");
		System.out.println("1: To Home");
		System.out.println("2: Use hibernate.cfg.xml (create mode)");
		System.out.println("3: To specify hibernate cfg xml under src/");
		System.out.println("testmode: To use exsiting DB with hibernate_update.cfg.xml");
		System.out.print(">>");

		this.inputString = sc.nextLine();

	}

	@Override
	public void runNextState() {
		new HomeState().runState();
	}

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
	
	private void initDB() {
		if(App.sessionFactory == null) {
			try {
				//TODO remove it later
				//App.sessionFactory = new Configuration().configure().buildSessionFactory();
				this.setUpProperty();
			} catch (Exception e) { 
				System.out.println("Failed to init.");
			}
		} else {
			System.out.println("Database is aleady initiallized.");
		}
	}

	@Override
	public boolean isValidInput() {
		File file = new File(this.hibernateFileLocation);
		return file.exists();
	}

}
