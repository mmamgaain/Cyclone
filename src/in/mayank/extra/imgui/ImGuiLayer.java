package in.mayank.extra.imgui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Objects;

import org.joml.Vector2f;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImGuiStyle;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;

public class ImGuiLayer {
	
	private long windowPtr;
	
	// LWJGL3 window backend
	private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
	
	//LWJGL3 renderer (SHOULD BE INITIALISED)
	private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
	
	// User UI to render
    //private final ExampleUi exampleUi = new ExampleUi();
	
	public ImGuiLayer(long windowPtr) {
		this.windowPtr = windowPtr;
	}
	
	// Initialize Dear ImGui.
    public void initImGui() {
        // IMPORTANT!!
        // This line is critical for Dear ImGui to work.
        ImGui.createContext();
        
        // ------------------------------------------------------------
        // Initialize ImGuiIO config
        final ImGuiIO io = ImGui.getIO();
        
        io.setIniFilename("res/fonts/imgui.ini"); // We don't want to save .ini file but if we do we can simply pass a string as a filename
        						 // This is useful for saving the states and position of all ImGui components
        io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard);  // Enable Keyboard Controls
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable);      // Enable Docking
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);    // Enable Multi-Viewport / Platform Windows
        io.setConfigViewportsNoTaskBarIcon(true);
        
        // ------------------------------------------------------------
        // Fonts configuration
        // Read: https://raw.githubusercontent.com/ocornut/imgui/master/docs/FONTS.txt
        
        /*final ImFontAtlas fontAtlas = io.getFonts();
        final ImFontConfig fontConfig = new ImFontConfig(); // Natively allocated object, should be explicitly destroyed
        
        // Glyphs could be added per-font as well as per config used globally like here
        fontConfig.setGlyphRanges(fontAtlas.getGlyphRangesCyrillic());
        
        // Add a default font, which is 'ProggyClean.ttf, 13px'
        fontAtlas.addFontDefault();
        
        // Fonts merge example
        fontConfig.setMergeMode(true); // When enabled, all fonts added with this config would be merged with the previously added font
        fontConfig.setPixelSnapH(true);
        
        fontAtlas.addFontFromMemoryTTF(loadFromResources("basis33.ttf"), 16, fontConfig);
        
        fontConfig.setMergeMode(false);
        fontConfig.setPixelSnapH(false);
        
        // Fonts from file/memory example
        // We can add new fonts from the file system
        fontAtlas.addFontFromFileTTF("src/test/resources/Righteous-Regular.ttf", 14, fontConfig);
        fontAtlas.addFontFromFileTTF("src/test/resources/Righteous-Regular.ttf", 16, fontConfig);
        
        // Or directly from the memory
        fontConfig.setName("Roboto-Regular.ttf, 14px"); // This name will be displayed in Style Editor
        fontAtlas.addFontFromMemoryTTF(loadFromResources("Roboto-Regular.ttf"), 14, fontConfig);
        fontConfig.setName("Roboto-Regular.ttf, 16px"); // We can apply a new config value every time we add a new font
        fontAtlas.addFontFromMemoryTTF(loadFromResources("Roboto-Regular.ttf"), 16, fontConfig);
        
        fontConfig.destroy(); // After all fonts were added we don't need this config anymore*/
        
        // When viewports are enabled we tweak WindowRounding/WindowBg so platform windows can look identical to regular ones.
        if (io.hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final ImGuiStyle style = ImGui.getStyle();
            style.setWindowRounding(0.0F);
            style.setColor(ImGuiCol.WindowBg, ImGui.getColorU32(ImGuiCol.WindowBg, 1));
        }
        
        // Method initializes GLFW backend.
        // This method SHOULD be called after you've setup GLFW.
        // ImGui context should be created as well.
        imGuiGlfw.init(windowPtr, true);
        // Method initializes LWJGL3 renderer.
        // This method SHOULD be called after you've initialized your ImGui configuration (fonts and so on).
        // ImGui context should be created as well.
        imGuiGl3.init("#version 410 core");
    }
    
    public void startFrame() { imGuiGlfw.newFrame(); ImGui.newFrame(); }
    
    public void endFrame() {
    	//try { exampleUi.render(); } catch(Exception e) { e.printStackTrace(); }
    	ImGui.render();
    	
    	// After Dear ImGui prepared a draw data, we use it in the LWJGL3 renderer.
        // At that moment ImGui will be rendered to the current OpenGL context.
        imGuiGl3.renderDrawData(ImGui.getDrawData());
        
        if(ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
        }
    }
    
    public ImGuiLayer addImage(final int textureID, final float startX, final float startY, final float offsetX, final float offsetY) {
        ImGui.getWindowDrawList().addImage(textureID, startX, startY, startX + offsetX, startY + offsetY);
    	return this;
    }
    
    public float getWindowSizeX() { return ImGui.getWindowSizeX(); }
    
    public float getWindowSizeY() { return ImGui.getWindowSizeY(); }
    
    public Vector2f getWindowSize() { return new Vector2f(getWindowSizeX(), getWindowSizeY()); }
    
    public float getWindowPosX() { return ImGui.getWindowPosX(); }
    
    public float getWindowPosY() { return ImGui.getWindowPosY(); }
    
    public Vector2f getWindowPos() { return new Vector2f(getWindowPosX(), getWindowPosY()); }
    
    private byte[] loadFromResources(final String fileName) {
        try (InputStream is = Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(fileName));
            ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            final byte[] data = new byte[16384];
            int nRead;
            while ((nRead = is.read(data, 0, data.length)) != -1) buffer.write(data, 0, nRead);
            return buffer.toByteArray();
        } catch (IOException e) { throw new UncheckedIOException(e); }
    }
    
    public void dispose() {
    	// You should clean up after yourself in reverse order.
        imGuiGl3.dispose();
        imGuiGlfw.dispose();
        ImGui.destroyContext();
    }
    
}
