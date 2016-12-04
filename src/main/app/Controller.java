package app;

import algorithm.Homomorphic;
import io.file.WavFile;
import io.file.WavFileException;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dung on 01/12/2016.
 */
public class Controller {
    public WavFile wav;
    public Homomorphic homomorphic;

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
            int[][] raw = new int[nChanels][nFrames];
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

            homomorphic = new Homomorphic(samples);

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

    public void process(HomomorphicProcessListener listener, int offset){
        List<double[]> result = homomorphic.process(offset);
        listener.onProcessReturn(result, offset);
    }

    public double[] getRawSignal(){
        return homomorphic.copyRaws();
    }
}
