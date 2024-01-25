package Potato;

// Game Wrapper
public abstract class Scene {
    public Scene() {

    }

    public void init() {
        System.out.println("Scene initialization potato");
    }

    public abstract void update(float dt);
}
