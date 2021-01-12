package in.mayank.extra.utils;

import static in.mayank.renderer.Renderer.startFrame;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import in.mayank.extra.core.Core;

public class FBO {
	
	public static final int COLOR_ATTACHMENT_TEXTURE = 0, DEPTH_ATTACHMENT_TEXTURE = 1, COLOR_ATTACHMENT_BUFFER = 2,
							DEPTH_ATTACHMENT_BUFFER  = 3, COLOR_ATTACHMENT_NONE    = 4;
	private static int MAX_TARGETS = -1;
	
	private int id, depthTexture, depthBuffer;
	private final int[] colorTexture, colorBuffer;
	private int width, height, samplesPerPixel, maxTarget;
	private boolean dirty = false;
	
	private FBO reserve = null;
	
	/** Creates the most generic FBO.<br /><br />
	 * It contains:
	 * <ol><li>Color Texture</li>
	 * <li>Depth Renderbuffer</li>
	 * <li>Not multisampled</li>
	 * <li>Single render target</li></ol>
	 * 
	 * @param width The width of the frame buffer.
	 * @param height The height of the frame buffer. */
	public FBO(final int width, final int height) { this(width, height, COLOR_ATTACHMENT_TEXTURE, DEPTH_ATTACHMENT_BUFFER, 1, 0); }
	
	/** Creates a multisampled FBO.<br /><br />
	 * It contains:
	 * <ol><li>Color Texture</li>
	 * <li>Depth Renderbuffer</li>
	 * <li>Multisampled</li>
	 * <li>Single render target</li></ol> */
	public FBO(final int width, final int height, final int samples) { this(width, height, DEPTH_ATTACHMENT_BUFFER, samples, 1); }
	
	/**  */
	public FBO(final int width, final int height, final int depthAttachmentType, final int samples, final int maxTargetIndex) {
		if(MAX_TARGETS == -1) MAX_TARGETS = Math.min(GL11.glGetInteger(GL30.GL_MAX_COLOR_ATTACHMENTS), GL11.glGetInteger(GL20.GL_MAX_DRAW_BUFFERS));
		samplesPerPixel = Math.min(1, samples);
		maxTarget = (int) Maths.clamp(maxTargetIndex, 0, MAX_TARGETS);
		colorTexture = new int[maxTarget + 1];
		colorBuffer = new int[maxTarget + 1];
		createFrameBuffer();
		this.width = width;
		this.height = height;
		if(isMultisampled()) {
			createColorBufferAttachments(maxTarget);
			if(reserve == null) reserve = new FBO(width, height, depthAttachmentType, 1, maxTarget);
		}
		else createColorTextureAttachments(maxTarget);
		
		if(depthAttachmentType == DEPTH_ATTACHMENT_TEXTURE) createDepthTextureAttachment();
		else if(depthAttachmentType == DEPTH_ATTACHMENT_BUFFER) createDepthBufferAttachment();
		
		unbindFrameBuffer();
	}
	
	/**  */
	public FBO(final int width, final int height, final int colorAttachmentType, final int depthAttachmentType, final int samples, final int maxTargetIndex) {
		if(MAX_TARGETS == -1) MAX_TARGETS = (int) Maths.min(GL11.glGetInteger(GL30.GL_MAX_COLOR_ATTACHMENTS), GL11.glGetInteger(GL20.GL_MAX_DRAW_BUFFERS));
		samplesPerPixel = (int) Maths.max(1, samples);
		maxTarget = (int) Maths.clamp(maxTargetIndex, 0, MAX_TARGETS);
		colorTexture = new int[maxTarget + 1];
		colorBuffer = new int[maxTarget + 1];
		createFrameBuffer();
		this.width = width;
		this.height = height;
		if(colorAttachmentType == COLOR_ATTACHMENT_NONE) { GL11.glDrawBuffer(GL11.GL_NONE); maxTarget = 0; }
		else if(isMultisampled() || colorAttachmentType == COLOR_ATTACHMENT_BUFFER) {
			createColorBufferAttachments(maxTarget);
			if(reserve == null) reserve = new FBO(width, height, depthAttachmentType, 1, maxTarget);
		}
		else createColorTextureAttachments(maxTarget);
		
		if(depthAttachmentType == DEPTH_ATTACHMENT_TEXTURE) createDepthTextureAttachment();
		else createDepthBufferAttachment();
		
		unbindFrameBuffer();
	}
	
