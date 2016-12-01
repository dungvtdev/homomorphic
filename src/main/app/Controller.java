package app;

import io.file.WavFile;
import io.file.WavFileException;

import java.io.File;
import java.io.IOException;

/**
 * Created by dung on 01/12/2016.
 */
public class Controller {
    public WavFile wav;

    public void loadWav(File file){
        WavFile w;
        try{
            w = WavFile.openWavFile(file);
            wav = w;
            // read file

            wav.close();
        }catch (IOException e){
            System.out.println("Loi khong mo duoc file");
        }catch (WavFileException e){
            System.out.println("Loi parse file wav");
            e.printStackTrace();
        }
    }
}
