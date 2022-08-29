package com.kisman.cc.features.nocom.modules.tracker

import com.kisman.cc.features.nocom.modules.NoComTrackerModule
import com.kisman.cc.util.Globals.mc
import com.kisman.cc.util.chat.cubic.AbstractChatMessage
import com.kisman.cc.util.chat.cubic.ChatUtility
import net.minecraft.network.play.client.CPacketPlayerDigging
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import kotlin.math.abs

/**
 * @author _kisman_
 * @since 12:29 of 28.08.2022
 */
class TrackedPlayer(
    x : Int,
    z : Int
) {
    private var progress = "|"
    private var renderDistance = NoComTrackerModule.instance!!.renderDistance.supplierInt

    private var primaryChunks = ArrayList<TrackedChunk>(4)
    var lastRequestedChunks = ArrayList<ChunkPos>()
    private var lastReceivedChunks = ArrayList<ChunkPos>()

    var dimension = mc.player.dimension

    private var estimatedCenter : ChunkPos? = null

    private var sinceLastRequest = System.currentTimeMillis()

    private var isReadyToRequest = true
    var isSpiraling = false

    private var failures = 0
    private var successfulPolls = 0

    private var spiralX = 0
    private var spiralZ = 0


    init {
        setBlockCoords(
            x,
            z
        )

        initChunksUsingCenter()
    }

    fun update() {
        when(lastReceivedChunks.size) {
            0 -> {
                failures++

                if(NoComTrackerModule.instance!!.debugErrors.valBoolean) {
                    print(
                        "Received 0 primary chunks, did we lose them? failures: $failures",
                        ChatUtility.error()
                    )
                }

                if(NoComTrackerModule.instance!!.notify.valBoolean) {
                    print(
                        "Tracker list target.",
                        ChatUtility.warning()
                    )
                }

                estimatedCenter = ChunkPos(BlockPos(
                    NoComTrackerModule.instance!!.x.valInt,
                    0,
                    NoComTrackerModule.instance!!.z.valInt
                ))
            }
            1 -> {
                estimatedCenter = lastReceivedChunks[0]
                failures = 0
            }
            2 -> {
                //https://i.imgur.com/IO4UvMQ.png <-- original code btw :^)

                if(
                    lastReceivedChunks[0].x != lastReceivedChunks[1].x
                    && lastReceivedChunks[0].z != lastReceivedChunks[1].z
                    && NoComTrackerModule.instance!!.debugErrors.valBoolean
                ) {
                    print(
                        "Received 2 chunks that not on line. Is this split?",
                        ChatUtility.error()
                    )
                }

                estimatedCenter = average(
                    lastReceivedChunks[0],
                    lastReceivedChunks[1]
                )
                failures = 0
            }
            3 -> {
                var index = 0

                for(i in 0..3) {
                    var exists = false

                    for(pos in lastReceivedChunks) {
                        if(primaryChunks[i].getChunkPos() == pos) {
                            exists = true
                            break
                        }
                    }

                    if(!exists) {
                        break
                    }

                    index++
                }

                estimatedCenter = oppositeCorner(index)
                failures = 0
            }
            4 -> {
                failures = 0
            }
        }

        initChunksUsingCenter()

        NoComTrackerModule.instance!!.x.valDouble = blockCoords().x.toDouble()
        NoComTrackerModule.instance!!.z.valDouble = blockCoords().z.toDouble()

        lastReceivedChunks.clear()
        updateProgress()

        if(failures == 0) {
            successfulPolls++
        } else if(NoComTrackerModule.instance!!.autoSpiral.valBoolean && failures > NoComTrackerModule.instance!!.spiralTrigger.valDouble) {
            isSpiraling = true

            if(NoComTrackerModule.instance!!.debugErrors.valBoolean) {
                print(
                    "Enabling Spiral Scanner",
                    ChatUtility.warning()
                )
            }
        }

        isReadyToRequest = true
    }

    fun onDimensionChange(
        newDimension : Int
    ) {
        if(dimension == newDimension) {
            return
        }

        val old = blockCoords()
        var x = old.x
        var z = old.z

        if(newDimension == -1) {
            x /= 8
            z /= 8

            if(NoComTrackerModule.instance!!.debug.valBoolean) {
                print(
                    "Dimension has been changed to nether.",
                    ChatUtility.warning()
                )
            }
        } else {
            x *= 8
            z *= 8

            if(NoComTrackerModule.instance!!.debug.valBoolean) {
                print(
                    "Dimension has been changed to ${if(newDimension == 0) "overworld" else "end"}.",
                    ChatUtility.warning()
                )
            }
        }

        dimension = newDimension

        if(isSpiraling) {
            resetSpiral()
        }

        setBlockCoords(
            x,
            z
        )

        isReadyToRequest = true
    }

    fun processSpiral(
        max : Int
    ) : Boolean {
        val steps = NoComTrackerModule.instance!!.spiralChunkStep.valInt * 16

        for(i in 0..NoComTrackerModule.instance!!.spiralPPT.valInt) {
            val sx = spiralX + blockCoords().x
            val sz = spiralZ + blockCoords().z
            val pos = BlockPos(
                sx,
                0,
                sz
            )

            mc.player.connection.sendPacket(CPacketPlayerDigging(
                CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK,
                pos,
                EnumFacing.UP
            ))

            lastRequestedChunks.add(ChunkPos(pos))

            if(abs(spiralX) <= abs(spiralZ) && (spiralX != spiralZ || spiralX >= 0)) {
                spiralX += if(spiralZ >= 0) steps else -steps
            } else {
                spiralZ += if(spiralX >= 0) -steps else steps
            }
        }

        return spiralX < max
    }

    fun onCoordReceive(
        pos : ChunkPos
    ) {
        lastReceivedChunks.add(pos)
        lastRequestedChunks.remove(pos)

        if(isSpiraling) {
            if(NoComTrackerModule.instance!!.debug.valBoolean) {
                print(
                    "Spiral found target. Tracking...",
                    ChatUtility.complete()
                )
            }

            resetSpiral()
            update()
        }
    }

    fun requestChunk() : Boolean {
        if((System.currentTimeMillis() - sinceLastRequest) > NoComTrackerModule.instance!!.timeOut.valDouble) {
            isReadyToRequest = true
        }

        if(!isReadyToRequest) {
            return false
        }

        lastRequestedChunks.clear()

        for(chunk in primaryChunks) {
            if(NoComTrackerModule.instance!!.debug.valBoolean) {
                print(
                    "REQUESTING CHUNKS...",
                    ChatUtility.warning()
                )
            }

            mc.player.connection.sendPacket(CPacketPlayerDigging(
                CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK,
                chunk.getBlockPos(),
                EnumFacing.UP
            ))

            lastRequestedChunks.add(chunk.getChunkPos())
        }

        isReadyToRequest = false
        sinceLastRequest = System.currentTimeMillis()

        return true
    }

    fun resetSpiral() {
        isSpiraling = false
        spiralX = 0
        spiralZ = 0
        lastRequestedChunks.clear()
    }

    private fun updateProgress() {
        if(failures > 0) {
            progress = "!$failures!"
        } else if(progress.contains("!")) {
            progress = "|"
        }

        when(progress) {
            "|" -> progress = "/"
            "/" -> progress = "-"
            "-" -> progress = "\\"
            "\\" -> progress = "|"
        }
    }

    private fun blockCoords() : BlockPos = estimatedCenter!!.getBlock(0, 0, 0)

    private fun oppositeCorner(
        index : Int
    ) : ChunkPos = when(index) {
        0 -> {
            primaryChunks[3].getChunkPos()
        }
        1 -> {
            primaryChunks[2].getChunkPos()
        }
        2 -> {
            primaryChunks[1].getChunkPos()
        }
        3 -> {
            primaryChunks[0].getChunkPos()
        }
        else -> {
            estimatedCenter!!
        }
    }

    private fun average(
        first : ChunkPos,
        second : ChunkPos
    ) : ChunkPos = ChunkPos(
        (first.x + second.x) / 2,
        (first.x + second.x) / 2
    )

    private fun setBlockCoords(
        x : Int,
        z : Int
    ) {
        NoComTrackerModule.instance!!.x.valDouble = x.toDouble()
        NoComTrackerModule.instance!!.z.valDouble = z.toDouble()

        estimatedCenter = ChunkPos(BlockPos(
            x,
            0,
            z
        ))
    }

    private fun initChunksUsingCenter() {
        for(i in 0..3) {
            var x = estimatedCenter!!.x
            var z = estimatedCenter!!.z

            when(i) {
                0 -> {
                    x -= renderDistance.get()
                    z -= renderDistance.get()
                }
                1 -> {
                    x += renderDistance.get()
                    z -= renderDistance.get()
                }
                2 -> {
                    x -= renderDistance.get()
                    z += renderDistance.get()
                }
                3 -> {
                    x += renderDistance.get()
                    z += renderDistance.get()
                }
            }

            primaryChunks[i] = TrackedChunk(
                x,
                z
            )
        }
    }

    private fun print(
        message : String,
        handler : AbstractChatMessage
    ) {
        NoComTrackerModule.instance!!.print(
            message,
            handler
        )
    }

    fun getReport() : String = "Last reported coordinates: ${
        when(dimension) {
            0 -> {
                "${blockCoords().x}, ${blockCoords().z} in dimension overworld / ${blockCoords().x / 8}, ${blockCoords().z / 8} in dimension nether"
            }
            1 -> {
                "${blockCoords().x}, ${blockCoords().z} in dimension end"
            }
            -1 -> {
                "${blockCoords().x}, ${blockCoords().z} in dimension nether / ${blockCoords().x * 8}, ${blockCoords().z * 8} in dimension overworld"
            }
            else -> "kill yourself with love <3"
        }
    }\n Successful polls: $successfulPolls / failures before disabling module: $failures"
}