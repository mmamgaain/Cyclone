package in.mayank.extra.utils;

public class WaterFBO {
	
	private FBO refraction, reflection;
	
	public WaterFBO(int reflectWidth, int reflectHeight, int refractWidth, int refractHeight) {
		this(reflectWidth, reflectHeight, refractWidth, refractHeight, 1);
	}
	
	public WaterFBO(int reflectWidth, int reflectHeight, int refractWidth, int refractHeight, int samples) {
		reflection = new FBO(reflectWidth, reflectHeight, FBO.COLOR_ATTACHMENT_TEXTURE, FBO.DEPTH_ATTACHMENT_BUFFER, samples, 0);
		refraction = new FBO(refractWidth, refractHeight, FBO.COLOR_ATTACHMENT_TEXTURE, FBO.DEPTH_ATTACHMENT_TEXTURE, samples, 0);
	}
	
	public void bindReflectionBuffer() {
		reflection.bindFrameBuffer();
	}
	
	public void bindRefractionBuffer() {
		refraction.bindFrameBuffer();
	}
	
	public static void unbindFrameBuffer() {
		FBO.unbindFrameBuffer();
	}
	
	public int getReflectionTexture() {
		return reflection.getColorTexture();
	}
	
	public int getRefractionTexture() {
		return refraction.getColorTexture();
	}
	
	public int getRefractionDepthTexture() {
		return refraction.getDepthTexture();
	}
	
	public void dispose() {
		reflection.dispose();
		refraction.dispose();
	}
	
}
