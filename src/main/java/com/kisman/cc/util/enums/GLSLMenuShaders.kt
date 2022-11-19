package com.kisman.cc.util.enums

/**
 * TODO: shadetoy/glslsandbox shader parser
 *
 * @author _kisman_
 * @since 14:11 of 19.11.2022
 */
/*
enum class GLSLMenuShaders(
    val link : String,
    val type : ShaderTypes
) {

    ;

    */
/*fun parseShader() : String = if(type == ShaderTypes.ShaderToy) {

    } else {
        throw NullPointerException("GLSLSandBox shaders is not supporting yet")
    }*//*

}

enum class ShaderTypes {
    ShaderToy,
    GLSLSandBox
}*/

//TODO: finish it in the next update
enum class GLSLMenuShaders(
    val file : String
) {
    //https://www.shadertoy.com/view/stccWf
    SquaresHighway("squares_highway.fsh"),

    //https://www.shadertoy.com/view/MlfGR4
    VoxelPacMan("voxel_pac_man.fsh"),

    //https://glslsandbox.com/e#94387.0
    Squares2("squares2.fsh")

    ;

    fun path() : String = "assets/kismancc/shader/glslmenu/shaders/$file"
}