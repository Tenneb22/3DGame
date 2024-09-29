package de.tenneb22.core.rendering;

import de.tenneb22.core.Camera;
import de.tenneb22.core.ShaderManager;
import de.tenneb22.core.WindowManager;
import de.tenneb22.core.entity.Entity;
import de.tenneb22.core.entity.Model;
import de.tenneb22.core.entity.SceneManager;
import de.tenneb22.core.entity.terrain.Terrain;
import de.tenneb22.core.lighting.DirectionalLight;
import de.tenneb22.core.lighting.PointLight;
import de.tenneb22.core.lighting.SpotLight;
import de.tenneb22.core.utils.Consts;
import de.tenneb22.core.utils.Transformation;
import de.tenneb22.test.Main;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.glViewport;

public class RenderManager {

    private final WindowManager window;
    private EntityRenderer entityRenderer;
    private TerrainRenderer terrainRenderer;

    public RenderManager() {
        window = Main.getWindow();
    }

    public void init() throws Exception {
        entityRenderer = new EntityRenderer();
        terrainRenderer = new TerrainRenderer();
        entityRenderer.init();
        terrainRenderer.init();
    }

    public static void renderLights(PointLight[] pointLights, SpotLight[] spotLights,
                                    DirectionalLight directionalLight, ShaderManager shader) {
        shader.setUniform("ambientLight", Consts.AMBIENT_LIGHT);
        shader.setUniform("specularPower", Consts.SPECULAR_POWER);
        int numLights = spotLights != null ? spotLights.length : 0;
        for(int i = 0; i < numLights; i++) {
            shader.setUniform("spotLights", spotLights[i], i);
        }

        numLights = pointLights != null ? pointLights.length : 0;
        for(int i = 0; i < numLights; i++) {
            shader.setUniform("pointLights", pointLights[i], i);
        }
        shader.setUniform("directionalLight", directionalLight);
    }

    public void render(Camera camera, SceneManager scene) {
        clear();

        if(window.isResized()) {
            glViewport(0,0, window.getWidth(), window.getHeight());
            window.setResize(false);
        }

        entityRenderer.render(camera, scene.getPointLights(), scene.getSpotLights(), scene.getDirectionalLight());
        terrainRenderer.render(camera, scene.getPointLights(), scene.getSpotLights(), scene.getDirectionalLight());
    }

    public void processEntity(Entity entity) {
        List<Entity> entityList = entityRenderer.getEntities().get(entity.getModel());
        if(entityList != null)
            entityList.add(entity);
        else {
            List<Entity> newEntityList = new ArrayList<>();
            newEntityList.add(entity);
            entityRenderer.getEntities().put(entity.getModel(), newEntityList);
        }
    }

    public void processTerrain(Terrain terrain) {
        terrainRenderer.getTerrain().add(terrain);
    }

    public void clear() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    public void cleanup() {
        entityRenderer.cleanup();
        terrainRenderer.cleanup();
    }
}
