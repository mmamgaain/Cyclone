package in.mayank.renderer;

import org.lwjgl.opengl.GL11;

import in.mayank.extra.postprocessing.PostProcessing;
import in.mayank.extra.utils.FBO;

/** A renderer that renders either to a texture or to the screen. It's
 * used in the {@link PostProcessing} pipeline. */
public class PostRenderer {
	
	private final FBO fbo;
	
	/** Creates a renderer that will render to the screen. */
	public PostRenderer() { fbo = null; }
	
	/** Creates a renderer that will render to an FBO. After the render
	 * pass, you can get the output by calling the {@link #getOutputTexture()}
	 * method.
	 * 
	 * @param width The width of the FBO created.
	 * @param height The height of the FBO created.
	 * @param colorType Color attachment type of the FBO created.
	 * @param depthType Depth attachment type of the FBO created.
	 * @param targets The maximum index of the number of targets. Default is zero. */
	public PostRenderer(final int width, final int height, final int colorType, final int depthType, final int samples, final int targets) { fbo = new FBO(width, height, colorType, depthType, samples, targets); }
	
	/** Renders the texture bound to the shaders being used. The render target
	 * could be either the default display or the {@link FBO}, depending on
	 * which constructor was used to initialize this object. */
	public void render() { if(fbo != null) fbo.bindFrameBuffer(); GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4); if(fbo != null) FBO.unbindFrameBuffer(); }
	
	/** This method returns the ID of the color texture attachment, if the renderer is set
	 * to render to the FBO. If the renderer created is set to render to
	 * the screen, it returns zero.
	 * 
	 * @param attachment The index of the color texture attachment that has to be returned.
	 * In the case of an FBO with only a single render target, supply an attachment value of
	 * zero. */
	public int getOutputTexture(final int attachment) { return (fbo != null) ? fbo.getColorTexture(attachment) : 0; }
	
	/***/
	public int getDepthTexture() { return fbo != null ? fbo.getDepthTexture() : 0; }
	
	/** Disposes of the resources held by the renderer. */
	public void dispose() { if(fbo != null) fbo.dispose(); }
	
}
