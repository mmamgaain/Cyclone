package in.mayank.extra.audio;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;

/**  */
public class AudioMaster {
	
	public static final int DISTANCE_ATTENUATION_MODEL_EXPONENT 		= AL11.AL_EXPONENT_DISTANCE,
							DISTANCE_ATTENUATION_MODEL_EXPONENT_CLAMPED = AL11.AL_EXPONENT_DISTANCE_CLAMPED,
							DISTANCE_ATTENUATION_MODEL_LINEAR 			= AL11.AL_LINEAR_DISTANCE,
							DISTANCE_ATTENUATION_MODEL_LINEAR_CLAMPED 	= AL11.AL_LINEAR_DISTANCE_CLAMPED,
							DISTANCE_ATTENUATION_MODEL_INVERSE 			= AL10.AL_INVERSE_DISTANCE,
							DISTANCE_ATTENUATION_MODEL_INVERSE_CLAMPED 	= AL10.AL_INVERSE_DISTANCE_CLAMPED;
	
	private static List<Integer> buffers = new ArrayList<>();
	private static long device, context;
	
	/** Initializes the OpenAL specific functions. This should be called
	 * before calling anything from this package. */
	public static boolean init() {
		device = ALC10.alcOpenDevice((ByteBuffer)null);
		
		context = ALC10.alcCreateContext(device, (IntBuffer)null);
		
		ALC10.alcMakeContextCurrent(context);
		AL.createCapabilities(ALC.createCapabilities(device));
		
		return AL10.alGetError() != AL10.AL_NO_ERROR ? false : true;
	}
	
	public static void setListenerData(float x, float y, float z, float vx, float vy, float vz) {
		AL10.alListener3f(AL10.AL_POSITION, x, y, z);
		AL10.alListener3f(AL10.AL_VELOCITY, vx, vy, vz);
	}
	
	public static void setDistanceModel(int model) {
		AL10.alDistanceModel(model);
	}
	
	public static int loadSound(String fileName) {
		int buffer = AL10.alGenBuffers();
		buffers.add(buffer);
		WaveData wave = WaveData.create(fileName);
		AL10.alBufferData(buffer, wave.format, wave.data, wave.sampleRate);
		wave.dispose();
		return buffer;
	}
	
	public static void dispose() {
		for(int i = 0; i < buffers.size(); i++)
			AL10.alDeleteBuffers(buffers.get(i));
		ALC10.alcDestroyContext(context);
		ALC10.alcCloseDevice(device);
	}
	
}

/*  */
class WaveData {
	
	final int format, sampleRate, totalBytes;
	final ByteBuffer data;
	
	private final AudioInputStream audioStream;
	private final byte[] dataArray;
	
	private WaveData(AudioInputStream stream) {
		audioStream = stream;
		AudioFormat format = stream.getFormat();
		this.format = getOpenALFormat(format.getChannels(), format.getSampleSizeInBits());
		sampleRate = (int) format.getSampleRate();
		totalBytes = (int) stream.getFrameLength() * format.getFrameSize();
		data = BufferUtils.createByteBuffer(totalBytes);
		dataArray = new byte[totalBytes];
		loadData();
	}
	
	private ByteBuffer loadData() {
		try {
			int bytesRead = audioStream.read(dataArray, 0, totalBytes);
			data.clear();
			data.put(dataArray, 0, bytesRead);
			data.flip();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Couldn't read bytes from audio stream!");
		}
		return data;
	}
	
	public static WaveData create(String fileName) {
		InputStream stream = null;
		try {
			stream = new FileInputStream(new File(fileName));
		} catch (FileNotFoundException e) {
			System.err.println("Couldn't find file at : " + fileName);
			e.printStackTrace();
			return null;
		}
		InputStream bufferedInput = new BufferedInputStream(stream);
		AudioInputStream audioStream = null;
		try {
			audioStream = AudioSystem.getAudioInputStream(bufferedInput);
		} catch (UnsupportedAudioFileException | IOException e) {
			e.printStackTrace();
		}
		WaveData waveStream = new WaveData(audioStream);
		
		return waveStream;
	}
	
	private static int getOpenALFormat(int channels, int bitsPerSample) {
		return channels == 1 ? (bitsPerSample == 8 ? AL10.AL_FORMAT_MONO8 : AL10.AL_FORMAT_MONO16) : (bitsPerSample == 8 ? AL10.AL_FORMAT_STEREO8 : AL10.AL_FORMAT_STEREO16);
	}
	
	protected void dispose() {
		try {
			audioStream.close();
			data.clear();
		} catch (IOException e) {e.printStackTrace();System.err.println("Problem occured while closing the audio stream.");}
	}
}
