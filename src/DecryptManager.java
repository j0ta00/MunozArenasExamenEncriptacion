import javax.crypto.*;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class DecryptManager {
    public void decryptFile(String algorithm, SecretKey key, File inputFile, File outputFile) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key);
        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(outputFile);
             BufferedInputStream is = new BufferedInputStream(fis);
             BufferedOutputStream os = new BufferedOutputStream(fos)) {
            byte[] buff = new byte[cipher.getBlockSize()];
            while (is.read(buff) != -1) os.write(cipher.update(buff));
            os.write(cipher.doFinal());
        }
    }
}
