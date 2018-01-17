
/**
 * MySign to sign files and verify signatures. This program should take in two
 * command line arguments, a flag to specify whether it should be signing or
 * verifying ("s" or "v"), and the file that should be signed or verified.
 *
 * e.g., "java MySign s myfile.txt" e.g., "java MySign v myfile.txt.signed"
 *
 * @author Daler Rahimov
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class MySign {

    static String pathPublicFile = "pubkey.rsa";
    static String pathPrivateFile = "private.rsa";

    /**
     * @param args the command line arguments
     * @throws java.security.NoSuchAlgorithmException
     * @throws java.lang.ClassNotFoundException
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws NoSuchAlgorithmException, ClassNotFoundException, IOException {
        String pathFileToSign;
        String pathSignedFile;
        if (args.length != 2) {
            System.out.println("Usage: java MySign [s,v] myfile.txt");
            System.exit(1);
        }
        if (args[0].toLowerCase().equals("s")) {
            pathFileToSign = args[1];
            /*
             If called to sign (e.g., "java MySign s myfile.txt") your program should:
             Generate a SHA-256 hash of the contents of the provided file (e.g., "myfile.txt")
             "decrypt" this hash value using the private key stored in privkey.rsa (i.e., raise it to the Dth power mod N)
             Your program should exit and display an error if privkey.rsa is not found in the current directory
             Write out a signed version of the file (e.g., "myfile.txt.signed") that contains:
             The contents of the original file
             The "decrypted" hash of the original file
             */
            sign(pathFileToSign);
        } else if (args[0].toLowerCase().equals("v")) {
            pathSignedFile = args[1];
            /*
             If called to verify (e.g., "java MySign v myfile.txt.signed") your program should:
             Read the contents of the original file
             Generate a SHA-256 hash of the contents of the orignal file
             Read the "decrypted" hash of the original file
             "encrypt" this value with the contents of pubkey.rsa (i.e., raise it to the Eth power mod N)
             Your program should exit and display an error if pubkey.rsa is not found in the current directory
             Compare these two hash values (the on newly generated and the one that you just "encrypted") and print out to the console whether or not the signature is valid (i.e., whether or not the values are the same).
             */
            // Verifying file 
            boolean isValid = false;
            try {
                isValid = verify(pathSignedFile);
            } catch (Exception e) {
                System.out.println("Signature is NOT valid !!!");
                System.exit(1);
            }
            if (isValid) {
                System.out.println("Signature is valid !!!");
            } else {
                System.out.println("Signature is NOT valid !!!");
            }
        } else {
            System.out.println("Usage: java MySign [s,v] myfile.txt");
        }

    }

    private static void sign(String pathFileToSign) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {

        /// Singing a file
        ///***** RED THE PRIVATE.RSA AND GET D AND N
        BigInteger n;
        BigInteger d;
        String pathSignedFile = pathFileToSign + ".signed";
        List<BigInteger> privateKeys = new ArrayList<>();
        InputStream fis = null;
        try {
            fis = new FileInputStream(pathPrivateFile);
        } catch (FileNotFoundException ex) {
            System.out.println("privkey.rsa is not found in the current directory!");
            System.exit(1);
        }
        ObjectInputStream objectinputstream = new ObjectInputStream(fis);
        privateKeys = (List<BigInteger>) objectinputstream.readObject();
        fis.close();
        d = privateKeys.get(0);
        n = privateKeys.get(1);

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        try {
            File file = new File(pathFileToSign);
            fis = new FileInputStream(file);
        } catch (FileNotFoundException ex) {
            System.out.println(pathFileToSign + " is not found in the current directory!");
            System.exit(1);
        }

        new PrintWriter(pathSignedFile).close();// empty the content of the file
        FileOutputStream fos = new FileOutputStream(pathSignedFile);

        byte[] dataBytes = new byte[1024];
        int nread = 1;
        while ((nread = fis.read(dataBytes)) != -1) {
            fos.write(dataBytes, 0, nread);
            md.update(dataBytes, 0, nread);
        }
        byte[] mdbytes = md.digest();
        BigInteger sha256 = new BigInteger(mdbytes);
        sha256 = sha256.abs();
        BigInteger sha256_sign = sha256.modPow(d, n);

//        System.out.println("n = " + n + "\n e = " + e + "\n d = " + d);
//        System.out.println(sha256_sign.toString(16));
//        System.out.println(sha256.toString(16));
//        System.out.println(sha256_sign.modPow(e, n).toString(16));
        fos.write(sha256_sign.toString(16).getBytes());
        fos.close();
        fis.close();
    }

    private static boolean verify(String pathSignedFile) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
        ///***** RED THE PRIVATE.RSA AND GET D AND N
        BigInteger e;
        BigInteger n;
        List<BigInteger> publicKeys = new ArrayList<>();
        InputStream fis = null;
        try {
            fis = new FileInputStream(pathPublicFile);
        } catch (FileNotFoundException ex) {
            System.out.println("public.rsa is not found in the current directory!");
            System.exit(1);
        }
        ObjectInputStream objectinputstream = new ObjectInputStream(fis);
        publicKeys = (List<BigInteger>) objectinputstream.readObject();
        fis.close();
        e = publicKeys.get(0);
        n = publicKeys.get(1);

        //hash the file
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        fis = null;
        long sizeOfOriginalFile = 0;
        try {
            File file = new File(pathSignedFile);
            sizeOfOriginalFile = file.length() - 512;
            fis = new FileInputStream(file);
        } catch (FileNotFoundException ex) {
            System.out.println(pathSignedFile + " is not found in the current directory!");
            System.exit(1);
        }
        byte[] dataBytes = new byte[1024];
        int nread = 0;
        long totalNRead = 0;
        while (totalNRead < sizeOfOriginalFile) {
            if (totalNRead < sizeOfOriginalFile - 1024) {
                nread = fis.read(dataBytes);
                md.update(dataBytes, 0, nread);
                totalNRead += nread;
            } else {
                nread = fis.read(dataBytes, 0, (int) (sizeOfOriginalFile - totalNRead));
                md.update(dataBytes, 0, nread);
                totalNRead += nread;
//                System.out.println(new String(dataBytes));
            }
        }
        byte[] mdbytes = md.digest();
        BigInteger sha256 = new BigInteger(mdbytes);
        sha256 = sha256.abs();
        byte[] sha256SignatureBytes = new byte[512];
        fis.read(sha256SignatureBytes);
        String sha256SignatureHex = new String(sha256SignatureBytes);

        BigInteger sha256Signature = new BigInteger(sha256SignatureHex, 16);
        BigInteger sha256_incryp = sha256Signature.modPow(e, n);
        fis.close();
        return (sha256.equals(sha256_incryp));
    }
}