	private void createFrameBuffer() {
		id = GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, id);
		attachDrawBufferTargets();
	}
	
	private void attachDrawBufferTargets() { for(int i = 0; i <= maxTarget; i++) GL20.glDrawBuffers(GL30.GL_COLOR_ATTACHMENT0 + i); }
	
	/** Binds this frame buffer, changes the viewport and clears
	 * the draw buffer for a fresh render. */
	public void bindFrameBuffer() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, id);
		GL11.glViewport(0, 0, width, height);
		dirty = hasMultipleRenderTargets();
		startFrame();
	}
	
	/**  */
	public static void applyDepth(final float depth) { GL11.glClearDepth(depth); startFrame(); GL11.glClearDepth(1); }
	
	/***/
	public static void scissor(int startX, int startY, int endX, int endY) {
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glScissor(startX, startY, endX, endY);
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}
	
	public static void applyDepth(final float depth, final int xStart, final int yStart, final int xEnd, final int yEnd) {
		GL11.glViewport(xStart, yStart, xEnd, yEnd);
		GL11.glClearDepth(depth);
		startFrame();
		GL11.glClearDepth(1);
	}
	
	public static void unbindFrameBuffer() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		GL11.glViewport(0, 0, Core.getWidth(), Core.getHeight());
	}
	
	private void createColorTextureAttachments(final int maxAttachment) {
		for(int i = 0; i <= maxAttachment; i++) {
			colorTexture[i] = GL11.glGenTextures();
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorTexture[i]);
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA4, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
			GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0 + i, GL11.GL_TEXTURE_2D, colorTexture[i], 0);
		}
	}
	
	private void createDepthTextureAttachment() {
		depthTexture = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTexture);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT32, width, height, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (FloatBuffer) null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, depthTexture, 0);
	}
	
	private void createColorBufferAttachments(final int maxAttachment) {
		for(int i = 0; i <= maxAttachment; i++) {
			colorBuffer[i] = GL30.glGenRenderbuffers();
			GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, colorBuffer[i]);
			if(!isMultisampled()) GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL11.GL_RGBA8, width, height);
			else GL30.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, samplesPerPixel, GL11.GL_RGBA8, width, height);
			GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0 + i, GL30.GL_RENDERBUFFER, colorBuffer[i]);
		}
	}
	
	private void createDepthBufferAttachment() {
		depthBuffer = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, depthBuffer);
		if(!isMultisampled()) GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL14.GL_DEPTH_COMPONENT24, width, height);
		else GL30.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, samplesPerPixel, GL14.GL_DEPTH_COMPONENT24, width, height);
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, depthBuffer);
	}
	
	/***/
	public void resolveToFBO(final FBO fbo) {
		if(isMultisampled()) {
			GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, fbo.id);
			GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, id);
			if(hasMultipleRenderTargets()) {
				for(int i = 0; i <= maxTarget; i++) {
					GL11.glReadBuffer(GL30.GL_COLOR_ATTACHMENT0 + i);
				    GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0 + i);
					GL30.glBlitFramebuffer(0, 0, width, height, 0, 0, fbo.width, fbo.height, GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT, GL11.GL_LINEAR);
				}
			}
			else {
				GL11.glReadBuffer(GL30.GL_COLOR_ATTACHMENT0);
			    GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);
			    GL30.glBlitFramebuffer(0, 0, width, height, 0, 0, fbo.width, fbo.height, GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT, GL11.GL_LINEAR);
			}
			unbindFrameBuffer();
		}
	}
	
	/***/
	public FBO resolveToFBO() {
		if(isMultisampled()) {
			dirty = false;
			GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, reserve.id);
			GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, id);
			for(int i = 0; i < maxTarget + 1; i++) {
				GL11.glReadBuffer(GL30.GL_COLOR_ATTACHMENT0 + i);
			    GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0 + i);
				GL30.glBlitFramebuffer(0, 0, this.width, this.height, 0, 0, reserve.width, reserve.height, GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT, GL11.GL_NEAREST);
			}
			unbindFrameBuffer();
		}
		return reserve;
	}
	
	private int[] resolveToTextures() {
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, reserve.id);
		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, id);
		for(int i = 0; i < maxTarget + 1; i++) {
			GL11.glReadBuffer(GL30.GL_COLOR_ATTACHMENT0 + i);
			GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0 + i);
			GL30.glBlitFramebuffer(0, 0, width, height, 0, 0, reserve.width, reserve.height, GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT, GL11.GL_NEAREST);
		}
		unbindFrameBuffer();
		dirty = false;
		
		return reserve.colorTexture;
	}
	
	private int resolveToTexture(final int attachment) {
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, reserve.id);
		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, id);
		GL11.glReadBuffer(GL30.GL_COLOR_ATTACHMENT0 + attachment);
		GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0 + attachment);
		GL30.glBlitFramebuffer(0, 0, width, height, 0, 0, reserve.width, reserve.height, GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT, GL11.GL_NEAREST);
		unbindFrameBuffer();
		if(!hasMultipleRenderTargets() && attachment == 0) dirty = false;
		return reserve.colorTexture[attachment];
	}
	
	public void resolveToScreen() {
		if(isMultisampled()) {
			GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
			GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, id);
			GL11.glDrawBuffer(GL11.GL_BACK);
			GL11.glReadBuffer(GL30.GL_COLOR_ATTACHMENT0);
			GL30.glBlitFramebuffer(0, 0, width, height, 0, 0, Core.getWidth(), Core.getHeight(), GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST);
			unbindFrameBuffer();
		}
	}
	
	public void resolveToScreen(final int attachment) {
		if(isMultisampled()) {
			GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
			GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, id);
			GL11.glDrawBuffer(GL11.GL_BACK);
			if(hasMultipleRenderTargets()) GL11.glReadBuffer(GL30.GL_COLOR_ATTACHMENT0 + attachment);
			else GL11.glReadBuffer(GL30.GL_COLOR_ATTACHMENT0);
			GL30.glBlitFramebuffer(0, 0, width, height, 0, 0, Core.getWidth(), Core.getHeight(), GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST);
			unbindFrameBuffer();
		}
	}
	
	/** Returns the color texture of the non-multisampled frame buffer.
	 * If this frame buffer is multisampled, the texture at index position zero
	 * (i.e., the first texture position). */
	public int getColorTexture() { return isMultisampled() ? (dirty ? resolveToTexture(0) : reserve.colorTexture[0]) : colorTexture[0]; }
	
	/** Returns the color texture of this frame buffer.
	 * 
	 * @param attachment The index of the color texture attachment of this
	 * FBO that needs to be returned.
	 * @throws IndexOutOfBoundsException If the <code>attachment</code> value
	 * provided is out of the range of accepted values. */
	public int getColorTexture(final int attachment) throws IndexOutOfBoundsException {
		if(!Maths.inRange(attachment, 0, maxTarget)) throw new IndexOutOfBoundsException("The FBO color attachment index " + attachment + " is out of range.");
		return isMultisampled() ? (dirty ? resolveToTexture(attachment) : reserve.colorTexture[attachment]) : colorTexture[attachment];
	}
	
	public int[] getAllColorTextures() { return isMultisampled() ? (dirty ? resolveToTextures() : reserve.colorTexture) : colorTexture; }
	
	public boolean hasColorTexture() { return colorTexture[0] > 0; }
	
	public int getDepthTexture() { return depthTexture; }
	
	public boolean hasDepthTexture() { return depthTexture > 0; }
	
	public int getColorBuffer() { return colorBuffer[0]; }
	
	public int getColorBuffer(final int attachment) { return colorBuffer[attachment]; }
	
	public boolean hasColorBuffer() { return colorBuffer[0] > 0; }
	
	public int getDepthBuffer() { return depthBuffer; }
	
	public boolean hasDepthBuffer() { return depthBuffer > 0; }
	
	public int getWidth() { return width; }
	
	public int getHeight() { return height; }
	
	public boolean isMultisampled() { return samplesPerPixel > 1; }
	
	/** Queries the number of samples that this FBO reserves for every pixel that the dimensions
	 * of this FBO define. A non-multisampled FBO would return a value of 1 (minimum).
	 * 
	 * @return The number of samples per pixel. */
	public int getSamplesPerPixel() { return samplesPerPixel; }
	
	public boolean hasMultipleRenderTargets() { return maxTarget > 0; }
	
	public int getMaxTargetIndex() { return maxTarget + 1; }
	
	public static int getMaxAvailableTargets() { return MAX_TARGETS; }
	
	
	public void dispose() {
		if(hasColorTexture()) GL11.glDeleteTextures(colorTexture);
		else if(hasColorBuffer()) GL30.glDeleteRenderbuffers(colorBuffer);
		if(hasDepthTexture()) GL11.glDeleteTextures(depthTexture);
		else if(hasDepthBuffer()) GL30.glDeleteRenderbuffers(depthBuffer);
		if(reserve != null) reserve.dispose();
		GL30.glDeleteFramebuffers(id);
	}
	
}
