package engine.shapes;

import java.util.HashMap;

import javax.management.RuntimeErrorException;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import engine.game.Game;
import engine.util.FileUtil;
import engine.util.Matrix4;
import engine.util.Vector3;

/**
 * ShaderProgram Class. Used to load and use Vertex and Fragment shaders easily.
 *
 * @author Sri Harsha Chilakapati
 */
public class ShaderProgram {

	public enum Uniform {
		model(0), color(1), view(2), projection(3), imageInfo(4), spriteNumber(5), zfar(6), size(7);

		private int value;

		Uniform(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static Uniform get(int index) {
			if (index < 0 || index > size.getValue()) {
				return null;
			}
			for (Uniform uni : Uniform.values()) {
				if (uni.getValue() == index) {
					return uni;
				}
			}
			return null;
		}
	}

	protected static final HashMap<String, ShaderProgram> allPrograms;

	static {
		allPrograms = new HashMap<String, ShaderProgram>();
	}

	// ProgramID
	protected int programID;
	// Vertex Shader ID
	protected int vertexShaderID;
	// Fragment Shader ID
	protected int fragmentShaderID;
	// Location of all the uniforms
	protected int[] uniforms;

	public ShaderProgram(String name) {
		this(name, name, name);
	}

	public ShaderProgram(String vertex, String fragment, String programName) {
		programID = GL20.glCreateProgram();

		attachVertexShader("res/shaders/" + vertex + ".vert");
		attachFragmentShader("res/shaders/" + fragment + ".frag");

		link();

		uniforms = new int[Uniform.size.getValue()];
		uniforms[Uniform.model.getValue()] = GL20.glGetUniformLocation(programID, "u_model");
		uniforms[Uniform.color.getValue()] = GL20.glGetUniformLocation(programID, "u_color");
		uniforms[Uniform.view.getValue()] = GL20.glGetUniformLocation(programID, "u_view");
		uniforms[Uniform.projection.getValue()] = GL20.glGetUniformLocation(programID, "u_projection");
		uniforms[Uniform.imageInfo.getValue()] = GL20.glGetUniformLocation(programID, "u_imageInfo");
		uniforms[Uniform.spriteNumber.getValue()] = GL20.glGetUniformLocation(programID, "u_spriteNumber");
		uniforms[Uniform.zfar.getValue()] = GL20.glGetUniformLocation(programID, "u_zfar");

		bind();
		setUniform(Uniform.zfar, 10f);
		setUniform(Uniform.model, Matrix4.createIdentityMatrix());
		setUniform(Uniform.view, Matrix4.createIdentityMatrix());
		setUniform(Uniform.projection, Matrix4.createIdentityMatrix());
		setUniform(Uniform.color, new Vector3(1f));
		setUniform(Uniform.imageInfo, 1f, 1f, 1f, 1f);
		setUniform(Uniform.spriteNumber, -1f);
		unbind();

		/*
		for (int i = 0; i < uniforms.length; i++) {
			int uniform = uniforms[i];
			if (uniform == -1) {
				Uniform uni = Uniform.get(i);
				System.out.println("ShaderProgram \"" + programName + "\" is missing Uniform \"" + uni + "\".");
			}
		}
		*/

		allPrograms.put(programName, this);
	}

