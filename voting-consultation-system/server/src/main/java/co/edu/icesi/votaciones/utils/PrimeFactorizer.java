package co.edu.icesi.votaciones.utils;

import java.util.ArrayList;
import java.util.List;

public class PrimeFactorizer {
    public static int countPrimeFactors(long n) {
        List<Long> factors = new ArrayList<>();
        long divisor = 2;

        while (divisor * divisor <= n) {
            if (n % divisor == 0) {
                factors.add(divisor);
                n /= divisor;
            } else {
                divisor++;
            }
        }

        if (n > 1) {
            factors.add(n);
        }

        return factors.size();
    }

    public static boolean isPrime(int number) {
        if (number <= 1) return false;
        for (int i = 2; i <= Math.sqrt(number); i++) {
            if (number % i == 0) return false;
        }
        return true;
    }
}