package the.kis.devs.server.capeapi.cape

/**
 * @author _kisman_
 * @since 10:09 of 01.11.2022
 */
enum class Capes(
    val name0 : String,
    val subscription : Int
) {
    Release("release", 1),
    Beta("beta", 2),
    Developer("developer", 3)
}