package com.github.spigotbasics.modules.basicsgive

import com.github.spigotbasics.core.command.parsed.arguments.IntRangeArg
import com.github.spigotbasics.core.command.parsed.arguments.ItemMaterialArg
import com.github.spigotbasics.core.command.parsed.arguments.SelectorMultiPlayerArg
import com.github.spigotbasics.core.command.parsed.arguments.SnbtArg
import com.github.spigotbasics.core.messages.tags.providers.ItemStackTag
import com.github.spigotbasics.core.module.AbstractBasicsModule
import com.github.spigotbasics.core.module.loader.ModuleInstantiationContext
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class BasicsGiveModule(context: ModuleInstantiationContext) : AbstractBasicsModule(context) {
    val permission =
        permissionManager.createSimplePermission("basics.give", "Allows the player to use the /give command")

    val permissionOthers =
        permissionManager.createSimplePermission(
            "basics.give.others",
            "Allows to give items to others using the /give command",
        )

    val permissionSnbt =
        permissionManager.createSimplePermission(
            "basics.give.snbt",
            "Allows to give items using SNBT Strings using the /give command",
        )

    val maxAmount
        get() = config.getInt("max-amount", 64)

    val dropOverflow
        get() = config.getBoolean("drop-overflow")

    val dropnaturally
        get() = config.getBoolean("drop-naturally")

    fun getStackSize(material: Material): Int {
        val defaultAmount = config.get("default-amount") ?: "stack"
        if (defaultAmount is Int) {
            return defaultAmount
        }
        if (defaultAmount is String) {
            if (defaultAmount.equals("stack", true)) {
                return material.maxStackSize
            }
            return defaultAmount.toIntOrNull() ?: 1
        }
        return 1
    }

    fun msgGiveOthers(
        receiver: Player,
        item: ItemStack,
    ) = messages.getMessage("give-others").concerns(receiver).tags(ItemStackTag(item))

    fun msgGiveSelf(
        receiver: Player,
        item: ItemStack,
    ) = messages.getMessage("give").concerns(receiver).tags(ItemStackTag(item))

    override fun onEnable() {
        val amountRangeArg = IntRangeArg("Amount", { 1 }, ::maxAmount)
        val itemArg = ItemMaterialArg("Item")
        val snbtItemArg = SnbtArg("Item SNBT")
        val multiPlayerArg = SelectorMultiPlayerArg("Receiving Player(s)")

        commandFactory.parsedCommandBuilder("give", permission)
            .mapContext {
                usage = "[Receiving Player(s)] <Item|SNBT> [Amount]"

                path {
                    playerOnly()

                    arguments {
                        named("item", itemArg)
                    }
                }

                path {
                    playerOnly()
                    permissions(permissionSnbt)

                    arguments {
                        named("snbt", snbtItemArg)
                    }
                }

                path {
                    permissions(permissionOthers)

                    arguments {
                        named("receiver", multiPlayerArg)
                        named("item", itemArg)
                    }
                }

                path {
                    permissions(permissionOthers, permissionSnbt)

                    arguments {
                        named("receiver", multiPlayerArg)
                        named("snbt", snbtItemArg)
                    }
                }

                path {
                    playerOnly()

                    arguments {
                        named("item", itemArg)
                        named("amount", amountRangeArg)
                    }
                }

                path {
                    playerOnly()
                    permissions(permissionSnbt)

                    arguments {
                        named("snbt", snbtItemArg)
                        named("amount", amountRangeArg)
                    }
                }

                path {
                    permissions(permissionOthers)

                    arguments {
                        named("receiver", multiPlayerArg)
                        named("item", itemArg)
                        named("amount", amountRangeArg)
                    }
                }

                path {
                    permissions(permissionOthers, permissionSnbt)

                    arguments {
                        named("receiver", multiPlayerArg)
                        named("snbt", snbtItemArg)
                        named("amount", amountRangeArg)
                    }
                }
            }
            .executor(GiveExecutor(this))
            .register()
    }
}
