package net.mymai1208.extrapitanlib.builder.impl

import net.minecraft.util.Identifier
import net.mymai1208.extrapitanlib.ModComponent
import net.mymai1208.extrapitanlib.builder.BasicBuilder
import net.mymai1208.extrapitanlib.builder.ItemBuilder
import net.pitan76.mcpitanlib.api.item.CompatibleItemSettings
import net.pitan76.mcpitanlib.api.item.ExtendItem

class ItemBuilderImpl(val modComponent: ModComponent, val id: String) : BasicBuilder<ExtendItem>,
    ItemBuilder<ItemBuilderImpl> {
    private var settings = CompatibleItemSettings()
    private val identifier: Identifier = Identifier(modComponent.modId, id)
    override fun getIdentifier(): Identifier {
        return identifier
    }

    override fun build(): ExtendItem {
        return ExtendItem(settings)
    }

    override fun settings(lambda: CompatibleItemSettings.() -> Unit): ItemBuilderImpl {
        settings = settings.apply(lambda)

        return this
    }

}