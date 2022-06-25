package com.kisman.cc.features.command.exceptions

import com.kisman.cc.features.command.ICommand

/**
 * @author _kisman_
 * @since 19:30 of 22.06.2022
 */
class SimilarCommandNamesException(command1 : ICommand, command2 : ICommand) : RuntimeException("Similar names of ${command1::class.java.name} and ${command2::class.java.name}!")