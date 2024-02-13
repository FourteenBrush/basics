package com.github.spigotbasics.modules.basicsgive

import com.github.spigotbasics.core.command.parsed.MapCommandContext
import com.github.spigotbasics.core.command.parsed.ParsedCommandContextExecutor
import com.github.spigotbasics.core.extensions.addOrDrop
import com.github.spigotbasics.core.logger.BasicsLoggerFactory
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class GiveExecutor(private val module: BasicsGiveModule) : ParsedCommandContextExecutor<MapCommandContext> {
    private val logger = BasicsLoggerFactory.getModuleLogger(module, GiveExecutor::class)

    override fun execute(
        sender: CommandSender,
        context: MapCommandContext,
    ) {
        // Bukkit.broadcastMessage("GiveExecutor called")

        logger.debug(10, "Max Give Amount: ${module.maxAmount}")

        val material = context["item"] as Material
        val receiver = context.getOrDefault("receiver", sender) as Player
        val amount = context.getOrDefault("amount", module.getStackSize(material)) as Int

        val item = ItemStack(material, amount)

        val result = receiver.inventory.addOrDrop(item.clone(), module.dropOverflow)
        logger.debug(10, "Added to inventory: ${result.addedToInv}, Dropped to world: ${result.droppedToWorld}")

        val msg =
            if (sender === receiver) {
                module.msgGiveSelf(receiver, item)
            } else {
                module.msgGiveOthers(receiver, item)
            }

        msg.sendToSender(sender)
    }
}
