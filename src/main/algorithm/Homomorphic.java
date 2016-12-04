package algorithm;

import app.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * Created by dung on 02/12/2016.
 */
public class Homomorphic {
    private double[] raws;

    public int windowSize=512;
    public int cnSize=19;

    public Homomorphic(double[] samples) {
        this.raws = samples;
    }


    public List<double[]> process(int offset) {
        List<double[]> result = new ArrayList<double[]>();

        // adjust
        double[] samples = sampleAndAdjust(raws, offset, windowSize);

        // hamming
        hammingFilter(samples);
        result.add(samples);              // return hamming, chua copy

        // FFT, log, FFT-1
        double[][] complexSamples = realToComplex(samples);
        double[][] fsamples = FFT.fft(complexSamples);
        log10Complex(fsamples);
        double[][] ifsamples = FFT.ifft(fsamples);      // ra cepstrum

        // windows cn
        int n = ifsamples.length;
        for(int i=cnSize;i<n;i++){
            ifsamples[i][0]=0;
            ifsamples[i][1]=0;
        }

        // reverse to frequency response
        double[][] fceps = FFT.fft(ifsamples);

        // mag response
        double[] mresponse = new double[n];
        for(int i=0;i<n;i++){
            mresponse[i] = Math.pow(10.0, fceps[i][0]);
        }
        result.add(mresponse);

        // phase response
        double[] presponse = new double[n];
        double log10e = Math.log10(Math.E);
        for(int i=0;i<n;i++){
            presponse[i]=fceps[i][1]/log10e;
        }
        result.add(presponse);

        return result;
    }


    /*
     * Adjust
     */
    private double[] sampleAndAdjust(double[] samples, int offset, int size) {
        return sampleAndAdjust(samples, offset, size, 0.95f, true);
    }

    private double[] sampleAndAdjust(double[] samples, int offset, int size, float a, boolean expand) {
        if (offset + size > samples.length && !expand) {
            size = samples.length - offset;
        }
        double[] r = new double[size];
        if (offset == 0) r[0] = samples[0];
        int to = Math.min(size, samples.length-offset);
        for (int i = 1; i < to; i++) {
            r[i] = samples[offset + i] - a * samples[offset + i - 1];
        }
        for(int i=to;i<size;i++) r[i]=0;
        return r;
    }


    /*
     * hamming
     */
    private void hammingFilter(double[] samples){
        int wsize = samples.length;
        double[] hamming = getHammingSeries(wsize);
        for(int i=0;i<wsize;i++){
            samples[i]*=hamming[i];
        }
    }

    double[] cachedHamming;
    private double[] getHammingSeries(int wndSize){
        if(cachedHamming==null || cachedHamming.length !=wndSize){
            cachedHamming=new double[wndSize];
            for(int i=0;i<wndSize;i++){
                cachedHamming[i] = - (0.54 - 0.46 * Math.cos(2 * Math.PI * i / (wndSize - 1)));
            }
        }
        return cachedHamming;
    }

    /*
     * log10
     */
    private void log10Complex(double[][] numbers){
        int n = numbers.length;
        double m, p, log10e=Math.log10(Math.E);
        for(int i=0;i<n;i++){
            m = moduleComplex(numbers[i]);
            p = phaseComplex(numbers[i]);
            numbers[i][0] = Math.log10(m);
            numbers[i][1] = log10e*p;
        }
    }

    private double moduleComplex(double[] complex){
        return Math.sqrt(complex[0]*complex[0]+complex[1]*complex[1]);
    }

    private double phaseComplex(double[] complex){
        return Math.atan2(complex[1], complex[0]);
    }


    private double[] copyList(double[] input){
        return copyList(input,0,-1);
    }

    private double[] copyList(double[] input, int start, int size) {
        int n = input.length;
        if(size<0){
            size = n-start;
        }else if(n < start+size){
            size = n - start;
        }
        double[] output = new double[size];
        for (int i = 0; i < n; i++) {
            output[i] = input[i+start];
        }
        return output;
    }

    private double[][] realToComplex(double[] numbers){
        int n = numbers.length;
        double[][] com = new double[n][2];
        for(int i=0;i<n;i++){
            com[i][0] = numbers[i];
            com[i][1] = 0;
        }
        return com;
    }

    public double[] copyRaws(){
        return copyList(raws);
    }

}
