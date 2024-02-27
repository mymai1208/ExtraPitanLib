package net.mymai1208.extrapitanlib.builder

import net.minecraft.util.Identifier
import net.mymai1208.extrapitanlib.ModComponent
import net.pitan76.mcpitanlib.api.item.CompatibleItemSettings
import net.pitan76.mcpitanlib.api.item.ExtendBlockItem

class BlockItemBuilder(val modComponent: ModComponent, val identifier: Identifier) : BasicBuilder<ExtendBlockItem>, ItemBuilder<BlockItemBuilder> {
    private var settings = CompatibleItemSettings()
    override fun getIdentifier(): Identifier {
        return identifier
    }

    override fun build(): ExtendBlockItem {
        return ExtendBlockItem(modComponent.registeredBlocks[identifier]!!, settings)
    }

    override fun settings(lambda: CompatibleItemSettings.() -> Unit): BlockItemBuilder {
        settings = settings.apply(lambda)

        return this
    }
}