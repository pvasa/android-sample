package dev.priyankvasa.sample.android.ui.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

abstract class Destination(private val route: String) {
    protected open val navArgs: List<NavArg> = emptyList()
    val namedNavArgs get() = navArgs.asNamedNavArgs()

    private val preparedRoute by lazy(LazyThreadSafetyMode.NONE) {
        buildString {
            val requiredArgs =
                navArgs.filterIsInstance<NavArg.RequiredArg>()

            val optionalArgs =
                navArgs.filterIsInstance<NavArg.OptionalArg>()

            append(String.format(route, *requiredArgs.map(NavArg::name).toTypedArray()))

            if (optionalArgs.isNotEmpty()) {
                append("?")

                val args = optionalArgs.joinToString(",") { arg ->
                    "${arg.name}={${arg.name}}"
                }

                append(args)
            }
        }
    }

    /**
     * @return [String] - the prepared route for this nav graph destination
     */
    operator fun invoke(vararg args: Pair<String, String?>): String =
        args.fold(preparedRoute) { route, (argKey, argValue) ->
            route.replace("{$argKey}", argValue.orEmpty())
        }

    sealed class NavArg {
        abstract val name: String
        abstract val type: NavType<*>

        data class RequiredArg(
            override val name: String,
            override val type: NavType<*>,
        ) : NavArg()

        data class OptionalArg(
            override val name: String,
            override val type: NavType<*>,
            val nullable: Boolean = true,
            val defaultValue: Any? = null,
        ) : NavArg() {
            init {
                if (!nullable) {
                    requireNotNull(defaultValue) {
                        "An optional argument should either be nullable or have a default value."
                    }
                }
            }
        }
    }
}

fun List<Destination.NavArg>.asNamedNavArgs(): List<NamedNavArgument> =
    map { arg ->
        when (arg) {
            is Destination.NavArg.RequiredArg ->
                navArgument(arg.name) {
                    type = arg.type
                    nullable = false
                }

            is Destination.NavArg.OptionalArg ->
                navArgument(arg.name) {
                    type = arg.type
                    nullable = arg.nullable
                    defaultValue = arg.defaultValue
                }
        }
    }
