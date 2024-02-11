package com.github.spigotbasics.core.command.parsed.arguments

import com.github.spigotbasics.common.Dictionary
import com.github.spigotbasics.core.command.parsed.CommandArgument
import org.bukkit.Material

class MaterialArgument : CommandArgument<Material>() {
    private val materials = Dictionary.from(Material.entries.filter { it.isItem }.map { it.name to it })
    private val materialNames = materials.keys.toList().sorted()

    override fun parse(value: String): Material? {
        return materials[value]
    }

    override fun tabComplete(): List<String> {
        return materialNames
    }
}
