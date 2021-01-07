package in.mayank.extra.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.joml.Matrix4f;

import in.mayank.extra.model.Particle;
import in.mayank.renderer.ParticleRenderer;

public class ParticleMaster {
	
	private static List<Particle> particles = new ArrayList<>();
	
	private static ParticleRenderer renderer;
	
	public static void init(String vertexFile, String fragmentFile, Loader loader, Matrix4f projectionMatrix) {
		renderer = new ParticleRenderer(vertexFile, fragmentFile, loader, projectionMatrix);
	}
	
	public static void addParticle(Particle p) { particles.add(p); }
	
	public static void update() {
		Iterator<Particle> iterator = particles.iterator();
		while(iterator.hasNext()) if(!iterator.next().update()) iterator.remove();
	}
	
	public static void render(Matrix4f view) { renderer.render(particles, view); }
	
	public static void dispose() { renderer.dispose(); particles.clear(); }
	
}
