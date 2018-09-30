package DataStructure;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.LinearLayout;

import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import edu.kaist.cps.ubitap.chessclient.Main;
import edu.kaist.cps.ubitap.chessclient.R;
import edu.kaist.cps.ubitap.chessclient.Wrapper;

public class CircularQueue {

    int size;
    ArrayList<AudioChunk> cQueue;
    boolean flag;
    int offset=7;
    int offsetCount=0;
    Context c;

    int waitBlocks=13;

    public CircularQueue(int size,Context c){
        this.c=c;
        this.size=size;
        cQueue = new ArrayList<AudioChunk>(size);
        this.flag=false;
        int milliSecondToWait=10;
        this.waitBlocks = (int) Math.ceil(((2*milliSecondToWait)*Wrapper.RECORDER_SAMPLERATE)/((Wrapper.bufferSize/4    )*1000) );

    }

    public boolean insert(AudioChunk chunk)  {

        if((!flag)  ) {

                if (Utils.getAbsByteMax(chunk.data) > Wrapper.THRESHOLD) {
                    Main.t1 = System.currentTimeMillis();
                    this.flag = true;
                }

            this.offsetCount=0;
        }
        else if(flag ) {

            if(this.offsetCount == (this.waitBlocks-1) && this.cQueue.size() == this.size){

                ArrayList<AudioChunk> dataBulk =new ArrayList<AudioChunk>( cQueue.subList(0,this.waitBlocks + 1));
                byte[] b = new byte[0];

                for(int i = 0 ; i< dataBulk.size() ;i++) {
                    b= ArrayUtils.addAll(b,dataBulk.remove(dataBulk.size()-1).data);

                }

                try {

                    Log.d(Main.tag,"Length: "+ b.length);

                    Log.d(Main.tag,"Time: "+ String.valueOf(System.currentTimeMillis()-Main.t1));
                    Utils.sendData(new AudioChunk(b,Utils.getCurrentTime(),Main.ID));

                } catch (IOException e) {
                    e.printStackTrace();
                }

                this.flag = false;

                this.offsetCount=0;
            }

        }
        this.offsetCount++;


        //insert Code
        if(this.cQueue.size() == this.size){
            this.cQueue.remove(this.cQueue.size()-1);
        }
        this.cQueue.add(0,chunk);

        return true;
    }

    public int size(){
        return this.cQueue.size();
    }
}