package state;

import java.io.File;
import java.util.Scanner;

import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import main.App;
import main.DataLoader;

public class InitState implements State {

	private Scanner sc = new Scanner(System.in);
	private String inputString = "";
	private String hibernateFileLocation = "";
	private boolean isStandardConifig = true;

	@Override
	public void runState() {
		printStateMessage();
		requestInput();
		executeCommand();
		//next state is within executeCommand()
		//runNextState();
	}


	@Override
	public void executeCommand() {
		
		switch (this.inputString) {
		case "1":
			new HomeState().runState();
			break;
		case "2":
			this.isStandardConifig = true;
			initDB();
			runNextState();
			break;
		default:
			this.isStandardConifig = false;
			this.hibernateFileLocation = "src/" + this.inputString;
			if(!isValidInput(hibernateFileLocation)) {
				System.out.println("File does not exist.");
				runState();
			} else {
				initDB();
				runNextState();
			}
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
		System.out.println("1: To Home.");
		System.out.println("2: Use hibernate.cfg.xml file under directory src.");
		System.out.println("File name: To specify hibernate cfg xml file under src directory.");
		System.out.print(">>");

		this.inputString = sc.nextLine();

	}

	@Override
	public void runNextState() {
		new HomeState().runState();
	}

	private void setUp() throws Exception {
		try {
			if(this.isStandardConifig) {
				App.registry = new StandardServiceRegistryBuilder()
					.configure() // configures settings from hibernate.cfg.xml
					.build();
			} else if(!this.isStandardConifig) {
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
				this.setUp();
				new DataLoader(App.sessionFactory).load();
			} catch (Exception e) { 
				System.out.println("Failed to init.");
			}
		} else {
			System.out.println("App is aleady initiallized.");
		}
	}


	@Override
	public boolean isValidInput(String inputString) {
		File file = new File(inputString);
		return file.exists();
	}

}
