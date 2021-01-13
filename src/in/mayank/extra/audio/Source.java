package in.mayank.extra.audio;

import org.joml.Vector3f;
import org.lwjgl.openal.AL10;

import in.mayank.extra.utils.Maths;

public class Source {
	
	private final int id;
	
	private final Vector3f pos, vel;
	
	private float rolloffFactor = 1, refDistance = 6, maxDistance = 15, maxVolume = 1, minVolume = 0;
	private static boolean isPlaying = false;
	
	public Source(final int buffer) {
		id = AL10.alGenSources();
		AL10.alSourcef(id, AL10.AL_MAX_GAIN, maxVolume);
		AL10.alSourcef(id, AL10.AL_MIN_GAIN, minVolume);
		AL10.alSourcef(id, AL10.AL_GAIN, 1);
		AL10.alSourcef(id, AL10.AL_PITCH, 1);
		pos = new Vector3f();
		AL10.alSource3f(id, AL10.AL_POSITION, 0, 0, 0);
		vel = new Vector3f();
		AL10.alSource3f(id, AL10.AL_VELOCITY, 0, 0, 0);
		bindSound(buffer);
		loadDistanceModelVariables();
	}
	
	private void loadDistanceModelVariables() {
		AL10.alSourcef(id, AL10.AL_ROLLOFF_FACTOR, rolloffFactor);
		AL10.alSourcef(id, AL10.AL_REFERENCE_DISTANCE, refDistance);
		AL10.alSourcef(id, AL10.AL_MAX_DISTANCE, maxDistance);
	}
	
	public Source setDistanceModelVariables(final float rolloffFactor, final float refDistance, final float maxDistance) {
		this.rolloffFactor = rolloffFactor;
		this.refDistance = refDistance;
		this.maxDistance = maxDistance;
		loadDistanceModelVariables();
		return this;
	}
	
	public float getRolloffFactor() { return rolloffFactor; }
	
	public float getReferenceDistance() { return refDistance; }
	
	public float getMaxDistance() { return maxDistance; }
	
	public Source play() { if(!isPlaying) { AL10.alSourcePlay(id); isPlaying = true; } return this; }
	
	/** Binds the source to a particular sound which will then
	 * be played upon the invokation of the {@link #play()}
	 * method.
	 * <br><br>
	 * <b><u>NOTE</u> :</b> <i>This method is already called in
	 * the constructor. The only reason to call this method would
	 * be to change the buffer already bound to this source without
	 * initializing it again.</i>
	 * 
	 * @param buffer The sound data being represented as an integer
	 * in the way OpenGL is expecting it. It can be received as the
	 * result of the {@link AudioMaster#loadSound(String)} method. */
	public Source bindSound(final int buffer) { AL10.alSourcei(id, AL10.AL_BUFFER, buffer); return this; }
	
	public Source pause() { isPlaying = false; AL10.alSourcePause(id); return this; }
	
	public Source stop() { isPlaying = false; AL10.alSourceStop(id); return this; }
	
	public boolean isPlaying() { return isPlaying; }
	
	public Source setLooping(final boolean loop) { AL10.alSourcei(id, AL10.AL_LOOPING, loop ? AL10.AL_TRUE : AL10.AL_FALSE); return this; }
	
	public boolean isLooping() { return AL10.alGetSourcei(id, AL10.AL_LOOPING) == AL10.AL_TRUE; }
	
	public Source setVolume(final float volume) { AL10.alSourcef(id, AL10.AL_GAIN, (float)Maths.clamp(volume, getMinVolume(), getMaxVolume())); return this; }
	
	/** The max volume is assumed to be 1 and the minimum as 0 as the volume
	 * is set relative to it. */
	public Source setVolumeInRange(final float volume) { AL10.alSourcef(id, AL10.AL_GAIN, (float)Maths.map(Maths.clamp(volume, 0, 1), 0, 1, minVolume, maxVolume)); return this; }
	
	public float getVolume() { return AL10.alGetSourcef(id, AL10.AL_GAIN); }
	
	public Source setMaxVolume(final float max) { maxVolume = max; AL10.alSourcef(id, AL10.AL_MAX_GAIN, maxVolume); return this; }
	
	public Source setMinVolume(final float min) { minVolume = min; AL10.alSourcef(id, AL10.AL_MIN_GAIN, minVolume); return this; }
	
	public float getMinVolume() { return minVolume; }
	
	public float getMaxVolume() { return maxVolume; }
	
	public Source setPitch(final float pitch) { AL10.alSourcef(id, AL10.AL_PITCH, pitch); return this; }
	
	public float getPitch() { return AL10.alGetSourcef(id, AL10.AL_PITCH); }
	
	public Source setPosition(final float x, final float y, final float z) { pos.set(x, y, z); AL10.alSource3f(id, AL10.AL_POSITION, x, y, z); return this; }
	
	public Source setPosition(final Vector3f pos) { this.pos.set(pos); AL10.alSource3f(id, AL10.AL_POSITION, pos.x, pos.y, pos.z); return this; }
	
	public Vector3f getPosition() { return pos; }
	
	public Source changePosition(final float dx, final float dy, final float dz) {
		pos.x += dx;
		pos.y += dy;
		pos.z += dz;
		AL10.alSource3f(id, AL10.AL_POSITION, pos.x, pos.y, pos.z);
		return this;
	}
	
	public Source changePosition(final Vector3f newPos) { pos.add(newPos); AL10.alSource3f(id, AL10.AL_POSITION, pos.x, pos.y, pos.z); return this; }
	
	public Source setVelocity(final float vx, final float vy, final float vz) { vel.set(vx, vy, vz); AL10.alSource3f(id, AL10.AL_VELOCITY, vx, vy, vz); return this; }
	
	public Source changeVelocity(final float dvx, final float dvy, final float dvz) {
		vel.x += dvx;
		vel.y += dvy;
		vel.z += dvz;
		AL10.alSource3f(id, AL10.AL_VELOCITY, vel.x, vel.y, vel.z);
		return this;
	}
	
	public Source changeVelocity(final Vector3f newVel) { vel.add(newVel); AL10.alSource3f(id, AL10.AL_VELOCITY, vel.x, vel.y, vel.z); return this; }
	
	public Source setVelocity(final Vector3f velocity) { vel.set(velocity); AL10.alSource3f(id, AL10.AL_VELOCITY, velocity.x, velocity.y, velocity.z); return this; }
	
	public Vector3f getVelocity() { return vel; }
	
	public void dispose() { AL10.alDeleteSources(id); }
	
}
