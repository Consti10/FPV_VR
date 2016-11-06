package com.constantin.wilson.FPV_VR;

/**
 * Created by Constantin on 30.10.2016.
 */

public class UnityHacks {
    /*
}
    public class Distortion {
        private float[] coef;
            public float[] get() {
                return coef;
            }
            public  void set(float value) {
                if (value != null) {
                    coef = (float[])value.Clone();
                } else {
                    coef = null;
                }
            }
        }


    public Distortion ApproximateInverse(Distortion distort, float maxRadius,
                                                int numSamples) {
        int numCoefficients = 6;
        numSamples=100;
        maxRadius=1;
        // R + K1*R^3 + K2*R^5 = r, with R = rp = distort(r)
        // Repeating for numSamples:
        //   [ R0^3, R0^5 ] * [ K1 ] = [ r0 - R0 ]
        //   [ R1^3, R1^5 ]   [ K2 ]   [ r1 - R1 ]
        //   [ R2^3, R2^5 ]            [ r2 - R2 ]
        //   [ etc... ]                [ etc... ]
        // That is:
        //   matA * [K1, K2] = y
        // Solve:
        //   [K1, K2] = inverse(transpose(matA) * matA) * transpose(matA) * y
        double[][] matA = new double[numSamples][numCoefficients];
        double[] vecY = new double[numSamples];
        for (int i = 0; i < numSamples; ++i) {
            float r = maxRadius * (i + 1) / (float) numSamples;
            double rp = distort.distort(r);
            double v = rp;
            for (int j = 0; j < numCoefficients; ++j) {
                v *= rp * rp;
                matA[i][j] = v;
            }
            vecY[i] = r - rp;
        }
        double[] vecK = solveLeastSquares(matA, vecY);
        // Convert to float for use in a fresh Distortion object.
        float[] coefficients = new float[vecK.Length];
        for (int i = 0; i < vecK.Length; ++i) {
            coefficients[i] = (float) vecK[i];
        }
        return new Distortion { Coef = coefficients };
    }

    public float distort(float r) {
        float r2 = r * r;
        float ret = 0;
        for (int j=coef.Length-1; j>=0; j--) {
            ret = r2 * (ret + coef[j]);
        }
        return (ret + 1) * r;
    }
      */
}
