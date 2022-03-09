import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Main {
private static String RESULTADO_HASH_IGUALES="La comprobación de la integridad del fichero es correcta",DESede="DESede",
    RESULTADO_HASH_DIFERENTES="El fichero no es el esperado",NOMBRE_FICHERO_HASH="hash.txt", NOMBRE_FICHERO_ENCRIPT ="mensaje.txt.encript",
    CLAVE="clave.raw",MENSAJE_FINAL="Fichero desencriptado y guardado con éxito en el fichero mensaje_claro.txt",MENSAJE_CLARO="mensaje_claro.txt",
    ERROR_NO_EXISTE_FICHERO="No existe fichero de clave %s.",ERROR_ES="Error de E/S leyendo clave de fichero %s";
    public static void main(String[] args) {
        BufferedInputStream bin = crearBufferInputStream(new File(NOMBRE_FICHERO_ENCRIPT));
        byte[] hash = getFileHash(bin, "SHA-256");
        comprobarIntegridadFichero(hash);
        desencriptar();

    }
private static void desencriptar(){
        byte[] valorClave=null;
        SecretKey clave;
        SecretKeySpec keySpec;
    try (FileInputStream fisClave = new FileInputStream(CLAVE)) {
        valorClave = fisClave.readAllBytes();
    } catch (FileNotFoundException e) {
        System.err.printf(ERROR_NO_EXISTE_FICHERO, CLAVE);
        return;
    } catch (IOException e) {
        System.err.printf(ERROR_ES, CLAVE);
        return;
    }

    keySpec = new SecretKeySpec(valorClave,DESede);
    clave = new SecretKeySpec(keySpec.getEncoded(), DESede);

    DecryptManager decryptingManager = new DecryptManager();
    try {
        decryptingManager.decryptFile(DESede, clave, new File(NOMBRE_FICHERO_ENCRIPT), new File(MENSAJE_CLARO));
        System.out.println(MENSAJE_FINAL);
    } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IOException | IllegalBlockSizeException | BadPaddingException e) {
        e.printStackTrace();
    }
}
    private static void comprobarIntegridadFichero(byte[] hash) {
        if(leerHash().equals(toHexString(hash))){
            System.out.println(RESULTADO_HASH_IGUALES);
        }else{
            System.out.println(RESULTADO_HASH_DIFERENTES);
        }
    }

    private static String leerHash(){
        String hash="";
        try(BufferedReader bf = new BufferedReader(new FileReader(NOMBRE_FICHERO_HASH))){
           hash=bf.readLine();
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hash;
    }

    private static String toHexString(byte[] hash) {
        StringBuilder result= new StringBuilder();
        for (byte b : hash) {
            result.append(String.format("%x", b));
        }
        return result.toString();
    }

    private static BufferedInputStream crearBufferInputStream(File fichero) {
        BufferedInputStream bin = null;
        try {
            bin = new BufferedInputStream(new FileInputStream(fichero));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bin;
    }

    private static byte[] getFileHash(BufferedInputStream bin, String algorithm) {
        byte[] hash=new byte[0];
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            md.update(bin.readAllBytes());
            hash = md.digest();
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
        return hash;
    }


}