	/**
	 * Attach a Fragment Shader to this program.
	 *
	 * @param name
	 *            The file name of the Fragment Shader.
	 */
	public void attachFragmentShader(String name) {
		// Read the source
		String fragmentShaderSource = FileUtil.readFromFile(name);

		// Create the shader and set the source
		fragmentShaderID = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
		GL20.glShaderSource(fragmentShaderID, fragmentShaderSource);

		// Compile the shader
		GL20.glCompileShader(fragmentShaderID);

		// Check for errors
		if (GL20.glGetShaderi(fragmentShaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			System.err.println("Unable to create fragment shader:");

			// Print log
			System.err.println(GL20.glGetShaderInfoLog(fragmentShaderID,
					GL20.glGetShaderi(fragmentShaderID, GL20.GL_INFO_LOG_LENGTH)));

			dispose();
			Game.end();
		}

		// Attach the shader
		GL20.glAttachShader(programID, fragmentShaderID);
	}

	/**
	 * Attach a Vertex Shader to this program.
	 *
	 * @param name
	 *            The file name of the vertex shader.
	 */
	public void attachVertexShader(String name) {
		// Load the source
		String vertexShaderSource = FileUtil.readFromFile(name);

		// Create the shader and set the source
		vertexShaderID = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
		GL20.glShaderSource(vertexShaderID, vertexShaderSource);

		// Compile the shader
		GL20.glCompileShader(vertexShaderID);

		// Check for errors
		if (GL20.glGetShaderi(vertexShaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			System.err.println("Unable to create vertex shader:");

			// Print log
			System.err.println(GL20.glGetShaderInfoLog(vertexShaderID,
					GL20.glGetShaderi(vertexShaderID, GL20.GL_INFO_LOG_LENGTH)));

			dispose();
			Game.end();
		}

		// Attach the shader
		GL20.glAttachShader(programID, vertexShaderID);
	}

	/**
	 * Bind this program to use.
	 */
	public void bind() {
		GL20.glUseProgram(programID);
	}

	/**
	 * Dispose the program and shaders.
	 */
	public void dispose() {
		// Unbind the program
		unbind();

		// Detach the shaders
		GL20.glDetachShader(programID, vertexShaderID);
		GL20.glDetachShader(programID, fragmentShaderID);

		// Delete the shaders
		GL20.glDeleteShader(vertexShaderID);
		GL20.glDeleteShader(fragmentShaderID);

		// Delete the program
		GL20.glDeleteProgram(programID);
	}

	/**
	 * @return The ID of this program.
	 */
	public int getID() {
		return programID;
	}

	/**
	 * Links this program in order to use.
	 */
	public void link() {
		// Link this program
		GL20.glLinkProgram(programID);

		// Check for linking errors
		if (GL20.glGetProgrami(programID, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
			System.err.println("Unable to link shader program");
			dispose();
			Game.end();
		}
	}

	/**
	 * Sets a uniform float variable
	 *
	 * @param name
	 *            The name of the uniform
	 * @param f
	 *            The float value
	 */
	public void setUniform(Uniform name, float f) {
		GL20.glUniform1f(uniforms[name.getValue()], f);
	}

	/**
	 * Sets a uniform matrix variable.
	 *
	 * @param name
	 *            The name of the uniform.
	 * @param value
	 *            The value of the matrix.
	 */
	public void setUniform(Uniform name, Matrix4 value) {
		GL20.glUniformMatrix4(uniforms[name.getValue()], false, value.toFloatBuffer());
	}

	/**
	 * Sets a uniform vector variable
	 *
	 * @param name
	 *            The name of the uniform
	 * @param v
	 *            The value of the vector
	 */
	public void setUniform(Uniform name, Vector3 v) {
		GL20.glUniform3f(uniforms[name.getValue()], v.getX(), v.getY(), v.getZ());
	}

	/**
	 * Sets a uniform Vector4f variable
	 *
	 * @param name
	 *            Name of the uniform
	 * @param f0
	 * @param f1
	 * @param f2
	 * @param f3
	 */
	public void setUniform(Uniform name, float f0, float f1, float f2, float f3) {
		GL20.glUniform4f(uniforms[name.getValue()], f0, f1, f2, f3);
	}

	public static ShaderProgram getProgram(String name) {
		ShaderProgram program = allPrograms.get(name);
		if (program == null) {
			throw new RuntimeErrorException(new Error(), "Could not find program " + name);
		}
		return allPrograms.get(name);
	}

	/**
	 * Unbind the shader program.
	 */
	public static void unbind() {
		GL20.glUseProgram(0);
	}
}
