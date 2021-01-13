package in.mayank.extra.postprocessing;

public class GaussianBlurFilter {
	
	private final HorizontalBlurFilter hblur;
	private final VerticalBlurFilter vblur;
	
	public GaussianBlurFilter(int width, int height, String horizontalVertexFile, String verticalVertexFile, String fragmentFile, int renderTarget) {
		hblur = new HorizontalBlurFilter(width, height, horizontalVertexFile, fragmentFile, 1, 0);
		if(renderTarget == PostProcessing.RENDER_TO_SCREEN) vblur = new VerticalBlurFilter(verticalVertexFile, fragmentFile);
		else if (renderTarget == PostProcessing.RENDER_TO_FBO) vblur = new VerticalBlurFilter(width, height, verticalVertexFile, fragmentFile, 1, 0);
		else {
			vblur = null;
			System.err.println("The Gaussian Blur Filter's render target was selected as an unknown value");
			System.err.println("Valid values are PostProcessing.RENDER_TO_FBO and PostProcessing.RENDER_TO_SCREEN.");
			System.exit(-1);
		}
	}
	
	public int getOutputTexture() { return vblur.getOutputTexture(); }
	
	public void render(final int colorTexture) { hblur.render(colorTexture); vblur.render(hblur.getOutputTexture()); }
	
	public void dispose() { hblur.dispose(); vblur.dispose(); }
	
}
