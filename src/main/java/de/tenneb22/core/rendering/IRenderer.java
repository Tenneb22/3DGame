package de.tenneb22.core.rendering;

import de.tenneb22.core.Camera;
import de.tenneb22.core.entity.Model;
import de.tenneb22.core.lighting.DirectionalLight;
import de.tenneb22.core.lighting.PointLight;
import de.tenneb22.core.lighting.SpotLight;

public interface IRenderer<T> {

    public void init() throws Exception;

    public void render(Camera camera, PointLight[] pointLights, SpotLight[] spotLights,
                       DirectionalLight directionalLight);

    abstract void bind(Model model);

    public void unbind();

    public void prepare(T t, Camera camera);

    public void cleanup();

}
