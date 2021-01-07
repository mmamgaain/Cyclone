package in.mayank.extra.postprocessing;

public class BloomFilter {
	
	private ContrastChanger contrast;
	private GaussianBlurFilter blur;
	private CombineFilter combine;
	
	public BloomFilter(int width, int height, String postVertexFile, String contrastFragmentFile, String hblurVertexFile, String vblurVertexFile, String blurFragmentFile, String combineFragmentFile, int samples, int renderTarget) {
		contrast = new ContrastChanger(postVertexFile, contrastFragmentFile, width, height, samples, 1);
		blur = new GaussianBlurFilter(width, height, hblurVertexFile, vblurVertexFile, blurFragmentFile, PostProcessing.RENDER_TO_FBO);
		if(renderTarget == PostProcessing.RENDER_TO_SCREEN) combine = new CombineFilter(postVertexFile, combineFragmentFile);
		else if(renderTarget == PostProcessing.RENDER_TO_FBO) combine = new CombineFilter(width, height, postVertexFile, combineFragmentFile, samples, 0);
		else { 
			System.err.println("The Render Target specified was an incorrect value.");
			System.err.println("The correct value is either : PostProcessing.RENDER_TO_SCREEN or PostProcessing.RENDER_TO_FBO.");
			System.exit(-1);
		}
	}
	
	public void render(int colorTexture) {
		contrast.render(colorTexture);
		blur.render(contrast.getOutputTexture(1));
		combine.render(contrast.getOutputTexture(0), blur.getOutputTexture());
	}
	
	public int getOutputTexture() {
		return getOutputTexture(0);
	}
	
	public int getOutputTexture(int attachment) {
		return combine.getOutputTexture(attachment);
	}
	
	public void dispose() {
		contrast.dispose();
		blur.dispose();
		combine.dispose();
	}
	
}
