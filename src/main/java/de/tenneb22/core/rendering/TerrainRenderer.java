package de.tenneb22.core.rendering;

import de.tenneb22.core.Camera;
import de.tenneb22.core.ShaderManager;
import de.tenneb22.core.entity.Entity;
import de.tenneb22.core.entity.Model;
import de.tenneb22.core.entity.terrain.Terrain;
import de.tenneb22.core.lighting.DirectionalLight;
import de.tenneb22.core.lighting.PointLight;
import de.tenneb22.core.lighting.SpotLight;
import de.tenneb22.core.utils.Consts;
import de.tenneb22.core.utils.Transformation;
import de.tenneb22.core.utils.Utils;
import de.tenneb22.test.Main;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TerrainRenderer implements IRenderer{

    ShaderManager shader;
    private List<Terrain> terrains;

    public TerrainRenderer() throws Exception {
        terrains = new ArrayList<>();
        shader = new ShaderManager();
    }

    @Override
    public void init() throws Exception {
        shader = new ShaderManager();
        shader.createVertexShader(Utils.loadResource("/shaders/terrain_vertex.vs"));
        shader.createFragmentShader(Utils.loadResource("/shaders/terrain_fragment.fs"));
        shader.link();
        shader.createUniform("backgroundTexture");
        shader.createUniform("redTexture");
        shader.createUniform("greenTexture");
        shader.createUniform("blueTexture");
        shader.createUniform("blendMap");
        shader.createUniform("transformationMatrix");
        shader.createUniform("projectionMatrix");
        shader.createUniform("viewMatrix");
        shader.createUniform("ambientLight");
        shader.createMaterialUniform("material");
        shader.createUniform("specularPower");
        shader.createDirectionalLightUniform("directionalLight");
        shader.createPointLightListUniform("pointLights", Consts.MAX_POINT_LIGHTS);
        shader.createSpotLightListUniform("spotLights", Consts.MAX_SPOT_LIGHTS);
    }

    @Override
    public void render(Camera camera, PointLight[] pointLights, SpotLight[] spotLights, DirectionalLight directionalLight) {
        shader.bind();
        shader.setUniform("projectionMatrix", Main.getWindow().updateProjectionMatrix());
        RenderManager.renderLights(pointLights, spotLights, directionalLight, shader);
        for(Terrain terrain : terrains) {
            bind(terrain.getModel());
            prepare(terrain, camera);
            GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            unbind();
        }
        terrains.clear();
        shader.unbind();
    }

    @Override
    public void bind(Model model) {
        GL30.glBindVertexArray(model.getId());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);

        RenderManager.enableCulling();

        shader.setUniform("backgroundTexture", 0);
        shader.setUniform("redTexture", 1);
        shader.setUniform("greenTexture", 2);
        shader.setUniform("blueTexture", 3);
        shader.setUniform("blendMap", 4);

        shader.setUniform("material", model.getMaterial());

    }

    @Override
    public void unbind() {
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }

    @Override
    public void prepare(Object terrain, Camera camera) {

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, ((Terrain)terrain).getBlendMapTerrain().getBackground().getId());
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, ((Terrain)terrain).getBlendMapTerrain().getRedTexture().getId());
        GL13.glActiveTexture(GL13.GL_TEXTURE2);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, ((Terrain)terrain).getBlendMapTerrain().getGreenTexture().getId());
        GL13.glActiveTexture(GL13.GL_TEXTURE3);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, ((Terrain)terrain).getBlendMapTerrain().getBlueTexture().getId());
        GL13.glActiveTexture(GL13.GL_TEXTURE4);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, ((Terrain)terrain).getBlendMap().getId());

        shader.setUniform("transformationMatrix", Transformation.createTransformationMatrix((Terrain) terrain));
        shader.setUniform("viewMatrix", Transformation.getViewMatrix(camera));
    }

    @Override
    public void cleanup() {
        shader.cleanup();
    }

    public List<Terrain> getTerrain() {
        return terrains;
    }
}
