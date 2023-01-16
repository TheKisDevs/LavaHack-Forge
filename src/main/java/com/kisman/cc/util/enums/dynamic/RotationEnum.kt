package com.kisman.cc.util.enums.dynamic

import com.kisman.cc.util.entity.RotationSaver
import com.kisman.cc.util.enums.RotationLogic
import net.minecraft.client.Minecraft
import net.minecraft.util.math.Vec3d
import org.cubic.dynamictask.*
import net.minecraft.network.play.client.*
import com.kisman.cc.util.world.*
import net.minecraft.util.math.BlockPos

/**
 * @author _kisman_
 * @since 20:09 of 05.06.2022
 */
class RotationEnum {
    companion object {
        private val taskR : AbstractTask.DelegateAbstractTask<Void> = AbstractTask.types(
            Void::class.java,
            FloatArray::class.java,//YawPitch
            java.lang.Boolean::class.java//Silent
        )

        private val taskRFromSaver : AbstractTask.DelegateAbstractTask<Void> = AbstractTask.types(
            Void::class.java,
            RotationSaver::class.java,//RotationSaver(yaw, pitch, yaw head, yaw offset)
            java.lang.Boolean::class.java//Silent
        )

        private val taskCEntity : AbstractTask.DelegateAbstractTask<FloatArray> = AbstractTask.types(
            FloatArray::class.java,
            Integer::class.java,//ID of the entity
            RotationLogic::class.java//Rotation logic
        )

        private val taskCBlock : AbstractTask.DelegateAbstractTask<FloatArray> = AbstractTask.types(
            FloatArray::class.java,
            BlockPos::class.java//block
        )

        private val mc = Minecraft.getMinecraft()
    }

