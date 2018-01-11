package javax.microedition.m3g;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.lcdui.Graphics;

public final class Graphics3D {
	public static final int ANTIALIAS = 2;
	public static final int DITHER = 4;
	public static final int OVERWRITE = 16;
	public static final int TRUE_COLOR = 8;

	private static final String PROPERTY_SUPPORT_ANTIALIASING = "supportAntialiasing";
	private static final String PROPERTY_SUPPORT_TRUECOLOR = "supportTrueColor";
	private static final String PROPERTY_SUPPORT_DITHERING = "supportDithering";
	private static final String PROPERTY_SUPPORT_MIPMAPPING = "supportMipmapping";
	private static final String PROPERTY_SUPPORT_PERSPECTIVE_CORRECTION = "supportPerspectiveCorrection";
	private static final String PROPERTY_SUPPORT_LOCAL_CAMERA_LIGHTING = "supportLocalCameraLighting";
	private static final String PROPERTY_MAX_LIGHTS = "maxLights";
	private static final String PROPERTY_MAX_VIEWPORT_WIDTH = "maxViewportWidth";
	private static final String PROPERTY_MAX_VIEWPORT_HEIGHT = "maxViewportHeight";
	private static final String PROPERTY_MAX_VIEWPORT_DIMENSION = "maxViewportDimension";
	private static final String PROPERTY_MAX_TEXTURE_DIMENSION = "maxTextureDimension";
	private static final String PROPERTY_MAX_SPRITE_CROP_DIMENSION = "maxSpriteCropDimension";
	private static final String PROPERTY_MAX_TRANSFORM_PER_VERTEX = "maxTransformsPerVertex";
	private static final String PROPERTY_MAX_TEXTURE_UNITS = "numTextureUnits";

	private static Graphics3D instance = null;

	private int maxTextureUnits = 1;
	private static int maxTextureSize;

	private int viewportX = 0;
	private int viewportY = 0;
	private int viewportWidth = 0;
	private int viewportHeight = 0;
	private int maxViewportWidth = 0;
	private int maxViewportHeight = 0;

	private EGL10 egl;
	private EGLConfig eglConfig;
	private EGLDisplay eglDisplay;
	private EGLSurface eglWindowSurface;
	private EGLContext eglContext;
	private GL10 gl = null;

	private Object renderTarget;
	private boolean targetBound = false;

	private Camera camera;
	private Transform cameraTransform;

	private Vector lights = new Vector();
	private Vector lightTransforms = new Vector();
	private static boolean[] lightFlags;
	private int maxLights = 1;
	private boolean lightHasChanged = false;

	private Background defaultBackground = new Background();

	private boolean cameraHasChanged = false;

	private float depthRangeNear = 0;
	private float depthRangeFar = 1;
	private boolean depthRangeHasChanged = false;

	private boolean depthBufferEnabled;
	private boolean overwrite;
	private int hints;

	private static Hashtable implementationProperties = new Hashtable();

	private int width, height;

	private int clipX0, clipY0, clipX1, clipY1;
	private int scissorX, scissorY, scissorWidth, scissorHeight;

	private Graphics3D() {
		initGLES();
		populateProperties();
	}

	public static Graphics3D getInstance() {
		if (instance == null) {
			instance = new Graphics3D();
		}
		return instance;
	}

