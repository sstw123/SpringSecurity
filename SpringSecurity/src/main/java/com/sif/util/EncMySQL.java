package com.sif.util;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Scanner;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

public class EncMySQL {
	
	public static void main(String[] args) {
		
		StandardPBEStringEncryptor pbe = new StandardPBEStringEncryptor();
		Map<String, String> envList = System.getenv();
		
		pbe.setAlgorithm("PBEWithMD5AndDES");
		pbe.setPassword(envList.get("ENV_PASS"));
		
		System.out.println(envList.get("ENV_PASS"));
		
		Scanner scanner = new Scanner(System.in);
		
		System.out.print("MySQL Username >> ");
		String username = scanner.nextLine();
		
		System.out.print("MySQL Password >> ");
		String password= scanner.nextLine();
		
		String encUserName = pbe.encrypt(username);
		String encPassword = pbe.encrypt(password);
		
		String saveFile = "./src/main/webapp/WEB-INF/spring/properties/db.connection.properties";
		
		String saveUserName = String.format("mysql.username=ENC(%s)", encUserName);
		String savePassword = String.format("mysql.password=ENC(%s)", encPassword);
		
		try {
			PrintWriter pw = new PrintWriter(saveFile);
			pw.println(saveUserName);
			pw.println(savePassword);
			pw.flush();
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		scanner.close();
		System.out.println("db.connection.properties 저장 완료!");
		
		
	}

}
