package net.mymai1208.extrapitanlib.builder

import net.pitan76.mcpitanlib.api.item.CompatibleItemSettings

interface ItemBuilder<T, Y> : BasicBuilder<T> {
    fun settings(lambda: CompatibleItemSettings.() -> Unit): Y
}