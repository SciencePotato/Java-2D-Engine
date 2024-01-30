package renderer;

import org.joml.*;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;

public class Shader {

    private int shaderId, vertexId, fragmentId;
    private String vertexSrc, fragmentSrc, filePath;
    private boolean beingUsed;

    public Shader(String filePath) {
        this.filePath = filePath;
        try {
            String src = new String(Files.readAllBytes(Paths.get(this.filePath)));
            String[] splitSrc = src.split("(#type)( )+([a-zA-Z]+)");

            // Determine Shader Type
            int idx = src.indexOf("#type") + 6;
            int eol = src.indexOf("\r\n", idx);
            String firstPattern = src.substring(idx, eol).trim();

            idx = src.indexOf("#type", eol) + 6;
            eol = src.indexOf("\r\n", idx);
            String secondPattern = src.substring(idx, eol).trim();

            if (firstPattern.equals("vertex")) {
                vertexSrc = splitSrc[1];
            } else if (firstPattern.equals("fragment")) {
                fragmentSrc = splitSrc[1];
            } else {
                throw new IOException("Unexpected token");
            }

            if (secondPattern.equals("vertex")) {
                vertexSrc = splitSrc[2];
            } else if (secondPattern.equals("fragment")) {
                fragmentSrc = splitSrc[2];
            } else {
                throw new IOException("Unexpected token");
            }
        } catch (IOException error) {
            error.printStackTrace();
            System.exit(0);
        }
    }

    public void compile() {
        vertexId = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexId, vertexSrc);
        glCompileShader(vertexId);

        // Error During compilation
        int success = glGetShaderi(vertexId, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(vertexId, GL_INFO_LOG_LENGTH);
            System.out.println("Error: Vertex Shader Compilation Failure, " + filePath);
            System.out.println(glGetShaderInfoLog(vertexId, len));
            System.exit(0);
        }

        // Load and Compile, pass to GPU
        fragmentId = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentId, fragmentSrc);
        glCompileShader(fragmentId);

        // Error During compilation
        success = glGetShaderi(fragmentId, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(fragmentId, GL_INFO_LOG_LENGTH);
            System.out.println("Error: Fragment Shader Compilation Failure, " + filePath);
            System.out.println(glGetShaderInfoLog(fragmentId, len));
            System.exit(0);
        }

        this.link();
    }

    public void link() {
        // Link program
        shaderId = glCreateProgram();
        glAttachShader(shaderId, vertexId);
        glAttachShader(shaderId, fragmentId);
        glLinkProgram(shaderId);

        int success = glGetProgrami(shaderId, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int len = glGetProgrami(shaderId, GL_INFO_LOG_LENGTH);
            System.out.println("Error: Linking issue, " + filePath);
            System.out.println(glGetProgramInfoLog(shaderId, len));
            System.exit(0);
        }
    }

    public void use() {
        if (!beingUsed) {
            glUseProgram(shaderId);
            beingUsed = true;
        }
    }

    public void detach() {
        glUseProgram(0);
        beingUsed = false;
    }

    public void uploadMat4(String varName, Matrix4f mat4) {
        int varLocation = glGetUniformLocation(shaderId, varName);
        // Ensure we're using shader everytime
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
        mat4.get(matBuffer);
        glUniformMatrix4fv(varLocation, false, matBuffer);
    }

    public void uploadMat3(String varName, Matrix3f mat3) {
        int varLocation = glGetUniformLocation(shaderId, varName);
        // Ensure we're using shader everytime
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(9);
        mat3.get(matBuffer);
        glUniformMatrix3fv(varLocation, false, matBuffer);
    }

    public void uploadVec4f(String varName, Vector4f vec) {
        int varLocation = glGetUniformLocation(shaderId, varName);
        use();
        glUniform4f(varLocation, vec.x, vec.y, vec.z, vec.w);
    }

    public void uploadVec3f(String varName, Vector3f vec) {
        int varLocation = glGetUniformLocation(shaderId, varName);
        use();
        glUniform3f(varLocation, vec.x, vec.y, vec.z);
    }

    public void uploadVec3f(String varName, Vector2f vec) {
        int varLocation = glGetUniformLocation(shaderId, varName);
        use();
        glUniform2f(varLocation, vec.x, vec.y);
    }

    public void uploadFloat(String varName, float val) {
        int varLocation = glGetUniformLocation(shaderId, varName);
        use();
        glUniform1f(varLocation, val);
    }

    public void uploadInt(String varName, int val) {
        int varLocation = glGetUniformLocation(shaderId, varName);
        use();
        glUniform1i(varLocation, val);
    }

}
