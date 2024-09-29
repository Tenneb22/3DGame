#version 400 core

in vec2 fragTextureCoord;
in vec3 fragNormal;
in vec3 fragPos;

out vec4 fragColor;

struct Material {
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    int hasTexture;
    float reflectance;
};

struct DirectionalLight {
    vec3 color;
    vec3 direction;
    float intensity;
};

uniform sampler2D textureSampler;
uniform vec3 ambientLight;
uniform Material material;
uniform float specularPower;
uniform DirectionalLight directionalLight;

vec4 ambientC;
vec4 diffuseC;
vec4 specularC;

void setupColors(Material material, vec2 textCoord) {
    if(material.hasTexture == 1) {
        ambientC = texture(textureSampler, textCoord);
        diffuseC = ambientC;
        specularC = ambientC;
    } else {
        ambientC = material.ambient;
        diffuseC = material.diffuse;
        specularC = material.specular;
    }
}

vec4 calcLightColor(vec3 light_color, float light_intensity, vec3 position, vec3 to_light_dir, vec3 normal) {
    vec4 diffuseColor = vec4(0,0,0,0);
    vec4 specColor = vec4(0,0,0,0);

    //diffuse Light
    float diffuseFactor = max(dot(normal, to_light_dir), 0.0);
    diffuseColor = diffuseC * vec4(light_color, 1.0) * light_intensity * diffuseFactor;

    //specular Color
    vec3 camera_direction = normalize(-position);
    vec3 from_light_dir = -to_light_dir;
    vec3 reflectedLight = normalize(reflect(from_light_dir, normal));
    float specularFactor = max(dot(camera_direction, reflectedLight), 0.0);
    specularFactor = pow(specularFactor, specularPower);
    specColor = specularC * light_intensity * specularFactor * material.reflectance * vec4(light_color, 1.0);

    return (diffuseColor + specColor);
}

vec4 calcDirectionalLight(DirectionalLight light, vec3 position, vec3 normal) {
    return calcLightColor(light.color, light.intensity, position, normalize(light.direction), normal);
}

void main() {

    setupColors(material, fragTextureCoord);

    vec4 diffuseSpecularComp = calcDirectionalLight(directionalLight, fragPos, fragNormal);

    fragColor = ambientC * vec4(ambientLight, 1) + diffuseSpecularComp;
}