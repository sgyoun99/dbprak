package main;

import frontend.HomeState;

public class App {
	
	public static void main(String[] args) {
		HomeState homeState = new HomeState();
		homeState.runState();
	}

}