	private void initGLES() {
		// Create EGL context
		this.egl = (EGL10) EGLContext.getEGL();

		this.eglDisplay = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
		EGL_ASSERT(eglDisplay != EGL10.EGL_NO_DISPLAY);

		int[] major_minor = new int[2];
		EGL_ASSERT(egl.eglInitialize(eglDisplay, major_minor));

		int[] num_config = new int[1];
		//EGL_ASSERT(egl.eglGetConfigs(eglDisplay, null, 0, num_config));
		int[] s_configAttribs = {
				EGL10.EGL_SURFACE_TYPE, EGL10.EGL_PBUFFER_BIT,
				EGL10.EGL_RED_SIZE, 8,
				EGL10.EGL_GREEN_SIZE, 8,
				EGL10.EGL_BLUE_SIZE, 8,
				EGL10.EGL_ALPHA_SIZE, 8,
				EGL10.EGL_DEPTH_SIZE, 8,
				EGL10.EGL_STENCIL_SIZE, EGL10.EGL_DONT_CARE,
				EGL10.EGL_NONE};
		EGLConfig[] eglConfigs = new EGLConfig[1];
		EGL_ASSERT(egl.eglChooseConfig(eglDisplay, s_configAttribs, eglConfigs, 1, num_config));
		this.eglConfig = eglConfigs[0];

		this.eglContext = egl.eglCreateContext(eglDisplay, eglConfig, EGL10.EGL_NO_CONTEXT, null);
		EGL_ASSERT(eglContext != EGL10.EGL_NO_CONTEXT);

		int[] eglPbufferAttribs = {
			EGL10.EGL_WIDTH, 1,
			EGL10.EGL_HEIGHT, 1,
			EGL10.EGL_NONE};
		EGLSurface tmpSurface = egl.eglCreatePbufferSurface(eglDisplay, eglConfig, eglPbufferAttribs);
		EGL_ASSERT(tmpSurface != EGL10.EGL_NO_SURFACE);
		EGL_ASSERT(egl.eglMakeCurrent(eglDisplay, tmpSurface, tmpSurface, eglContext));

		this.gl = (GL10) eglContext.getGL();

		// Get parameters from the GL instance
		int[] params = new int[2];
		gl.glGetIntegerv(GL10.GL_MAX_TEXTURE_UNITS, params, 0);
		maxTextureUnits = params[0];
		gl.glGetIntegerv(GL10.GL_MAX_LIGHTS, params, 0);
		maxLights = params[0];
		lightFlags = new boolean[maxLights];
		gl.glGetIntegerv(GL10.GL_MAX_VIEWPORT_DIMS, params, 0);
		maxViewportWidth = params[0];
		maxViewportHeight = params[1];
		gl.glGetIntegerv(GL10.GL_MAX_TEXTURE_SIZE, params, 0);
		maxTextureSize = params[0];
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		EGL_ASSERT(egl.eglMakeCurrent(eglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT));
		EGL_ASSERT(egl.eglDestroySurface(eglDisplay, tmpSurface));
	}

	private void populateProperties() {
		implementationProperties.put(PROPERTY_SUPPORT_ANTIALIASING, new Boolean(true));
		implementationProperties.put(PROPERTY_SUPPORT_TRUECOLOR, new Boolean(true));
		implementationProperties.put(PROPERTY_SUPPORT_DITHERING, new Boolean(false));
		implementationProperties.put(PROPERTY_SUPPORT_MIPMAPPING, new Boolean(false));
		implementationProperties.put(PROPERTY_SUPPORT_PERSPECTIVE_CORRECTION, new Boolean(true));
		implementationProperties.put(PROPERTY_SUPPORT_LOCAL_CAMERA_LIGHTING, new Boolean(false));
		implementationProperties.put(PROPERTY_MAX_LIGHTS, new Integer(maxLights));
		implementationProperties.put(PROPERTY_MAX_VIEWPORT_WIDTH, new Integer(maxViewportWidth));
		implementationProperties.put(PROPERTY_MAX_VIEWPORT_HEIGHT, new Integer(maxViewportHeight));
		implementationProperties.put(PROPERTY_MAX_VIEWPORT_DIMENSION, new Integer(Math.min(maxViewportWidth,
				maxViewportHeight)));
		implementationProperties.put(PROPERTY_MAX_TEXTURE_DIMENSION, new Integer(maxTextureSize));
		implementationProperties.put(PROPERTY_MAX_SPRITE_CROP_DIMENSION, new Integer(maxTextureSize));
		implementationProperties.put(PROPERTY_MAX_TRANSFORM_PER_VERTEX, new Integer(4));
		implementationProperties.put(PROPERTY_MAX_TEXTURE_UNITS, new Integer(maxTextureUnits));
	}

