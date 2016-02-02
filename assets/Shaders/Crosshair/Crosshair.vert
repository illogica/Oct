uniform mat4 g_WorldViewProjectionMatrix;

attribute vec3 inPosition;

#ifdef TEXTURE
    attribute vec2 inTexCoord;
    varying vec2 texCoord;
#endif

void main() {
    gl_Position = g_WorldViewProjectionMatrix * vec4(inPosition, 1.0);
    #ifdef TEXTURE
        texCoord = inTexCoord;
    #endif
}