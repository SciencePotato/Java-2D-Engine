package renderer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;

public class Shader {

    private int shaderId, vertexId, fragmentId;
    private String vertexSrc, fragmentSrc, filePath;

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
        glUseProgram(shaderId);
    }

    public void detach() {
        glUseProgram(0);
    }
}
