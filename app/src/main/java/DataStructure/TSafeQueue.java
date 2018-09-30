package DataStructure;

import android.util.Log;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import edu.kaist.cps.ubitap.chessclient.Main;

/**
 * Created by root on 18. 5. 20.
 */

public class TSafeQueue {

    public static BlockingQueue<AudioChunk> INbufferQueue = new ArrayBlockingQueue<AudioChunk>(300);

    public  static   void addToINbufferQueue(AudioChunk chunk) throws InterruptedException {
        TSafeQueue.INbufferQueue.put(chunk);
//        Log.d(Main.tag, "Queue Size : "+String.valueOf(INbufferQueue.size()));
    }

    public static synchronized AudioChunk removeFromINbufferQueue() throws InterruptedException {
        return TSafeQueue.INbufferQueue.take();
    }
}