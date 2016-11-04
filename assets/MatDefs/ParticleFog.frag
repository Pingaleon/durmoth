#ifdef USE_TEXTURE
uniform sampler2D m_Texture;
varying vec4 texCoord;
#endif

varying vec4 color;

uniform vec4 m_fogColor;
varying float z_Pos;

void main(){
    if (color.a <= 0.01)
        discard;

    #ifdef USE_TEXTURE
        #ifdef POINT_SPRITE
            vec2 uv = mix(texCoord.xy, texCoord.zw, gl_PointCoord.xy);
        #else
            vec2 uv = texCoord.xy;
        #endif
        gl_FragColor = texture2D(m_Texture, uv) * color;
    #else
        gl_FragColor = color;
    #endif
    float fogDensity = 3.0;

    vec4 fogColor = m_fogColor;

    float fogDistance = 200.0;

    fogColor.a = 1.0;

    float alpha = gl_FragColor.a;



    float depth = z_Pos / fogDistance;



    float fogFactor = exp2(-fogDensity * fogDensity * depth * depth * depth);

    fogFactor = clamp(fogFactor, 0.0, 1.0);


    if(gl_FragColor.a>0.4){
        gl_FragColor = mix(fogColor,gl_FragColor,fogFactor);
        //gl_FragColor.a=1.0;
    }
    else{gl_FragColor.a = 0.0;}
}