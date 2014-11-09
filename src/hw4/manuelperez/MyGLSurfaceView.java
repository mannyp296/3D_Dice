package hw4.manuelperez;

import java.util.Random;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

class MyGLSurfaceView extends GLSurfaceView {

    private final MyGLRenderer mRenderer;

    public MyGLSurfaceView(Context context) {
        super(context);

        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);

        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new MyGLRenderer(context);
        setRenderer(mRenderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float mPreviousX;
    private float mPreviousY;

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:

                float dx = x - mPreviousX;
                float dy = y - mPreviousY;

                // reverse direction of rotation above the mid-line
                if (y > getHeight() / 2) {
                  dx = dx * -1 ;
                }

                // reverse direction of rotation to left of the mid-line
                if (x < getWidth() / 2) {
                  dy = dy * -1 ;
                }
               
                mRenderer.mState = 4;
                float negative = -1.0f;
                mRenderer.mAnglex += negative*(dx) * TOUCH_SCALE_FACTOR;  // = 180.0f / 320
                mRenderer.mAngley += negative*(dy) * TOUCH_SCALE_FACTOR;  // = 180.0f / 320
                requestRender();
            
        }

        mPreviousX = x;
        mPreviousY = y;
        return true;
    }
    
    public void Click(){
    	
    	for(int i=0;i<=1100000;i=i+1)
        {
    		
    		mRenderer.mAngle+=(float)i;
    		if(i%80==0)
        		mRenderer.mState = (i%3)+1;
        	requestRender();
        }
    	Random rand = new Random();
        int  value = rand.nextInt(6);
        mRenderer.mState = 0;
        if(value<4)
        {
        	mRenderer.mFace=0;
        	mRenderer.mAngle=(float)(90*value);
        }
        else if(value==4)
        {
        	mRenderer.mFace=1;
        	mRenderer.mAngle=90.0f;
        }
        else if(value==5)
        {
        	mRenderer.mFace=1;
        	mRenderer.mAngle=270.0f;
        }
        requestRender();
    }
}