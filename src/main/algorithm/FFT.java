package algorithm;

public class FFT
{
    public static double[][] fft(double[][] x){
        int n = x[0].length;
        double[][] fx = new double[2][n];
        double[][] ffx = new double[2][n];

        fft_rec(n, 0, 1, x, fx, ffx);

        return fx;
    }

    private static void fft_rec(int n, int offset, int delta, double[][] x, double[][] fx, double[][] ffx){
        int n2 = n/2;            /* half the number of points in FFT */
        int k;                   /* generic index */
        double cs, sn;           /* cosine and sine */
        int k00, k01, k10, k11;  /* indices for butterflies */
        double tmp0, tmp1;       /* temporary storage */
        if (n <= 0)
        {
            return;
        }
        if(n != 2)  /* Perform recursive step. */
        {
              /* Calculate two (N/2)-point DFT's. */
            fft_rec(n2, offset, 2*delta, x, ffx, fx);
            fft_rec(n2, offset+delta, 2*delta, x, ffx, fx);

              /* Combine the two (N/2)-point DFT's into one N-point DFT. */
            for(k=0; k<n2; k++)
            {
                k00 = offset + k*delta;    k01 = k00 + n2*delta;
                k10 = offset + 2*k*delta;  k11 = k10 + delta;
                double alpha = (2*Math.PI*k)/n;
                cs = Math.cos(alpha);
                sn = Math.sin(alpha);
                tmp0 = cs * ffx[0][k11] + sn * ffx[1][k11];
                tmp1 = cs * ffx[1][k11] - sn * ffx[0][k11];
                fx[0][k01] = ffx[0][k10] - tmp0;
                fx[1][k01] = ffx[1][k10] - tmp1;
                fx[0][k00] = ffx[0][k10] + tmp0;
                fx[1][k00] = ffx[1][k10] + tmp1;
            }
        }
        else
        {
            k00 = offset; k01 = k00 + delta;
            fx[0][k01] = x[0][k00] - x[0][k01];
            fx[1][k01] = x[1][k00] - x[1][k01];
            fx[0][k00] = x[0][k00] + x[0][k01];
            fx[1][k00] = x[1][k00] + x[1][k01];
        }
    }

    /* IFFT */
    public static double[][] ifft(double[][] fx)
    {
        int n = fx[0].length;
        int n2 = n/2;       /* half the number of points in IFFT */
        int i;              /* generic index */
        double tmp0, tmp1;  /* temporary storage */

        /* Calculate IFFT via reciprocity property of DFT. */
        double[][] x=fft(fx);
        x[0][0] /=n;    x[1][0] /=n;
        x[0][n2] /=n;  x[1][n2] /=n;
        for(i=1; i<n2; i++)
        {
            tmp0 = x[0][i]/n;       tmp1 = x[1][i]/n;
            x[0][i] = x[0][n-i]/n;  x[1][i] = x[1][n-i]/n;
            x[0][n-i] = tmp0;       x[1][n-i] = tmp1;
        }

        return x;
    }

}