    @Suppress("unused")
    enum class Rotation(
        val taskR : AbstractTask<Void>,
        val taskRFromSaver : AbstractTask<Void>,
        val taskCEntity : AbstractTask<FloatArray>,
        val taskCBlock : AbstractTask<FloatArray>
    ) {
        None(
            taskR.task {
                return@task null
            },
            taskRFromSaver.task {
                return@task null
            },
            taskCEntity.task {
                return@task FloatArray(0)
            },
            taskCBlock.task {
                return@task FloatArray(0)
            }
        ),
        Client(
            taskR.task { arg : ArgumentFetcher ->
                if(arg.fetch<Boolean>(1)) {
                    return@task null
                }

                mc.player.rotationYaw = arg.fetch<FloatArray>(0)[0]
                mc.player.rotationPitch = arg.fetch<FloatArray>(0)[1]

                return@task null
            },
            taskRFromSaver.task { arg : ArgumentFetcher ->
                if(arg.fetch<Boolean>(1)) {
                    return@task null
                }
                mc.player.rotationYaw = arg.fetch<RotationSaver>(0).rotationYaw
                mc.player.rotationPitch = arg.fetch<RotationSaver>(0).rotationPitch

                return@task null
            },
            taskCEntity.task { arg: ArgumentFetcher ->
                return@task (
                        com.kisman.cc.util.world.RotationUtils.getRotation(com.kisman.cc.util.enums.dynamic.RotationEnum.Companion.mc.world.getEntityByID(arg.fetch(0)))
                        /*when(arg.fetch<RotationLogic>(1) as RotationLogic) {
                            RotationLogic.Default -> RotationUtils.getRotation(mc.world.getEntityByID(arg.fetch(0)))
//                            RotationLogic.WellMore -> RotationUtils.lookAtRandomed(mc.world.getEntityByID(arg.fetch(0)))
                        }*/
                )
            },
            taskCBlock.task { arg: ArgumentFetcher ->
                val block = arg.fetch<BlockPos>(0)
                return@task RotationUtils.calcAngle(mc.player.getPositionEyes(mc.renderPartialTicks), Vec3d(block.x.toDouble() + 0.5, block.y.toDouble(), block.z.toDouble() + 0.5))
            }
        ),
        ClientFull(
            taskR.task { arg : ArgumentFetcher ->
                if(arg.fetch<Boolean>(1)) {
                    return@task null
                }

                mc.player.rotationYaw = arg.fetch<FloatArray>(0)[0]
                mc.player.rotationYawHead = arg.fetch<FloatArray>(0)[0]
                mc.player.renderYawOffset = arg.fetch<FloatArray>(0)[0]
                mc.player.rotationPitch = arg.fetch<FloatArray>(0)[1]

                return@task null
            },
            taskRFromSaver.task { arg : ArgumentFetcher ->
                if(arg.fetch<Boolean>(1)) {
                    return@task null
                }
                mc.player.rotationYaw = arg.fetch<RotationSaver>(0).rotationYaw
                mc.player.rotationPitch = arg.fetch<RotationSaver>(0).rotationPitch
                mc.player.rotationYawHead = arg.fetch<RotationSaver>(0).rotationYawHead
                mc.player.renderYawOffset = arg.fetch<RotationSaver>(0).renderYawOffset

                return@task null
            }
        ),
        Packet(
            taskR.task { arg : ArgumentFetcher ->
                if(arg.fetch<Boolean>(1)) {
                    return@task null
                }

                mc.player.connection.sendPacket(CPacketPlayer.Rotation(arg.fetch<FloatArray>(0)[0], arg.fetch<FloatArray>(0)[1], mc.player.onGround))
                return@task null
            },
            taskRFromSaver.task { arg : ArgumentFetcher ->
                if(arg.fetch<Boolean>(1)) {
                    return@task null
                }

                mc.player.connection.sendPacket(CPacketPlayer.Rotation(arg.fetch<RotationSaver>(0).rotationYaw, arg.fetch<RotationSaver>(0).rotationPitch, mc.player.onGround))
                return@task null
            }
        ),
        PacketClient(
            taskR.task { arg : ArgumentFetcher ->
                Client.taskR.doTask(arg.fetch<FloatArray>(0), arg.fetch<Boolean>(1))
                Packet.taskR.doTask(arg.fetch<FloatArray>(0), arg.fetch<Boolean>(1))

                return@task null
            },
            taskRFromSaver.task { arg : ArgumentFetcher ->
                Client.taskRFromSaver.doTask(arg.fetch<RotationSaver>(0), arg.fetch<Boolean>(1))
                Packet.taskRFromSaver.doTask(arg.fetch<RotationSaver>(0), arg.fetch<Boolean>(1))

                return@task null
            }
        ),
        PacketClientFull(
            taskR.task { arg : ArgumentFetcher ->
                ClientFull.taskR.doTask(arg.fetch<FloatArray>(0), arg.fetch<Boolean>(1))
                Packet.taskR.doTask(arg.fetch<FloatArray>(0), arg.fetch<Boolean>(1))

                return@task null
            },
            taskRFromSaver.task { arg : ArgumentFetcher ->
                ClientFull.taskRFromSaver.doTask(arg.fetch<RotationSaver>(0), arg.fetch<Boolean>(1))
                Packet.taskRFromSaver.doTask(arg.fetch<RotationSaver>(0), arg.fetch<Boolean>(1))

                return@task null
            }
        ),
        ClientSilent(
            taskR.task { arg : ArgumentFetcher ->
                Client.taskR.doTask(arg.fetch<FloatArray>(0), false)

                return@task null
            },
            taskRFromSaver.task { arg : ArgumentFetcher ->
                Client.taskRFromSaver.doTask(arg.fetch<RotationSaver>(0), false)

                return@task null
            }
        ),
        ClientFullSilent(
            taskR.task { arg : ArgumentFetcher ->
                ClientFull.taskR.doTask(arg.fetch<FloatArray>(0), false)

                return@task null
            },
            taskRFromSaver.task { arg : ArgumentFetcher ->
                ClientFull.taskRFromSaver.doTask(arg.fetch<RotationSaver>(0), false)

                return@task null
            }
        ),
        PacketSilent(
            taskR.task { arg : ArgumentFetcher ->
                Packet.taskR.doTask(arg.fetch<FloatArray>(0), false)

                return@task null
            },
            taskRFromSaver.task { arg : ArgumentFetcher ->
                Packet.taskRFromSaver.doTask(arg.fetch<RotationSaver>(0), false)

                return@task null
            }
        ),
        PacketClientSilent(
            taskR.task { arg : ArgumentFetcher ->
                PacketClient.taskR.doTask(arg.fetch<FloatArray>(0), false)

                return@task null
            },
            taskRFromSaver.task { arg : ArgumentFetcher ->
                Client.taskRFromSaver.doTask(arg.fetch<RotationSaver>(0), false)
                Packet.taskRFromSaver.doTask(arg.fetch<RotationSaver>(0), false)

                return@task null
            }
        ),
        PacketClientFullSilent(
            taskR.task { arg : ArgumentFetcher ->
                PacketClientFull.taskR.doTask(arg.fetch<FloatArray>(0), false)

                return@task null
            },
            taskRFromSaver.task { arg : ArgumentFetcher ->
                ClientFull.taskRFromSaver.doTask(arg.fetch<RotationSaver>(0), false)
                Packet.taskRFromSaver.doTask(arg.fetch<RotationSaver>(0), false)

                return@task null
            }
        );

        constructor(taskR : AbstractTask<Void>) : this(taskR, Client.taskRFromSaver, Client.taskCEntity, Client.taskCBlock)
        constructor(taskR : AbstractTask<Void>, taskRFromSaver : AbstractTask<Void>) : this(taskR, taskRFromSaver, Client.taskCEntity, Client.taskCBlock)
    }
}