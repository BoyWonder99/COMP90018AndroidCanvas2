package io.cluo29.github.canvasdemo1;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by CLUO29 on 29/07/18.
 */

// adapted from previous COMP90018

public class GameView extends SurfaceView implements SurfaceHolder.Callback, Runnable{

    // game objects
    public Ball ball;
    GameScore gameScore;

    // SurfaceHolder is to control surface (graphics buffer)
    SurfaceHolder surfaceHolder;

    // game background Thread
    private Thread gameThread = null;

    // thread control flag
    volatile boolean gameThreadRunning = false;

    // screen size
    private int screenWidth;
    private int screenHeight;

    // canvas
    Canvas canvas;


    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);

        surfaceHolder = this.getHolder();
        // register callback
        surfaceHolder.addCallback(this);

    }



    @Override
    public void run()
    {

        gameThreadRunning = true;

        while (gameThreadRunning)
        {
            // draw graphics
            try
            {
                if (surfaceHolder.getSurface().isValid())
                {

                    // get canvas, CPU
                    //canvas = surfaceHolder.lockCanvas();

                    // get canvas, give me GPU, API 23+
                    canvas = surfaceHolder.getSurface().lockHardwareCanvas();

                    if (canvas != null) {

                       // synchronized (surfaceHolder) {

                            canvas.drawColor(Color.BLUE);
                            ball.drawSelf(canvas);
                            gameScore.drawSelf(canvas);




                       // }
                    }

                }
            } catch (Exception e)
            {
                e.printStackTrace();

            } finally
            {
                if (canvas != null)

                    // CPU
                    //surfaceHolder.unlockCanvasAndPost(canvas);
                    // GPU
                    surfaceHolder.getSurface().unlockCanvasAndPost(canvas);
            }


            if(Thread.currentThread().isInterrupted()){
                gameThreadRunning = false;

                Log.d("haha","quit when not blocked");


                return;
            }

            try
            {
                Thread.sleep(10);
            } catch (Exception e)
            {
                e.printStackTrace();


                Log.d("haha","quit when blocked");


                gameThreadRunning = false;


                return;
            }
        }
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        // surfaceView created

        // game init

        // set screen size

        screenWidth = getWidth();
        screenHeight = getHeight();

        // create a ball
        ball = new Ball(screenWidth, screenHeight);

        // set score
        gameScore = new GameScore(0);

        // let thread fly
        gameThread = new Thread(this);

        gameThread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        // surfaceDestroyed, try to stop game


        Log.d("haha","interrupt game thread");



        gameThread.interrupt();


        gameThreadRunning = false;



        // wait for game thread to quit

        try {

            gameThread.join();

        } catch (InterruptedException e) {

        }


        Log.d("haha","quit UI");




    }



}
