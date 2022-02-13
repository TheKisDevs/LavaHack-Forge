package com.kisman.cc.util.protect.keyauth;

import java.util.Scanner;

import com.kisman.cc.Kisman;
import com.kisman.cc.util.protect.keyauth.api.KeyAuth;

/**
 * @author SprayD
 */
public class KeyAuthApp {
	private static String url = "https://keyauth.win/api/1.1/";
	
	private static String ownerid = "hW04ojuQKY"; // You can find out the owner id in the profile settings keyauth.com
	private static String appname = "kismanccplus"; // Application name
	private static String version = "1.0"; // Application version

	public static KeyAuth keyAuth = new KeyAuth(appname, ownerid, version, url);

	public static void init() throws InterruptedException {
		println("KeyAuth API Example");
		println("\n\n Connecting...");
		//keyAuth.init();

		println("\n\n [1] Login\n [2] Upgrade\n [3] License key only\n\n Choose option: ");

		Scanner scanner = new Scanner(System.in);

		String username;
		String password;
		String key;

		int option = Integer.parseInt(scanner.nextLine());
		switch (option) {
		case 1:
			println("\n\n Enter username: ");
			username = scanner.nextLine();
			println("\n\n Enter password: ");
			password = scanner.nextLine();

			keyAuth.login(username, password);
			break;
		case 2:
			println("\n\n Enter username: ");
			username = scanner.nextLine();
			println("\n\n Enter license key: ");
			key = scanner.nextLine();

			keyAuth.upgrade(username, key);
			break;
		case 3:
			println("\n\n Enter license key: ");
			key = scanner.nextLine();

			keyAuth.license(key);
			break;
		default:
			println("\n\n Invalid Selection");
			Thread.sleep(3000);
			System.exit(0);
			break;
		}

		println("\n\n  Exit the program...");

		Thread.sleep(3200);
		System.exit(0);
	}

	private static void println(String text) {
		System.out.println(text);
	}
}
