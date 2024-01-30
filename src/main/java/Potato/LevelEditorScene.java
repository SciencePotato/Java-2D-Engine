package Potato;

import org.lwjgl.BufferUtils;
import renderer.Shader;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene extends Scene{
    private Shader defaultShader;
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
        this.defaultShader = new Shader("assets/shaders/default.glsl");
        this.defaultShader.compile();

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
        this.defaultShader.use();
        glBindVertexArray(vaoId);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        // Deconstruct
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
        this.defaultShader.detach();
    }
}
