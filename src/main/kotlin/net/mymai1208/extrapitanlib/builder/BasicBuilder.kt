package net.mymai1208.extrapitanlib.builder

import net.minecraft.util.Identifier

interface BasicBuilder<T> {
    fun getIdentifier(): Identifier
    fun build(): T
}