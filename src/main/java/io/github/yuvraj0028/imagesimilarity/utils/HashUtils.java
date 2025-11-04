package io.github.yuvraj0028.imagesimilarity.utils;

import java.awt.image.BufferedImage;

public class HashUtils {
    private HashUtils() {}

    public static long dHash64(BufferedImage img) {
        BufferedImage gray = ImageUtils.toGrayscale(img);
        BufferedImage small = ImageUtils.resize(gray, 9, 8);

        long hash = 0L;
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                int left = ImageUtils.getGray(small, x, y);
                int right = ImageUtils.getGray(small, x + 1, y);
                hash <<= 1;
                if (left > right) hash |= 1L;
            }
        }
        return hash;
    }

    public static long blockHash64(BufferedImage img) {
        BufferedImage gray = ImageUtils.toGrayscale(img);
        BufferedImage small = ImageUtils.resize(gray, 8, 8);

        int[] vals = new int[64];
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                vals[y * 8 + x] = ImageUtils.getGray(small, x, y);
            }
        }
        int median = median(vals);
        long hash = 0L;
        for (int i = 0; i < 64; i++) {
            hash <<= 1;
            if (vals[i] > median) hash |= 1L;
        }
        return hash;
    }

    private static int median(int[] arr) {
        int[] copy = arr.clone();
        java.util.Arrays.sort(copy);
        return copy[copy.length / 2];
    }

    public static long pHash64(BufferedImage img) {
        BufferedImage gray = ImageUtils.toGrayscale(img);
        BufferedImage small = ImageUtils.resize(gray, 32, 32);

        double[][] vals = new double[32][32];
        for (int y = 0; y < 32; y++) {
            for (int x = 0; x < 32; x++) {
                vals[y][x] = ImageUtils.getGray(small, x, y);
            }
        }

        double[][] dct = applyDCT(vals);

        double[] top = new double[64];
        int idx = 0;
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                top[idx++] = dct[y][x];
            }
        }

        double median = median(top);
        long hash = 0L;
        for (double v : top) {
            hash <<= 1;
            if (v > median) hash |= 1L;
        }
        return hash;
    }

    private static double median(double[] arr) {
        double[] copy = arr.clone();
        java.util.Arrays.sort(copy);
        return copy[copy.length / 2];
    }

    private static double[][] applyDCT(double[][] f) {
        int N = f.length;
        double[][] F = new double[N][N];
        double c1 = Math.PI / (2.0 * N);
        for (int u = 0; u < N; u++) {
            for (int v = 0; v < N; v++) {
                double sum = 0.0;
                for (int i = 0; i < N; i++) {
                    for (int j = 0; j < N; j++) {
                        sum += f[i][j] * Math.cos((2.0 * i + 1.0) * u * c1) * Math.cos((2.0 * j + 1.0) * v * c1);
                    }
                }
                double cu = u == 0 ? 1.0 / Math.sqrt(2) : 1.0;
                double cv = v == 0 ? 1.0 / Math.sqrt(2) : 1.0;
                F[u][v] = 0.25 * cu * cv * sum;
            }
        }
        return F;
    }
}
