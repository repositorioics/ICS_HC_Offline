package com.ics.ics_hc_offline.utils;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public class DesEncrypter {
    Cipher cipher;
    byte[] salt;
    private static final int ITERATION_COUNT = 19;

    public DesEncrypter(final String pCadenaClave) {
        this.salt = new byte[] { -87, -101, -56, 50, 86, 53, -29, 3 };
        try {
            final KeySpec iEspecClave = new PBEKeySpec(pCadenaClave.toCharArray(), this.salt, 19);
            final SecretKey iClaveSecreta = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(iEspecClave);
            this.cipher = Cipher.getInstance(iClaveSecreta.getAlgorithm());
            final AlgorithmParameterSpec iEspecParametro = new PBEParameterSpec(this.salt, 19);
            this.cipher.init(1, iClaveSecreta, iEspecParametro);
        }
        catch (InvalidAlgorithmParameterException ex) {}
        catch (InvalidKeySpecException ex2) {}
        catch (NoSuchPaddingException ex3) {}
        catch (NoSuchAlgorithmException ex4) {}
        catch (InvalidKeyException ex5) {}
    }

    public String encrypt(final String pCadena) {
        try {
            final byte[] stringBytesUTF8 = pCadena.getBytes("UTF-8");
            final byte[] iEncriptado = this.cipher.doFinal(stringBytesUTF8);
            return Base64.encodeToString(iEncriptado, Base64.DEFAULT);
        }
        catch (BadPaddingException ex) {}
        catch (IllegalBlockSizeException ex2) {}
        catch (UnsupportedEncodingException ex3) {}
        return null;
    }
}
