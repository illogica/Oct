#ifdef TEXTURE
    uniform sampler2D m_Texture;
    varying vec2 texCoord;
#endif

void main() {
    #ifdef TEXTURE
      vec4 texVal = texture2D(m_Texture, texCoord);
      if( (texVal.r < 0.2) && (texVal.g <= 0.2) && (texVal.b <= 0.2) ){
        texVal.a = 0.0;
      }

      gl_FragColor = texVal;
    #endif
}
