package in.mayank.extra.postprocessing;

public class BloomFilter {
	
	private final ContrastChanger contrast;
	private final GaussianBlurFilter blur;
	private final CombineFilter combine;
	
	public BloomFilter(final int width, final int height, final String postVertexFile, final String contrastFragmentFile, final String hblurVertexFile, final String vblurVertexFile, final String blurFragmentFile, final String combineFragmentFile, final int samples, final int renderTarget) {
		contrast = new ContrastChanger(postVertexFile, contrastFragmentFile, width, height, samples, 1);
		blur = new GaussianBlurFilter(width, height, hblurVertexFile, vblurVertexFile, blurFragmentFile, PostProcessing.RENDER_TO_FBO);
		if(renderTarget == PostProcessing.RENDER_TO_SCREEN) combine = new CombineFilter(postVertexFile, combineFragmentFile);
		else if(renderTarget == PostProcessing.RENDER_TO_FBO) combine = new CombineFilter(width, height, postVertexFile, combineFragmentFile, samples, 0);
		else {
			combine = null;
			System.err.println("The Render Target specified was an incorrect value.");
			System.err.println("The correct value is either : PostProcessing.RENDER_TO_SCREEN or PostProcessing.RENDER_TO_FBO.");
			System.exit(-1);
		}
	}
	
	public void render(final int colorTexture) {
		contrast.render(colorTexture);
		blur.render(contrast.getOutputTexture(1));
		combine.render(contrast.getOutputTexture(0), blur.getOutputTexture());
	}
	
	public int getOutputTexture() { return getOutputTexture(0); }
	
	public int getOutputTexture(final int attachment) { return combine.getOutputTexture(attachment); }
	
	public void dispose() { contrast.dispose(); blur.dispose(); combine.dispose(); }
	
}
