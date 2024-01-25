package Potato;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene extends Scene{
    private final String vertexShaderSrc =
            "#version 330 core\n" +
            "\n" +
            "layout(location = 0) in vec3 aPos;\n" +
            "layout(location = 1) in vec4 aColor;\n" +
            "\n" +
            "out vec4 fColor;\n" +
            "\n" +
            "void main() {\n" +
            "    fColor = aColor;\n" +
            "    gl_Position = vec4(aPos, 1.0);\n" +
            "}";
    private final String fragmentShaderSrc =
            "#version 330 core\n" +
            "\n" +
            "in vec4 fColor;\n" +
            "\n" +
            "out vec4 color;\n" +
            "\n" +
            "void main() {\n" +
            "    color = fColor;\n" +
            "}";

    private int vertexId, fragmentId, shaderProgram;
    private int vaoId, vboId, eboId;
    // Position vec3, Color v4 | Normalized coordinate, xyz
    private float[] vertexArray = {
            0.5f, -0.5f, 0.0f,      1.0f, 0.0f, 0.0f, 1.0f,
            -0.5f, 0.5f, 0.0f,      0.0f, 1.0f, 0.0f, 1.0f,
            0.5f, 0.5f, 0.0f,       0.0f, 0.0f, 1.0f, 1.0f,
            -0.5f, -0.5f, 0.0f,     1.0f, 1.0f, 0.0f, 1.0f
    };

    // Counter Clock wise | For Triangles
    private int[] elementArray = {
            2, 1, 0,
            0, 1, 3
    };

    public LevelEditorScene() {
        System.out.println("Inside level editor scene");
    }

    @Override
    public void init() {
        // Load and Compile, pass to GPU
        vertexId = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexId, vertexShaderSrc);
        glCompileShader(vertexId);

        // Error During compilation
        int success = glGetShaderi(vertexId, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(vertexId, GL_INFO_LOG_LENGTH);
            System.out.println("Error: Vertex Shader Compilation Failure");
            System.out.println(glGetShaderInfoLog(vertexId, len));
            System.exit(0);
        }

        // Load and Compile, pass to GPU
        fragmentId = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentId, fragmentShaderSrc);
        glCompileShader(fragmentId);

        // Error During compilation
        success = glGetShaderi(fragmentId, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(fragmentId, GL_INFO_LOG_LENGTH);
            System.out.println("Error: Fragment Shader Compilation Failure");
            System.out.println(glGetShaderInfoLog(fragmentId, len));
            System.exit(0);
        }

        // Link program
        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexId);
        glAttachShader(shaderProgram, fragmentId);
        glLinkProgram(shaderProgram);

        success = glGetProgrami(shaderProgram, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int len = glGetProgrami(shaderProgram, GL_INFO_LOG_LENGTH);
            System.out.println("Error: Linking issue");
            System.out.println(glGetProgramInfoLog(shaderProgram, len));
            System.exit(0);
        }

        // Generating VAO, VBO, EBO
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        // Buffer to OpenGL
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        // VBO initialization
        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // EBO
        eboId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        // VAO
        int positionSize = 3, colorSize = 4, floatSizeBytes = 4;
        int vertexSizeBytes = (positionSize + colorSize) * floatSizeBytes;
        glVertexAttribPointer(0, positionSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionSize * floatSizeBytes);
        glEnableVertexAttribArray(1);
    }

    @Override
    public void update(float dt) {
        // Bind shader program, VAO, Enable Attribute for Shaders
        glUseProgram(shaderProgram);
        glBindVertexArray(vaoId);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        // Deconstruct
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
        glUseProgram(0);
    }
}
