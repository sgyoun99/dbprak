package main;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class TestTmp {
	
	public static void create() {
		App.registry = new StandardServiceRegistryBuilder()
				.configure(new File(Config.SRC_LOCATION + "/"+"hibernate_create.cfg.xml")) // configures settings from hibernate.cfg.xml
				.build();
		App.sessionFactory = new MetadataSources(App.registry).buildMetadata().buildSessionFactory();
		new DataLoader(App.sessionFactory).load();
	}
	
	public static void testmode() {
		App.registry = new StandardServiceRegistryBuilder()
				.configure(new File(Config.SRC_LOCATION + "/"+"hibernate_validate.cfg.xml")) // configures settings from hibernate.cfg.xml
				.build();
		App.sessionFactory = new MetadataSources(App.registry).buildMetadata().buildSessionFactory();
		new Testtat().getProduct(App.sessionFactory, "B000006MIG");
	}

	public static void main(String[] args) {
//		create();
		testmode();
	}
	
	
}
