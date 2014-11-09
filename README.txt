						READ ME
By: Manuel Perez
ECE 150 HW 4

This is a six-sided die made through the use of OpenGL ES 2.0. The main
activity begins by creating a button and a MyGLSurfaceView object. The
object type  starts by initializing the renderer. And allows for the render
mode to change when changes are detected. This class also has a touch function,
that when the user touches the screen, the x and y coordinates of the touch
movement are calculated, the angle is modified, an the object is re-rendered.
This allows us to move the dice by spinning it on an axis according to the
user's movement. It also has an onClick() function. This function is called by
the main activity when the user clicks on the button. On this function a for
loops calls render repeatedly while adjusting the object's angle to give the
impression the dice is rolling. Then it uses a random generator to decide on
what face of the die to stop on and renders the object once more.

The render class begins by creating a cube object. Each time a drawFrame is
called (every frame), matrices are used to calculate the dice's rotation and
the cube is redrawn. It has several sates on how to deal with matrices
depending on what function is performing (rolling the dice, turning the dice).

Lastly the cube class defines a vertex shader and a fragment shader needed
to draw the sides of the cube. Several constants are defined regarding the
information in the cube including the cube's drawing order, texture
coordinates, and draw coordinates. The cube object uses several of this
coordinates to initialize several byte buffers with the appropriate information.
The function loadTextureis created and used to overlay a texture (image) over
a dice. The draw function uses the coordinates, byte buffers and texture
to draw the sides of the cube, color them, and texture them. The
GLES20.glDrawElements() allows us to iterate through the arrays and draw
the full cube, triangle by triangle without the need of calling draw()
more than once per instance.