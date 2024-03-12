package net.mymai1208.extrapitanlib.builder.impl

import net.minecraft.util.Identifier
import net.mymai1208.extrapitanlib.ModComponent
import net.mymai1208.extrapitanlib.builder.ItemBuilder
import net.pitan76.mcpitanlib.api.item.CompatibleItemSettings
import net.pitan76.mcpitanlib.api.item.ExtendBlockItem

class BlockItemBuilder(val modComponent: ModComponent, val id: Identifier) : ItemBuilder<ExtendBlockItem, BlockItemBuilder> {
    private var settings = CompatibleItemSettings()
    override fun getIdentifier(): Identifier {
        return id
    }

    override fun build(): ExtendBlockItem {
        return ExtendBlockItem(modComponent.registeredBlocks[id]!!, settings)
    }

    override fun settings(lambda: CompatibleItemSettings.() -> Unit): BlockItemBuilder {
        settings = settings.apply(lambda)

        return this
    }
}