package algorithm;

import app.Controller;
import io.file.WavFile;

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
    public int sampleRate=1;

    public double zcrThreshold=125;
    public double powerThreshold=8.2;

    public Homomorphic(double[] samples, int sampleRate) {
        this.raws = samples;
        this.sampleRate = sampleRate;
    }

    public double nSamplesToSecond() {
        double ntos = 1.0 / sampleRate;
        return ntos;
    }

    public double nSamplesToFrequency(double size) {
        double ntof = sampleRate / size;
        return ntof;
    }

    public double[][] getAllFormants(){
        int n=raws.length/(windowSize/2);
        double[][] result = new double[n][];
        int offset=0;
        for(int i=0;i<n;i++){
            List<double[]> r = process(offset, true);
            if(r!=null) {
                result[i] = r.get(0);
            }else{
                result[i]=null;
            }
            offset+=windowSize/2;
        }
        return result;
    }

    public List<double[]> process(int offset, boolean formantOnly) {
        boolean signalHasF0 = hasF0(raws, offset, windowSize);
        if(formantOnly && !signalHasF0){
            return null;
        }

        List<double[]> result = new ArrayList<double[]>();

        // adjust
        double[] samples = sampleAndAdjust(raws, offset, windowSize);

        // hamming
        hammingFilter(samples);
//        result.add(samples);              // return hamming, chua copy

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
        double max = Math.pow(10.0, fceps[0][0]);
        for(int i=0;i<n;i++){
            mresponse[i] = Math.pow(10.0, fceps[i][0]);
            if(max<mresponse[i])
                max = mresponse[i];
        }
        // to dB
        for(int i=0;i<n;i++){
            mresponse[i] = 20*Math.log10(mresponse[i]/max);
        }
//        result.add(mresponse);

        // phase response
        double[] presponse = new double[n];
        double log10e = Math.log10(Math.E);
        for(int i=0;i<n;i++){
            presponse[i]=fceps[i][1]/log10e;
        }
//        result.add(presponse);

        // formant
        int nFormant = 0;
        double[] fms=null;

        if(signalHasF0) {
            fms = new double[5];
            for (int i = 1; i < n / 2; i++) {
                if ((mresponse[i] > mresponse[i + 1]) & (mresponse[i] > mresponse[i - 1])) {
                    fms[nFormant] = i * sampleRate / windowSize;
                    nFormant++;
                    if (nFormant >= 5) break;
                }
            }
            if (nFormant < fms.length) {
                double[] f = new double[nFormant];
                for (int i = 0; i < nFormant; i++)
                    f[i] = fms[i];
                fms = f;
            }
//            result.add(fms);
        }

        if(!formantOnly){
            result.add(samples);              // return hamming, chua copy
            result.add(mresponse);
            result.add(presponse);
        }
        result.add(fms);

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

    private boolean hasF0(double[] sample, int offset, int size){
        double zcr = 0, power=0;
        int n = Math.min(sample.length, offset+size);
        power=sample[offset]*sample[offset];
        for(int i=offset+1;i<n;i++){
            if((sample[i-1] < 0 && sample[i]>0) ||(sample[i-1]>0 && sample[i]<0)){
                zcr+=1;
            }
            power+=sample[i]*sample[i];
        }
//        power*=1E10;
        System.out.println("ZCR "+zcr+" power "+power);
        return zcr<=zcrThreshold && power>=powerThreshold;
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
