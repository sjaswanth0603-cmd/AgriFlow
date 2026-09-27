package com.agriflow.m6_parallel;

import java.math.BigInteger;
import java.util.Random;

/**
 * Miller-Rabin Randomized Primality Testing Algorithm
 * 
 * Agricultural Use Case:
 * Authenticates cryptographic digital passports for organic grain export shipments.
 * Verifies large prime parameters for RSA / ECC key generation in supply-chain traceability.
 * 
 * Time Complexity: O(k * log^3 N) where k is number of test iterations
 * Error Probability: < (1/4)^k
 */
public class MillerRabinVerifier {

    private static final Random RNG = new Random();

    /**
     * Determines whether 'n' is probably prime using 'k' independent Miller-Rabin rounds.
     */
    public static boolean isProbablePrime(BigInteger n, int k) {
        if (n.compareTo(BigInteger.ONE) <= 0) return false;
        if (n.equals(BigInteger.TWO) || n.equals(BigInteger.valueOf(3))) return true;
        if (n.mod(BigInteger.TWO).equals(BigInteger.ZERO)) return false;

        // Write n - 1 as 2^s * d with d odd
        BigInteger d = n.subtract(BigInteger.ONE);
        int s = 0;
        while (d.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
            d = d.divide(BigInteger.TWO);
            s++;
        }

        // Witness loop
        for (int i = 0; i < k; i++) {
            BigInteger a = uniformRandom(BigInteger.TWO, n.subtract(BigInteger.TWO));
            BigInteger x = a.modPow(d, n);

            if (x.equals(BigInteger.ONE) || x.equals(n.subtract(BigInteger.ONE))) {
                continue;
            }

            boolean composite = true;
            for (int r = 1; r < s; r++) {
                x = x.modPow(BigInteger.TWO, n);
                if (x.equals(n.subtract(BigInteger.ONE))) {
                    composite = false;
                    break;
                }
            }

            if (composite) {
                return false; // Definitely composite
            }
        }

        return true; // Probably prime with error probability < 4^(-k)
    }

    private static BigInteger uniformRandom(BigInteger bottom, BigInteger top) {
        BigInteger res;
        do {
            res = new BigInteger(top.bitLength(), RNG);
        } while (res.compareTo(bottom) < 0 || res.compareTo(top) > 0);
        return res;
    }

    public static void main(String[] args) {
        // Test numbers
        BigInteger[] testBatchIds = {
            BigInteger.valueOf(104729),              // Known Prime
            BigInteger.valueOf(104730),              // Even composite
            new BigInteger("982451653"),             // 10-digit prime
            new BigInteger("982451655")              // Composite ending in 5
        };

        System.out.println("--- Grain Batch Cryptographic Seed Verification (Miller-Rabin) ---");
        for (BigInteger id : testBatchIds) {
            boolean prime = isProbablePrime(id, 10);
            System.out.printf("Batch Key %s: %s%n", id, prime ? "VALID PRIME (Certified Tag)" : "INVALID (Composite)");
        }
    }
}
