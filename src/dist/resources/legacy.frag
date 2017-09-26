#version 120

varying vec2 textureCoord;

uniform sampler2D texImage;
uniform vec4 color;

void main() {
    vec4 textureColor = texture2D(texImage, textureCoord);
    gl_FragColor = color * textureColor;
}