	protected void finalize() {
		// Release textures
		for (int i = 0; i < Image2D.recycledTextures.size(); i++) {
			((Image2D) Image2D.recycledTextures.elementAt(i)).releaseTexture(gl);
			Image2D.recycledTextures.remove(i);
		}

		// Destroy EGL
		egl.eglMakeCurrent(eglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
		if (eglWindowSurface != null)
			egl.eglDestroySurface(eglDisplay, eglWindowSurface);
		egl.eglDestroyContext(eglDisplay, eglContext);
		egl.eglTerminate(eglDisplay);
	}

	public void bindTarget(Object target) {
		bindTarget(target, true, 0);
	}

	public void bindTarget(Object target, boolean depthBuffer, int hints) {
		if (target == null)
			throw new NullPointerException("Rendering target must not be null");

		targetBound = true;

		// Create a new window surface if the target changes (i.e, for MIDP2, the target Canvas changed)
		if (target != renderTarget) {
			renderTarget = target;

			if (this.eglWindowSurface != null) {
				EGL_ASSERT(egl.eglMakeCurrent(eglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT));
				egl.eglDestroySurface(this.eglDisplay, this.eglWindowSurface);
			}

			if (target instanceof Graphics) {
				// Create an offscreen surface
				Graphics graphics = (Graphics) target;
				width = graphics.getCanvas().getWidth();
				height = graphics.getCanvas().getHeight();
			} else if (target instanceof Image2D) {
				Image2D image = (Image2D) target;
				width = image.getWidth();
				height = image.getHeight();
			}

			int[] s_surfaceAttribs = {
					EGL10.EGL_WIDTH, width,
					EGL10.EGL_HEIGHT, height,
					EGL10.EGL_NONE};
			this.eglWindowSurface = egl.eglCreatePbufferSurface(eglDisplay, eglConfig, s_surfaceAttribs);
			EGL_ASSERT(this.eglWindowSurface != EGL10.EGL_NO_SURFACE);
			EGL_ASSERT(egl.eglMakeCurrent(eglDisplay, eglWindowSurface, eglWindowSurface, eglContext));
			this.gl = (GL10) eglContext.getGL();

			// Set default clipping rectangle
			clipX0 = 0;
			clipY0 = 0;
			clipX1 = width;
			clipY1 = height;
			gl.glEnable(GL10.GL_SCISSOR_TEST);
			gl.glPixelStorei(GL10.GL_UNPACK_ALIGNMENT, 1);
			gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		}

		gl.glDepthMask(true);
		gl.glColorMask(true, true, true, !overwrite);
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		gl.glClearDepthf(1.0f);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		// Depth buffer
		depthBufferEnabled = depthBuffer;
		if (depthBuffer)
			gl.glEnable(GL10.GL_DEPTH_TEST);
		else
			gl.glDisable(GL10.GL_DEPTH_TEST);
		this.hints = hints;

		// Multisapling
		if ((hints & ANTIALIAS) != 0)
			gl.glEnable(GL10.GL_MULTISAMPLE);
		else
			gl.glDisable(GL10.GL_MULTISAMPLE);

		// Overwriting
		overwrite = ((hints & OVERWRITE) != 0);
		setViewport(0, 0, width, height);
	}

	private static void EGL_ASSERT(boolean val) {
		if (!val) {
			System.out.println("EGL_ASSERT failed!");
			throw new IllegalStateException();
		}
	}

	public Object getTarget() {
		return this.renderTarget;
	}

	public void releaseTarget() {
		int b[] = new int[width * height];
		int bt[] = new int[width * height];
		IntBuffer ib = IntBuffer.wrap(b);
		ib.position(0);

		gl.glFinish();
		gl.glReadPixels(0, 0, width, height, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, ib);

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				int pix = b[i * width + j];
				int pb = (pix >>> 16) & 0xff;
				int pr = (pix << 16) & 0x00ff0000;
				int pix1 = (pix & 0xff00ff00) | pr | pb | (overwrite ? 0xff000000 : (((pix & 0xff000000) == 0) ? 0 : 0xff000000));
				bt[(height - i - 1) * width + j] = pix1;
			}
		}

