package hw4.manuelperez;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

public class MyGLRenderer implements GLSurfaceView.Renderer {

	private Context _context;
	
    private static final String TAG = "MyGLRenderer";
    private Triangle mTriangle;
    private Square   mSquare;
    private Cube	mCube;

    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjMatrix = new float[16];
    private final float[] mVMatrix = new float[16];
    private final float[] mRotationMatrix = new float[16];
    private final float[]  mModelMatrix = new float[16];

    public MyGLRenderer(Context context) {
		_context = context;
    }
    
    // Declare as volatile because we are updating it from another thread
    public volatile float mAngle=0;
    public volatile float mAnglex=0;
    public volatile float mAngley=0;
    public volatile int mState=0;
    public volatile int mFace=0;
    public int oldFace=0;

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {

        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        //mTriangle = new Triangle(_context);
        //mSquare   = new Square();
         mCube = new Cube(_context);
    }

    public void onDrawFrame(GL10 unused) {

        // Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mVMatrix, 0, 0, 0, -5, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);
        
        
        if(mState==1)
        	Matrix.setRotateM(mRotationMatrix, 0, mAngle, -1.0f, -1.0f, -1.0f);
        else if(mState==2)
        	Matrix.setRotateM(mRotationMatrix, 0, mAngle, 1.0f, -1.0f, -1.0f);
        else if(mState==3)
        	Matrix.setRotateM(mRotationMatrix, 0, mAngle, 0.0f, -1.0f, 1.0f);
        else if(mState==4)
        {
        	if(oldFace==0)
        		Matrix.setRotateM(mRotationMatrix, 0, mAngle, 1.0f, 0.0f, 0.0f);
        	else
        		Matrix.setRotateM(mRotationMatrix, 0, mAngle, 0.0f, 1.0f, 0.0f);
        	Matrix.multiplyMM(mMVPMatrix, 0, mRotationMatrix, 0, mMVPMatrix, 0);
        	Matrix.setRotateM(mRotationMatrix, 0, mAnglex, 0.0f, 1.0f, 1.0f);
        	Matrix.multiplyMM(mMVPMatrix, 0, mRotationMatrix, 0, mMVPMatrix, 0);
        	Matrix.setRotateM(mRotationMatrix, 0, mAngley, 1.0f, 0.0f, 1.0f);
        	Matrix.multiplyMM(mMVPMatrix, 0, mRotationMatrix, 0, mMVPMatrix, 0);
        }
        else
        {
        	if(mFace==0)
        		Matrix.setRotateM(mRotationMatrix, 0, mAngle, 1.0f, 0.0f, 0.0f);
        	else
        		Matrix.setRotateM(mRotationMatrix, 0, mAngle, 0.0f, 1.0f, 0.0f);
        	oldFace=mFace;
        	mFace=0;
        	mAnglex=0;
        	mAngley=0;
        }
        mState=0;
        	
        // Draw square
       // mSquare.draw(mMVPMatrix);

        // Create a rotation for the triangle
//        long time = SystemClock.uptimeMillis() % 4000L;
//        float angle = 0.090f * ((int) time);
        //Matrix.setRotateM(mRotationMatrix, 0, mAngle, 1.0f, 1.0f, 1.0f);
        // Combine the rotation matrix with the projection and camera view
        Matrix.multiplyMM(mMVPMatrix, 0, mRotationMatrix, 0, mMVPMatrix, 0);

        // Draw triangle
        //mTriangle.draw(mMVPMatrix);
        
        //Draw Cube
        mCube.draw(mMVPMatrix);
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        // Adjust the viewport based on geometry changes,
        // such as screen rotation
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 3, 7);

    }

    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    /**
     * Utility method for debugging OpenGL calls. Provide the name of the call
     * just after making it:
     *
     * <pre>
     * mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
     * MyGLRenderer.checkGlError("glGetUniformLocation");</pre>
     *
     * If the operation is not successful, the check throws an error.
     *
     * @param glOperation - Name of the OpenGL call to check.
     */
    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }
}

