package algorithm;

public class FFT
{
    public static double[][] fft(double[][] x){
        int n = x.length;
        double[][] fx = new double[n][2];
        double[][] ffx = new double[n][2];

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
                tmp0 = cs * ffx[k11][0] + sn * ffx[k11][1];
                tmp1 = cs * ffx[k11][1] - sn * ffx[k11][0];
                fx[k01][0] = ffx[k10][0] - tmp0;
                fx[k01][1] = ffx[k10][1] - tmp1;
                fx[k00][0] = ffx[k10][0] + tmp0;
                fx[k00][1] = ffx[k10][1] + tmp1;
            }
        }
        else
        {
            k00 = offset; k01 = k00 + delta;
            fx[k01][0] = x[k00][0] - x[k01][0];
            fx[k01][1] = x[k00][1] - x[k01][1];
            fx[k00][0] = x[k00][0] + x[k01][0];
            fx[k00][1] = x[k00][1] + x[k01][1];
        }
    }

    /* IFFT */
    public static double[][] ifft(double[][] fx)
    {
        int n = fx.length;
        int n2 = n/2;       /* half the number of points in IFFT */
        int i;              /* generic index */
        double tmp0, tmp1;  /* temporary storage */

        /* Calculate IFFT via reciprocity property of DFT. */
        double[][] x=fft(fx);
        x[0][0] = x[0][0]/n;    x[0][1] = x[0][1]/n;
        x[n2][0] = x[n2][0]/n;  x[n2][1] = x[n2][1]/n;
        for(i=1; i<n2; i++)
        {
            tmp0 = x[i][0]/n;       tmp1 = x[i][1]/n;
            x[i][0] = x[n-i][0]/n;  x[i][1] = x[n-i][1]/n;
            x[n-i][0] = tmp0;       x[n-i][1] = tmp1;
        }

        return x;
    }

}