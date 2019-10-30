attribute vec4 a_Position;
attribute vec2 a_TextureCoordinates;

varying vec2 v_TextureCoordinates;

uniform mat4 u_Matrix;

void main() {
    v_TextureCoordinates = a_TextureCoordinates;
    gl_Position =  u_Matrix * a_Position;
}