class Triangle {

    private final String vertexShaderCode =
        // This matrix member variable provides a hook to manipulate
        // the coordinates of the objects that use this vertex shader
        "uniform mat4 uMVPMatrix;" +

        "attribute vec4 vPosition;" +
        "attribute vec4 a_color;" +
        "attribute vec2 tCoordinate;" +
        "varying vec2 v_tCoordinate;" +
        "varying vec4 v_Color;" +
        "void main() {" +
        // the matrix must be included as a modifier of gl_Position
        "  gl_Position = vPosition * uMVPMatrix;" +
        "	v_tCoordinate = tCoordinate;" +
        "	v_Color = a_color;" +
        "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
            "varying vec4 v_Color;" +
            "varying vec2 v_tCoordinate;" +
            "uniform sampler2D s_texture;" +
            "void main() {" +
            // texture2D() is a build-in function to fetch from the texture map
            "	vec4 texColor = texture2D(s_texture, v_tCoordinate); " + 
            "  gl_FragColor = v_Color*0.5 + texColor*0.5;" +
            "}";

    private final FloatBuffer vertexBuffer, texCoordBuffer, colorBuffer;
    private final int mProgram;
    private int mPositionHandle, mTexCoordHandle;
    private int mColorHandle, mTextureUniformHandle;
    private int mMVPMatrixHandle;
    private int mTextureDataHandle;
    
    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static float triangleCoords[] = { // in counterclockwise order:
         0.0f,  0.622008459f, 0.0f,   // top
        -0.5f, -0.311004243f, 0.0f,   // bottom left
         0.5f, -0.311004243f, 0.0f    // bottom right
    };
    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex (should be 4 bytes per float!?)

    //===================================
    static final int COORDS_PER_TEX = 2;
    static float texCoord[] = {
    	
		0.0f, 0.0f,
    	1.0f, 0.0f, 				
		1.0f, 1.0f

    };
    
    private final int texCoordStride = COORDS_PER_TEX * 4; // 4 bytes per float

