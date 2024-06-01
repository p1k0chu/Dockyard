package io.github.dockyardmc.utils

import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.writeVarInt
import io.netty.buffer.ByteBuf

data class Vector3(
    var x: Int,
    var y: Int,
    var z: Int,
)

data class Vector3f(
    var x: Float,
    var y: Float,
    var z: Float,
)

fun ByteBuf.writeVector3f(vector3: Vector3f) {
    this.writeFloat(vector3.x)
    this.writeFloat(vector3.y)
    this.writeFloat(vector3.z)
}

fun ByteBuf.writeVector3(vector3: Vector3) {
    this.writeVarInt(vector3.x)
    this.writeVarInt(vector3.y)
    this.writeVarInt(vector3.z)
}

fun ByteBuf.readVector3(): Vector3 {
    return Vector3(this.readVarInt(), this.readVarInt(), this.readVarInt())
}

fun ByteBuf.readBlockPosition(): Vector3 {
    val value: Long = this.readLong()
    val x = (value shr 38).toInt()
    val y = (value shl 52 shr 52).toInt()
    val z = (value shl 26 shr 38).toInt()
    return Vector3(x, y, z)
}


data class Vector2(
    var x: Float,
    var y: Float,
)