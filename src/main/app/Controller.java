package app;

import algorithm.Homomorphic;
import io.file.WavFile;
import io.file.WavFileException;
import visual.chart.ListSeries;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Created by dung on 01/12/2016.
 */
public class Controller{
    public WavFile wav;
    public Homomorphic homomorphic;
    private int currentOffset;

    public float delayTime=2;
    private RunThread runThread;

    public void setWindowSize(int wndSize){homomorphic.windowSize = wndSize;}

    public void setCnSize(int cnSize){homomorphic.cnSize=cnSize;}

    public WavFile getWav(){
        return wav;
    }

    private void loadWav(File file){
        WavFile w;
        try{
            w = WavFile.openWavFile(file);
            wav = w;

            // read file
            int chanel = 0;
            int nChanels = wav.getNumChannels();
            int nFrames = (int)wav.getNumFrames();
            double[][] raw = new double[nChanels][nFrames];
            wav.readFrames(raw, nFrames);
            if(chanel>=nChanels){
                throw new InvalidParameterException("Chanel index must less than nChanels");
            }
            double[] samples = new double[nFrames];
            for(int i=0;i<nFrames;i++){
                samples[i]= raw[chanel][i];
            }
            wav.close();
            wav.display();

            homomorphic = new Homomorphic(samples, (int)wav.getSampleRate());

        }catch (IOException e){
            System.out.println("Loi khong mo duoc file");
        }catch (WavFileException e){
            System.out.println("Loi parse file wav");
            e.printStackTrace();
        }
    }

    public String openFile(String path){
        File file = new File(path);
        loadWav(file);
        return file.getName();
    }

    public void openFile(File file){
        loadWav(file);
    }

    public double[][] processFormants(){
        double[][] fms = homomorphic.getAllFormants();
        return fms;
    }

    public boolean processNext(HomomorphicProcessListener listener){
        int offset = nextProcessIndex();
        if(offset<0){
            listener.onProcessReturn(false, null, currentOffset);
            return false;
        }else{
            currentOffset = offset;
            process(listener, currentOffset);
            return true;
        }
    }

    private int nextProcessIndex(){
        int offset = currentOffset + homomorphic.windowSize/2;
        int toffset = TruncateSampleIndex(offset);
        return (offset==toffset)? offset:-1;
    }

    public boolean processBack(HomomorphicProcessListener listener){
        int offset = currentOffset - homomorphic.windowSize/2;
        int toffset = TruncateSampleIndex(offset);
        if(offset!=toffset){
            listener.onProcessReturn(false, null, currentOffset);
            return false;
        }else{
            currentOffset = offset;
            process(listener, currentOffset);
            return true;
        }
    }

    public boolean process(HomomorphicProcessListener listener, int offset){
        if(offset>=0)
            currentOffset = offset;
        else
            offset = currentOffset;
        List<double[]> result = homomorphic.process(offset, false);
        listener.onProcessReturn(true,result, offset);
        return true;
    }


    public void processAuto(HomomorphicProcessListener listener, Function callback){
        stopRunning();
        runThread = new RunThread(listener, callback);
        runThread.start();
    }

    public void stopRunning(){
        if(runThread!=null){
            runThread.stopRunning();
            runThread=null;
        }
    }

    public double[] getRawSignal(){
        return homomorphic.copyRaws();
    }

    public int TruncateSampleIndex(int index){
        if(index<0) index=0;
        int n=(int)wav.getNumFrames();
        if(index>= n) index = n-1;
        return index;
    }

    public int getOffset(){return currentOffset;}

    class RunThread extends Thread{
        private HomomorphicProcessListener listener;
        private Function callback;
        boolean loop = false;

        public RunThread(HomomorphicProcessListener listener, Function callback){
            this.callback = callback;
            this.listener = listener;
        }

        @Override
        public void run() {
            loop=true;
            process(listener,currentOffset);
            while(loop){
                try {
                    if(nextProcessIndex()>=0){
                        Thread.sleep((long) (delayTime * 1000));
                    }
                    boolean isContinue = processNext(listener);
                    if(!isContinue){
                        break;
                    }
                }catch(InterruptedException e){
                    System.out.println("Thread process interrupted.");
                }
            }
            System.out.println("Thread process exiting.");
            callback.apply(null);
        }

        public void stopRunning(){
            loop=false;
            this.interrupt();
            callback.apply(null);
        }
    }

}
