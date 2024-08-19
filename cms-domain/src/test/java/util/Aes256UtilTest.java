package util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class Aes256UtilTest {

    @Test
    void encrypt(){
        String encrypt = Aes256Util.encrypt("Hello world");
        System.out.println(encrypt);
        System.out.println(Aes256Util.decrypt(encrypt));
        assertEquals(Aes256Util.decrypt(encrypt), "Hello world");
    }

}