    //===================================
    // Set color with red, green, blue and alpha (opacity) values
    float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };

    // Set another color
    static final int COLORB_PER_VER = 4;
    static float colorBlend[] = {
    	1.0f, 0.0f, 0.0f, 1.0f,
    	0.0f, 1.0f, 0.0f, 1.0f,
    	0.0f, 0.0f, 1.0f, 1.0f
    };
    //private final int colorBlendCount = colorBlend.length / COLORB_PER_VER;
    private final int colorBlendStride = COLORB_PER_VER * 4;
    
    //===================================
    public Triangle(Context context) {
    	
    	//===================================
    	// shape coordinate
    	//===================================
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                triangleCoords.length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(triangleCoords);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);

        //===================================
    	// texture coordinate
        //===================================
        // initialize texture coord byte buffer for texture coordinates
        ByteBuffer texbb = ByteBuffer.allocateDirect(
        		texCoord.length * 4);
        // use the device hardware's native byte order
        texbb.order(ByteOrder.nativeOrder());
        
        // create a floating point buffer from the ByteBuffer
        texCoordBuffer = texbb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        texCoordBuffer.put(texCoord);
        // set the buffer to read the first coordinate
        texCoordBuffer.position(0);
        
        //===================================
        // color
        //===================================
        ByteBuffer cbb = ByteBuffer.allocateDirect(
        		colorBlend.length * 4);
        cbb.order(ByteOrder.nativeOrder());
        
        colorBuffer = cbb.asFloatBuffer();
        colorBuffer.put(colorBlend);
        colorBuffer.position(0);
        
        //===================================
        // loading an image into texture
        //===================================
        mTextureDataHandle = loadTexture(context, R.drawable.myicon);

        //===================================
        // shader program
        //===================================
        // prepare shaders and OpenGL program
        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
                                                   vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
                                                     fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables

        
    }

    public static int loadTexture(final Context context, final int resourceId)
    {
        final int[] textureHandle = new int[1];
     
        GLES20.glGenTextures(1, textureHandle, 0);
     
        if (textureHandle[0] != 0)
        {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;   // No pre-scaling
     
            // Read in the resource
            final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
     
            // Bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
     
            // Set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
     
            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
     
            // Recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap.recycle();
        }
     
        if (textureHandle[0] == 0)
        {
            throw new RuntimeException("Error loading texture.");
        }
     
        return textureHandle[0];
    }
    
    public void draw(float[] mvpMatrix) {
        // Add program to OpenGL environment
        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        
        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        
        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                                     GLES20.GL_FLOAT, false,
                                     vertexStride, vertexBuffer);

        // setting vertex color
        mColorHandle = GLES20.glGetAttribLocation(mProgram, "a_color");
        Log.i("chuu", "Error: mColorHandle = "+mColorHandle);
        GLES20.glEnableVertexAttribArray(mColorHandle);
        GLES20.glVertexAttribPointer(mColorHandle, COLORB_PER_VER,
                                     GLES20.GL_FLOAT, false,
                                     colorBlendStride, colorBuffer);
        MyGLRenderer.checkGlError("glVertexAttribPointer...color");
        
        // setting texture coordinate to vertex shader
        mTexCoordHandle = GLES20.glGetAttribLocation(mProgram, "tCoordinate");
        GLES20.glEnableVertexAttribArray(mTexCoordHandle);
        GLES20.glVertexAttribPointer(mTexCoordHandle, COORDS_PER_TEX,
                					GLES20.GL_FLOAT, false,
                					texCoordStride, texCoordBuffer);   
        MyGLRenderer.checkGlError("glVertexAttribPointer...texCoord");
        
        // get handle to fragment shader's vColor member
        //mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        //GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        MyGLRenderer.checkGlError("glGetUniformLocation");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        MyGLRenderer.checkGlError("glUniformMatrix4fv");

        // texture
        mTextureUniformHandle = GLES20.glGetUniformLocation(mProgram, "s_texture");
        
        // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        
        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);
        
        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES20.glUniform1i(mTextureUniformHandle, 0);        
        
        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}

class Square {

    private final String vertexShaderCode =
        // This matrix member variable provides a hook to manipulate
        // the coordinates of the objects that use this vertex shader
        "uniform mat4 uMVPMatrix;" +

        "attribute vec4 vPosition;" +
        "void main() {" +
        // the matrix must be included as a modifier of gl_Position
        "  gl_Position = vPosition * uMVPMatrix;" +
        "}";

    private final String fragmentShaderCode =
        "precision mediump float;" +
        "uniform vec4 vColor;" +
        "void main() {" +
        "  gl_FragColor = vColor;" +
        "}";

    private final FloatBuffer vertexBuffer;
    private final ShortBuffer drawListBuffer;
    private final int mProgram;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static float squareCoords[] = { -0.5f,  0.5f, 0.0f,   // top left
                                    -0.5f, -0.5f, 0.0f,   // bottom left
                                     0.5f, -0.5f, 0.0f,   // bottom right
                                     0.5f,  0.5f, 0.0f }; // top right

    private final short drawOrder[] = { 0, 1, 2, 0, 2, 3 }; // order to draw vertices

    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    // Set color with red, green, blue and alpha (opacity) values
    float color[] = { 0.2f, 0.709803922f, 0.898039216f, 1.0f };

    public Square() {
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
        // (# of coordinate values * 4 bytes per float)
                squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
        // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        // prepare shaders and OpenGL program
        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
                                                   vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
                                                     fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables
    }

    public void draw(float[] mvpMatrix) {
        // Add program to OpenGL environment
        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                                     GLES20.GL_FLOAT, false,
                                     vertexStride, vertexBuffer);

        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        MyGLRenderer.checkGlError("glGetUniformLocation");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        MyGLRenderer.checkGlError("glUniformMatrix4fv");

        // Draw the square
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length,
                              GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}