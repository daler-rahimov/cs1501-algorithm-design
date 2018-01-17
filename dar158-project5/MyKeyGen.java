/*
 Write a program named MyKeyGen to generate a new keypair
 To generate a keypair, you will need to follow these steps as described in lecture:
 1. Pick P and Q to be random primes of an appropriate size to generate a 1024 bit key
 2. Generate N as P * Q
 3. Generate PHI(N) as (P-1) * (Q-1)
 4. Pick E such that 1 < E < PHI(N) and GCD(E, PHI(N))=1 (E must not divide PHI(N) evenly)
 5. Pick D such that D = E-1 mod PHI(N)
 6. After generating E, D, and N, save E and N to pubkey.rsa and D and N to privkey.rsa
 */

/**
 *
 * @author Daler Rahimov
 */

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;


public class MyKeyGen {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws java.lang.ClassNotFoundException
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String pathPublicFile = "pubkey.rsa";
        String pathPrivateFile = "private.rsa";
        int bitLength = 1024;
        SecureRandom srand = new SecureRandom();
        BigInteger one = new BigInteger("1");
        //Pick P and Q to be random primes of an appropriate size to generate a 1024 bit key
        BigInteger p = BigInteger.probablePrime(bitLength, srand);
        BigInteger q = BigInteger.probablePrime(bitLength, srand);
        //Generate PHI(N) as (P-1) * (Q-1)
        BigInteger phi = (p.subtract(one)).multiply(q.subtract(one));
        //Generate N as P * Q
        BigInteger n = p.multiply(q); //Varies here               
        //Pick E such that 1 < E < PHI(N) and GCD(E, PHI(N))=1 (E must not divide PHI(N) evenly)
        BigInteger e;
        do {
            e = new BigInteger(phi.bitLength(), srand);
        } while (e.compareTo(BigInteger.ONE) <= 0
                || e.compareTo(phi) >= 0
                || !e.gcd(phi).equals(BigInteger.ONE));

        //Pick D such that D = E-1 mod PHI(N)
        BigInteger d = e.modInverse(phi);

        // write the objects to a file 
        List<BigInteger> publicKeys = new ArrayList<>();
        OutputStream fos = new FileOutputStream(pathPublicFile);
        publicKeys.add(e);
        publicKeys.add(n);

        ObjectOutputStream outputStream = new ObjectOutputStream(fos);
        outputStream.writeObject(publicKeys);
        fos.close();

        List<BigInteger> privateKeys = new ArrayList<>();
        fos = new FileOutputStream(pathPrivateFile);
        privateKeys.add(d);
        privateKeys.add(n);

        outputStream = new ObjectOutputStream(fos);
        outputStream.writeObject(privateKeys);
        fos.close();
        
////        System.out.println(e);
//        // Message encryption
//        BigInteger msg = new BigInteger("1991");  // Any integer in the range [0, n)
//        BigInteger enc = msg.modPow(d, n);
////
//        // Message decryption
//        BigInteger dec = enc.modPow(e, n);
//        System.out.println(dec);
    }

}
