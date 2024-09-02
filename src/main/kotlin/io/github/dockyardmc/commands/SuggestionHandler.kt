@file:Suppress("UNCHECKED_CAST")

package io.github.dockyardmc.commands

import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundSuggestionsResponse
import io.github.dockyardmc.utils.getEnumEntries
import kotlin.reflect.KClass

object SuggestionHandler {

    fun handleSuggestionInput(transactionId: Int, inputCommand: String, player: Player) {
        val tokens = inputCommand.removePrefix("/").split(" ").toMutableList()
        val commandName = tokens[0]
        val command = Commands.commands[commandName] ?: return

        val currentlyTyped = tokens.last()

        if(tokens.size >= 2 && command.subcommands[tokens[1]] != null) {
            val subcommand = command.subcommands[tokens[1]]!!
            val current = subcommand.arguments.values.toList()[tokens.size - 3]
            handleSuggestion(current, inputCommand, currentlyTyped, player, transactionId)
        } else {
            val current = command.arguments.values.toList()[tokens.size - 2]
            handleSuggestion(current, inputCommand, currentlyTyped, player, transactionId)
        }
    }


    fun handleSuggestion(current: CommandArgumentData, inputCommand: String, currentlyTyped: String, player: Player, transactionId: Int) {
        // Auto suggest enum entries if user defined suggestion is null
        if(current.argument is EnumArgument && current.suggestions == null) {
            val enum = current.argument.enumType as KClass<Enum<*>>
            current.suggestions = SuggestionProvider.withContext { getEnumEntries(enum).map { it.name.lowercase() } }
        }

        val suggestions = current.suggestions ?: return
        val startAt = inputCommand.length - currentlyTyped.length
        val suggestedValues = suggestions.suggestions.invoke(player)

        val filtered = suggestedValues.filter { it.startsWith(currentlyTyped) }

        player.sendPacket(ClientboundSuggestionsResponse(transactionId, startAt, inputCommand.length, filtered))
    }
}