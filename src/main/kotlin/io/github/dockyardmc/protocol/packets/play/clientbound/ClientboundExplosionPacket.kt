package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.maths.vectors.Vector3d
import io.github.dockyardmc.particles.ParticleData
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.writeOptional
import io.github.dockyardmc.registry.registries.Particle
import io.github.dockyardmc.sounds.SoundEvent

class ClientboundExplosionPacket(
    location: Vector3d,
    playerVelocity: Vector3d?,
    particle: Particle,
    particleData: ParticleData?,
    explosionSound: SoundEvent
) : ClientboundPacket() {
    init {
        if(particleData != null && particleData.id != particle.getProtocolId()) throw Exception("Particle data ${particleData::class.simpleName} is not valid for particle ${particle.identifier}")
        if(particleData == null && ParticleData.requiresData(particle.getProtocolId())) throw Exception("Particle ${particle.identifier} requires particle data")

        location.write(buffer)
        buffer.writeOptional(playerVelocity) { buf, vec -> vec.write(buf); buf }
        buffer.writeVarInt(particle.getProtocolId())
        particleData?.write(buffer)
        explosionSound.write(buffer)
    }
}