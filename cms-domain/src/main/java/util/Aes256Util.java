package util;

import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;


public class Aes256Util {
    public static String alg = "AES/CBC/PKCS5Padding";
    private static final String KEY = "ZEROBASEKEYISZEROBASEKEY"; // key값에 '_' 사용 불가
    private static final String IV = KEY.substring(0,16);

    public static String encrypt(String text){
        try{
            //Cipher :  다양한 암호화 알고리즘을 사용하여 데이터 암호화 및 복호화 기능 제공
            Cipher cipher = Cipher.getInstance(alg);
            // 암호화 작업을 위한 키
            SecretKeySpec key = new SecretKeySpec(KEY.getBytes(), "AES");
            // 암호화 블록 연결(CBC)를 초기치(IV)를 만들기 위해 사용
            IvParameterSpec iv = new IvParameterSpec(IV.getBytes(StandardCharsets.UTF_8));
            //CBC 초기화 (작동 모드 & 키 & IV)
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            byte[] encrypted = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeBase64String(encrypted);
        }catch (Exception e){
            return null;
        }
    }

    public static String decrypt(String text){
        try {
            Cipher cipher = Cipher.getInstance(alg);
            SecretKeySpec key = new SecretKeySpec(KEY.getBytes(), "AES");
            IvParameterSpec iv = new IvParameterSpec(IV.getBytes(StandardCharsets.UTF_8));
            cipher.init(Cipher.DECRYPT_MODE, key, iv);

            byte[] decodeBytes = Base64.decodeBase64(text);
            byte[] decrypted = cipher.doFinal(decodeBytes);
            return new String(decrypted, StandardCharsets.UTF_8);

        }catch (Exception e){
            return null;
        }
    }
}