		if (renderTarget instanceof Graphics)
			((Graphics) renderTarget).drawRGB(bt, 0, width, 0, 0, width, height, true);
		else if (renderTarget instanceof Image2D) {
			ByteBuffer bb = ((Image2D) renderTarget).getPixels();
			if (bb == null)
				bb = ByteBuffer.allocateDirect(width * height * 4);
			bb.position(0);
			for (int i = 0; i < height; i++)
				for (int j = 0; j < width; j++) {
					int pix = bt[i * width + j];
					bb.put((byte) (pix >>> 24));
					bb.put((byte) ((pix >>> 16) & 0xFF));
					bb.put((byte) ((pix >>> 8) & 0xFF));
					bb.put((byte) (pix & 0xFF));
				}
		}

		for (int i = 0; i < Image2D.recycledTextures.size(); i++) {
			((Image2D) Image2D.recycledTextures.elementAt(i)).releaseTexture(gl);
			Image2D.recycledTextures.remove(i);
		}

		targetBound = false;
	}

	public void clear(Background background) {
		/*if (!targetBound) {
			throw new IllegalStateException("Graphics3D does not have a rendering target");
		}*/

		if (background != null)
			background.setupGL(gl);
		else {
			defaultBackground.setupGL(gl);
		}
	}

	public int addLight(Light light, Transform transform) {
		if (light == null)
			throw new NullPointerException("Light must not be null");

		lights.addElement(light);

		// Use identity transform if the given transform is null
		Transform t = new Transform();
		if (transform != null) {
			t.set(transform);
		}
		lightTransforms.addElement(t);

		int index = lights.size() - 1;

		// limit the number of lights
		if (index < maxLights) {
			lightFlags[index] = true;
		}

		lightHasChanged = true;
		return index;
	}

	public void setLight(int index, Light light, Transform transform) {
		lights.setElementAt(light, index);
		lightTransforms.setElementAt(transform, index);
		if (index < maxLights) {
			lightFlags[index] = true;
		}
		lightHasChanged = true;
	}

	public void resetLights() {
		lights.removeAllElements();
		lightTransforms.removeAllElements();
		for (int i = 0; i < maxLights; i++) {
			lightFlags[i] = false;
		}
		lightHasChanged = true;
	}

	public int getLightCount() {
		return lights.size();
	}

	public Light getLight(int index, Transform transform) {
		if (transform != null) {
			transform.set((Transform) lightTransforms.elementAt(index));
		}
		return (Light) lights.elementAt(index);
	}

	public int getHints() {
		return hints;
	}

	public boolean isDepthBufferEnabled() {
		return depthBufferEnabled;
	}

	public void setViewport(int x, int y, int width, int height) {

		if ((width <= 0) || (height <= 0) || (width > maxViewportWidth) || (height > maxViewportHeight)) {
			throw new IllegalArgumentException("Viewport coordinates are out of the allowed range");
		}

		this.viewportX = x;
		this.viewportY = y;
		this.viewportWidth = width;
		this.viewportHeight = height;

		int sx0 = Math.max(viewportX, clipX0);
		int sy0 = Math.max(viewportY, clipY0);
		int sx1 = Math.min(viewportX + viewportWidth, clipX1);
		int sy1 = Math.min(viewportY + viewportHeight, clipY1);

		scissorX = sx0;
		scissorY = sy0;

		if (sx0 < sx1 && sy0 < sy1) {
			scissorWidth = sx1 - sx0;
			scissorHeight = sy1 - sy0;
		} else
			scissorWidth = scissorHeight = 0;

		gl.glViewport(x, y, width, height);
	}

	public int getViewportX() {
		return this.viewportX;
	}

	public int getViewportY() {
		return this.viewportY;
	}

	public int getViewportWidth() {
		return this.viewportWidth;
	}

	public int getViewportHeight() {
		return this.viewportHeight;
	}

	public void setDepthRange(float near, float far) {
		if ((near < 0) || (near > 1) || (far < 0) || (far > 1)) {
			throw new IllegalArgumentException("Bad depth range");
		}

		if ((depthRangeNear != near) || (depthRangeFar != far)) {
			depthRangeNear = near;
			depthRangeFar = far;
			depthRangeHasChanged = true;
		}
	}

	public float getDepthRangeNear() {
		return depthRangeNear;
	}

	public float getDepthRangeFar() {
		return depthRangeFar;
	}

	public static Hashtable getProperties() {
		// Force initialization of Graphics3D in order to populate implementationProperties
		if (instance == null) {
			getInstance();
		}
		return implementationProperties;
	}

	public void setCamera(Camera camera, Transform transform) {
		this.camera = camera;

		Transform t = new Transform();
		if (transform != null) {
			t.set(transform);
		}
		t.mtx.invertMatrix();
		this.cameraTransform = t;
		cameraHasChanged = true;
	}

	public Camera getCamera(Transform transform) {
		if (transform != null)
			transform.set(this.cameraTransform);
		return camera;
	}

	public void render(Node node, Transform transform) {
		/*if (!targetBound)
			throw new IllegalStateException("Graphics3D does not have a rendering target");*/
		if (camera == null)
			throw new IllegalStateException("Graphics3D does not have a current camera");

		// If the given transform is null, use the identity matrix
		if (transform == null) {
			transform = new Transform();
		}

		// Apply Graphics3D settings to the OpenGL pipeline
		initRender();

		if ((node instanceof Mesh) || (node instanceof Sprite3D) || (node instanceof Group)) {
			renderNode(node, transform);
		} else {
			throw new IllegalArgumentException("Node is not a Sprite3D, Mesh, or Group");
		}
	}

	private void initRender() {
		if (cameraHasChanged) {
			Transform t = new Transform();

			gl.glMatrixMode(GL10.GL_PROJECTION);
			camera.getProjection(t);
			t.setGL(gl);

			gl.glMatrixMode(GL10.GL_MODELVIEW);
			t.set(cameraTransform);
			//t.mtx.invertMatrix();
			t.setGL(gl);

			gl.glViewport(viewportX, viewportY, viewportWidth, viewportHeight);
			gl.glScissor(scissorX, scissorY, scissorWidth, scissorHeight);
			cameraHasChanged = false;
		}

		if (lightHasChanged) {
			for (int i = 0; i < maxLights; i++) {
				if (lightFlags[i]) {
					Light light = (Light) lights.elementAt(i);
					Transform transform = (Transform) lightTransforms.elementAt(i);
					gl.glEnable(GL10.GL_LIGHT0 + i);
					gl.glPushMatrix();
					transform.multGL(gl);
					light.setupGL(gl, GL10.GL_LIGHT0 + i);
					gl.glPopMatrix();
				} else {
					gl.glDisable(GL10.GL_LIGHT0 + i);
				}
			}
			lightHasChanged = false;
		}

		if (depthRangeHasChanged) {
			gl.glDepthRangef(depthRangeNear, depthRangeFar);
			depthRangeHasChanged = false;
		}
	}

	public void render(VertexBuffer vertices, IndexBuffer triangles, Appearance appearance, Transform transform) {
		if (vertices == null)
			throw new NullPointerException("vertices == null");
		if (triangles == null)
			throw new NullPointerException("triangles == null");
		if (appearance == null)
			throw new NullPointerException("appearance == null");
		/*if (!targetBound)
			throw new IllegalStateException("Graphics3D does not have a rendering target");*/
		if (camera == null)
			throw new IllegalStateException("Graphics3D does not have a current camera");
		// TODO Check if vertices or triangles violates the constraints defined in VertexBuffer or IndexBuffer

		// If the given transform is null, use the identity matrix
		if (transform == null) {
			transform = new Transform();
		}

		// Apply Graphics3D settings to the OpenGL pipeline
		initRender();

		// Appearance
		appearance.setupGL(gl);

		// Vertices
		float[] scaleBias = new float[4];
		VertexArray positions = vertices.getPositions(scaleBias);
		if (positions.getComponentType() == 1) {
			ByteBuffer pos = (ByteBuffer) positions.getBuffer();
			pos.position(0);
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glVertexPointer(positions.getComponentCount(), GL10.GL_BYTE, 4, pos);
		} else {
			ShortBuffer pos = (ShortBuffer) positions.getBuffer();
			pos.position(0);
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glVertexPointer(positions.getComponentCount(), GL10.GL_SHORT, positions.stride, pos);
		}

		// Normals
		VertexArray normals = vertices.getNormals();
		if (normals != null) {
			gl.glEnable(GL10.GL_NORMALIZE);
			if (normals.getComponentType() == 1) {
				ByteBuffer norm = (ByteBuffer) normals.getBuffer();
				norm.position(0);
				gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
				gl.glNormalPointer(GL10.GL_BYTE, 4, norm);
			} else {
				ShortBuffer norm = (ShortBuffer) normals.getBuffer();
				norm.position(0);
				gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
				gl.glNormalPointer(GL10.GL_SHORT, normals.stride, norm);
			}
		} else {
			gl.glDisable(GL10.GL_NORMALIZE);
			gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
		}

		// Colors
		VertexArray colors = vertices.getColors();
		if (colors != null) {
			Buffer buffer = colors.getBuffer();
			buffer.position(0);
			gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
			gl.glColorPointer(4, GL10.GL_UNSIGNED_BYTE, 4, buffer);
		} else {
			// Use default color as we don't have color per vertex
			Color color = new Color(vertices.getDefaultColor());
			float[] colorArray = color.toRGBAArray();
			gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
			gl.glColor4f(colorArray[0], colorArray[1], colorArray[2], colorArray[3]);
		}

		// Textures
		for (int i = 0; i < maxTextureUnits; ++i) {
			float[] texScaleBias = new float[4];
			VertexArray texcoords = vertices.getTexCoords(i, texScaleBias);
			gl.glClientActiveTexture(GL10.GL_TEXTURE0 + i);
			gl.glActiveTexture(GL10.GL_TEXTURE0 + i);
			if ((texcoords != null) && (appearance.getTexture(i) != null)) {
				// Enable the texture coordinate array
				gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

				// Activate the texture unit
				gl.glEnable(GL10.GL_TEXTURE_2D);
				appearance.getTexture(i).setupGL(gl, texScaleBias);

				// Set the texture coordinates
				if (texcoords.getComponentType() == 1) {
					ByteBuffer buffer = (ByteBuffer) texcoords.getBuffer();
					buffer.position(0);
					gl.glTexCoordPointer(texcoords.getComponentCount(), GL10.GL_BYTE, 4, buffer);
				} else {
					ShortBuffer buffer = (ShortBuffer) texcoords.getBuffer();
					buffer.position(0);
					gl.glTexCoordPointer(texcoords.getComponentCount(), GL10.GL_SHORT, texcoords.stride, buffer);
				}

			} else {
				gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
				gl.glDisable(GL10.GL_TEXTURE_2D);
			}
		}

		// Scene
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glPushMatrix();
		transform.multGL(gl);

		gl.glTranslatef(scaleBias[1], scaleBias[2], scaleBias[3]);
		gl.glScalef(scaleBias[0], scaleBias[0], scaleBias[0]);

		// Draw
		ShortBuffer indices = triangles.getBuffer();
		indices.position(0);
		gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, triangles.getIndexCount(), GL10.GL_UNSIGNED_SHORT, indices);

		gl.glPopMatrix();

		// Release textures
		for (int i = 0; i < maxTextureUnits; i++) {
			if (appearance.getTexture(i) != null) {
				gl.glActiveTexture(GL10.GL_TEXTURE0 + i);
				gl.glClientActiveTexture(GL10.GL_TEXTURE0 + i);
				gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);
			}
		}
	}

	public void render(VertexBuffer vertices, IndexBuffer triangles, Appearance appearance, Transform transform,
					   int scope) {
		// TODO: check scope
		render(vertices, triangles, appearance, transform);
	}

	public void render(World world) {

		/*if (!targetBound) {
			throw new IllegalStateException("Graphics3D does not have a rendering target");
		}*/

		clear(world.getBackground());

		Transform t = new Transform();

		// Setup camera
		Camera c = world.getActiveCamera();
		if (c == null)
			throw new IllegalStateException("World has no active camera.");
		if (!c.getTransformTo(world, t))
			throw new IllegalStateException("Camera is not in world.");

		// Camera
		setCamera(c, t);
		initRender();
		resetLights();
		populateLights(world, world);
		initRender();

		// Begin traversal of scene graph
		renderDescendants(world, world, new Transform());
	}

	private void populateLights(World world, Object3D obj) {
		int numReferences = obj.getReferences(null);
		if (numReferences > 0) {
			Object3D[] objArray = new Object3D[numReferences];
			obj.getReferences(objArray);
			for (int i = 0; i < numReferences; ++i) {
				if (objArray[i] instanceof Light) {
					Transform t = new Transform();
					Light light = (Light) objArray[i];
					if (light.isRenderingEnabled() && light.getTransformTo(world, t))
						addLight(light, t);
				}
				populateLights(world, objArray[i]);
			}
		}
	}

	private void renderDescendants(Group group, Object3D caller, Transform transform) {
		Node child = group.firstChild;
		if (child != null) {
			do {
				if (child != caller) {
					Transform t = new Transform();
					child.getCompositeTransform(t);
					t.mtx.preMultiplyMatrix(transform.mtx);
					renderNode(child, t);
				}
				child = child.right;
			} while (child != group.firstChild);
		}
	}

	private void drawMesh(VertexBuffer vb, IndexBuffer ib, Appearance app, Transform modelTransform) {
		initRender();
		if (modelTransform != null) {
			float transform[] = new float[16];
			modelTransform.mtx.getMatrixColumns(transform);
			gl.glPushMatrix();
			FloatBuffer tr = ByteBuffer.allocateDirect(transform.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
			tr.put(transform).position(0);
			gl.glMultMatrixf(tr);
		}
		app.setupGL(gl);

		VertexArray colors = vb.getColors();
		if (colors != null) {
			gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
			Buffer buffer = colors.getBuffer();
			buffer.position(0);
			// Force number of color components to 4 (i.e. don't use colors.getComponentCount())
			gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
			gl.glColorPointer(4, GL10.GL_UNSIGNED_BYTE, 4, buffer);
		} else {
			gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
			// Use default color as we don't have color per vertex
			Color color = new Color(vb.getDefaultColor());
			float[] colorArray = color.toRGBAArray();
			gl.glColor4f(colorArray[0], colorArray[1], colorArray[2], colorArray[3]);
		}

		VertexArray normals = vb.getNormals();
		if (normals != null) {
/*			gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
			FloatBuffer norm = normals.getFloatBuffer();
			norm.position(0);
			gl.glEnable(GL10.GL_NORMALIZE);
			gl.glNormalPointer(GL10.GL_FLOAT, 0, norm);*/

			//FloatBuffer norm = normals.getFloatBuffer();
			gl.glEnable(GL10.GL_NORMALIZE);
			if (normals.getComponentType() == 1) {
				ByteBuffer norm = (ByteBuffer) normals.getBuffer();
				norm.position(0);
				gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
				gl.glNormalPointer(GL10.GL_BYTE, 4, norm);
			} else {
				ShortBuffer norm = (ShortBuffer) normals.getBuffer();
				norm.position(0);
				gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
				gl.glNormalPointer(GL10.GL_SHORT, normals.stride, norm);
			}
			/*norm.position(0);
			gl.glEnable(GL10.GL_NORMALIZE);
			gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
			gl.glNormalPointer(GL10.GL_FLOAT, 0, norm);*/

		} else {
			//gl.glDisable(GL10.GL_NORMALIZE);
			gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
		}

		// Vertices
		float[] scaleBias = new float[4];
		VertexArray vertices = vb.getPositions(scaleBias);
		if (vertices != null) {
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			//FloatBuffer pos = vertices.getFloatBuffer();
			if (vertices.getComponentType() == 1) {
				ByteBuffer buffer = (ByteBuffer) vertices.getBuffer();
				buffer.position(0);
				gl.glVertexPointer(vertices.getComponentCount(), GL10.GL_BYTE, 4, buffer);
			} else {
				ShortBuffer buffer = (ShortBuffer) vertices.getBuffer();
				buffer.position(0);
				gl.glVertexPointer(vertices.getComponentCount(), GL10.GL_SHORT, vertices.stride, buffer);
			}
			//pos.position(0);
			//gl.glVertexPointer(vertices.getComponentCount(), GL10.GL_FLOAT, 0, pos);
		} else {
			gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		}

		// Textures
		for (int i = 0; i < maxTextureUnits; i++) {
			float[] texScaleBias = new float[4];
			VertexArray texcoords = vb.getTexCoords(i, texScaleBias);
			gl.glClientActiveTexture(GL10.GL_TEXTURE0 + i);
			gl.glActiveTexture(GL10.GL_TEXTURE0 + i);
			if ((texcoords != null) && (app.getTexture(i) != null)) {
				// Enable the texture coordinate array
				gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
				//FloatBuffer tex = texcoords.getFloatBuffer();
				//tex.position(0);

				// Activate the texture unit
				//appearance.getTexture(i).setupGL(gl, texScaleBias);

				// Set the texture coordinates
				if (texcoords.getComponentType() == 1) {
					ByteBuffer buffer = (ByteBuffer) texcoords.getBuffer();
					buffer.position(0);
					gl.glTexCoordPointer(texcoords.getComponentCount(), GL10.GL_BYTE, 4, buffer);
				} else {
					ShortBuffer buffer = (ShortBuffer) texcoords.getBuffer();
					buffer.position(0);
					gl.glTexCoordPointer(texcoords.getComponentCount(), GL10.GL_SHORT, texcoords.stride, buffer);
				}
			} else {
				gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			}
		}

		gl.glMatrixMode(GL10.GL_TEXTURE);
		for (int i = 0; i < maxTextureUnits; i++) {
			float[] texScaleBias = new float[4];
			VertexArray texcoords = vb.getTexCoords(i, texScaleBias);
			if (texcoords != null && app.getTexture(i) != null) {
				//appearance.getTexture(i).setupGL(gl, texScaleBias);
				gl.glActiveTexture(GL10.GL_TEXTURE0 + i);
				gl.glTranslatef(texScaleBias[1], texScaleBias[2], texScaleBias[3]);
				gl.glScalef(texScaleBias[0], texScaleBias[0], texScaleBias[0]);
			}
		}
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		if (vertices != null) {
			gl.glTranslatef(scaleBias[1], scaleBias[2], scaleBias[3]);
			gl.glScalef(scaleBias[0], scaleBias[0], scaleBias[0]);
		}

		// Draw
		ShortBuffer indices = ib.getBuffer();
		indices.position(0);
		gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, ib.getIndexCount(), GL10.GL_UNSIGNED_SHORT, indices);

		if (modelTransform != null)
			gl.glPopMatrix();
	}

	private void renderNode(Node node, Transform transform) {
		if (node instanceof Mesh) {
			Mesh mesh = (Mesh) node;
			int subMeshes = mesh.getSubmeshCount();
			VertexBuffer vertices = mesh.getVertexBuffer();
			for (int i = 0; i < subMeshes; i++) {
				if (mesh.getAppearance(i) != null) {
					/*drawMesh*/
					render(vertices, mesh.getIndexBuffer(i), mesh.getAppearance(i), transform);
				}
			}
		} else if (node instanceof Sprite3D) {
			Sprite3D sprite = (Sprite3D) node;
			if (sprite.getAppearance() != null && sprite.getImage() != null && sprite.getCropWidth() != 0 && sprite.getCropHeight() != 0) {
				gl.glMatrixMode(GL10.GL_MODELVIEW);
				gl.glPushMatrix();
				transform.multGL(gl);

				sprite.render(gl, this);

				gl.glPopMatrix();
			}
		} else if (node instanceof Group) {
			renderDescendants((Group) node, node, transform);
		}

	}

	int getTextureUnitCount() {
		return maxTextureUnits;
	}

	static int getMaxTextureSize() {
		return maxTextureSize;
	}

	void disableTextureUnits() {
		for (int i = 0; i < maxTextureUnits; i++) {
			gl.glActiveTexture(GL10.GL_TEXTURE0 + i);
			gl.glDisable(GL10.GL_TEXTURE_2D);
		}
	}

}
