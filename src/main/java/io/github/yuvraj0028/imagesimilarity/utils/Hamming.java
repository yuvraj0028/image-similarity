package io.github.yuvraj0028.imagesimilarity.utils;

public class Hamming {
    Hamming(){}
    
    // this method returns the set bit diff count
    public static int distanceLong(long a, long b){
        long x = a ^ b;
        return Long.bitCount(x);
    }
}
