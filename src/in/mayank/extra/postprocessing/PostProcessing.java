package in.mayank.extra.postprocessing;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import in.mayank.extra.model.RawModel;
import in.mayank.extra.utils.FBO;
import in.mayank.extra.utils.Loader;

public abstract class PostProcessing {
	
	private static RawModel screen;
	private static FBO fbo = null;
	
	public static final int RENDER_TO_FBO = 1, RENDER_TO_SCREEN = 0;
	
	/** Initializes the Post-Processing pipeline. */
	public static void init(Loader loader, int width, int height, int samplesPerPixel) {
		init(loader, width, height, FBO.COLOR_ATTACHMENT_TEXTURE, FBO.DEPTH_ATTACHMENT_BUFFER, samplesPerPixel, 0);
	}
	
	/** Initializes the Post-Processing pipeline. */
	public static void init(Loader loader, int width, int height, int colorAttachmentType, int depthAttachmentType, int samplesPerPixel) {
		init(loader, width, height, colorAttachmentType, depthAttachmentType, samplesPerPixel, 0);
	}
	
	/**  */
	public static void init(Loader loader, int width, int height, int colorAttachmentType, int depthAttachmentType, int samplesPerPixel, int maxTargetIndex) {
		screen = loader.loadToVAO(new float[]{-1, 1, -1, -1, 1, 1, 1, -1}, 2);
		fbo = new FBO(width, height, colorAttachmentType, depthAttachmentType, samplesPerPixel, maxTargetIndex);
	}
	
	/** Starts the settings of OpenGL for the Post-Processing
	 * render cycle. This method should be called once each frame
	 * right before the entire call(s). */
	public static void start() {
		GL30.glBindVertexArray(screen.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}
	
	/**  */
	public static void bindFrameBuffer() {
		fbo.bindFrameBuffer();
	}
	
	/***/
	public static void unbindFrameBuffer() {
		FBO.unbindFrameBuffer();
	}
	
	/** Reverses the settings of OpenGL done for Post-Processing
	 * render cycle by {@link #start()}. This method should be called
	 * once each frame right after the entire call(s). */
	public static void stop() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}
	
	public static int getColorTexture() {
		return fbo.getColorTexture();
	}
	
	public static int getColorTexture(int attachment) {
		return fbo.getColorTexture(attachment);
	}
	
	public static int getDepthTexture() {
		return fbo.getDepthTexture();
	}
	
	public static void disposeBuffer() {
		if(fbo != null) {
			fbo.dispose();
			fbo = null;
		}
	}
	
	/** Binds a 2D texture to the specified location.
	 * 
	 * @param texture The ID of the texture to be bound.
	 * @param position The index position to which this texture is to be bound. */
	protected void bindTexture(int texture, int position) {
		GL13.glActiveTexture(GL13.GL_TEXTURE0 + position);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
	}
	
	/** Used to render the processed texture onto the quad.
	 * 
	 * @param colorTexture The ID of the texture to be rendered. */
	protected abstract void render(int colorTexture);
	
	/** Returns the id of the texture produced at the culmination of
	 * this post-processing stage. This will only produce a meaningful
	 * result if this stage is set to output to a texture rather than
	 * to the screen.
	 * 
	 * @param attachment The index number of the render target to be
	 * returned as the output target. Used for FBos with multiple render
	 * targets. */
	protected abstract int getOutputTexture(int attachment);
	
	/** Disposes and cleans the assets (like shaders and renderers) of this
	 * stage of the pipeline. */
	protected abstract void dispose();
	
}
