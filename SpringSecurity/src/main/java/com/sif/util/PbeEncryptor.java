package com.sif.util;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

public class PbeEncryptor {
	private static StandardPBEStringEncryptor pbeEnc;
	
	static {
		pbeEnc = new StandardPBEStringEncryptor();
		// 암호화를 하기 위한 salt값 : ENV_PASS 환경변수 이용
		String salt = System.getenv("ENV_PASS");
		pbeEnc.setPassword(salt);
		// 알고리즘 설정
		pbeEnc.setAlgorithm("PBEWithMD5AndDES");
	}
	
	public static String encrypt(String plainText) {
		return pbeEnc.encrypt(plainText);
	}
	
	public static String decrypt(String encText) {
		return pbeEnc.decrypt(encText);
	}

}
