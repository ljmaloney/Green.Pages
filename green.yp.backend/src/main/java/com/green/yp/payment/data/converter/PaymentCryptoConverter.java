package com.green.yp.payment.data.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.lang3.StringUtils;

@Converter
public class PaymentCryptoConverter implements AttributeConverter<String, byte[]> {
  private static final String AES = "AES/CBC/PKCS5Padding";
  private static final String SECRET = "BZUMvZK7y5ktMEn3w";
  private final Key key;

  public PaymentCryptoConverter() throws Exception {
    key = getKeyFromPassword(SECRET, "SSAABB");
  }

  @Override
  public byte[] convertToDatabaseColumn(String attribute) {
    if (StringUtils.isBlank(attribute)) {
      return null;
    }

    try {
      Cipher cipher = Cipher.getInstance(AES);
      byte[] ivByte = new byte[16];
      IvParameterSpec ivParams = new IvParameterSpec(ivByte);
      cipher.init(Cipher.ENCRYPT_MODE, key, ivParams);
      return cipher.doFinal(attribute.getBytes());
    } catch (Exception e) {
      throw new RuntimeException("Unexpected exception encrypting data field", e);
    }
  }

  @Override
  public String convertToEntityAttribute(byte[] dbData) {
    if (dbData == null) {
      return null;
    }
    try {
      Cipher cipher = Cipher.getInstance(AES);
      // the block size (in bytes), or 0 if the underlying algorithm is not a block cipher
      byte[] ivByte = new byte[16];
      // This class specifies an initialization vector (IV). Examples which use
      // IVs are ciphers in feedback mode, e.g., DES in CBC mode and RSA ciphers with OAEP encoding
      // operation.
      IvParameterSpec ivParamsSpec = new IvParameterSpec(ivByte);
      cipher.init(Cipher.DECRYPT_MODE, key, ivParamsSpec);
      return new String(cipher.doFinal(dbData));
    } catch (Exception e) {
      throw new RuntimeException("Unexpected exception decrypting data field", e);
    }
  }

  public static SecretKey getKeyFromPassword(String password, String salt)
      throws NoSuchAlgorithmException, InvalidKeySpecException {

    SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
    KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
    return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
  }
}
