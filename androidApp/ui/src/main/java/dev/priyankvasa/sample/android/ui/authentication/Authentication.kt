package dev.priyankvasa.sample.android.ui.authentication

import android.net.Uri
import androidx.navigation.NavType
import dev.priyankvasa.sample.android.ui.home.Home
import dev.priyankvasa.sample.android.ui.navigation.Destination

sealed class Authentication(path: String) :
    Destination("/authentication$path") {

    object Check : Authentication("/check/{%1\$s}") {
        override val navArgs: List<NavArg> = listOf(
            NavArg.RequiredArg(
                name = Args.navRouteOnAuth,
                type = NavType.StringType,
            ),
        )

        operator fun invoke(navRouteOnAuth: String): String =
            this(Args.navRouteOnAuth to Uri.encode(navRouteOnAuth))
    }

    object Methods : Authentication("/methods/{%1\$s}") {
        override val navArgs: List<NavArg> = listOf(
            NavArg.RequiredArg(
                name = Args.navRouteOnAuth,
                type = NavType.StringType,
            ),
        )

        operator fun invoke(navRouteOnAuth: String): String =
            this(Args.navRouteOnAuth to Uri.encode(navRouteOnAuth))
    }

    object Password : Authentication("/password/{%1\$s}/{%2\$s}") {
        override val navArgs: List<NavArg> = listOf(
            NavArg.RequiredArg(
                name = Args.emailAddress,
                type = NavType.StringType,
            ),
            NavArg.RequiredArg(
                name = Companion.Args.navRouteOnAuth,
                type = NavType.StringType,
            ),
        )

        operator fun invoke(emailAddress: String, navRouteOnAuth: String): String =
            this(
                Args.emailAddress to emailAddress,
                Companion.Args.navRouteOnAuth to Uri.encode(navRouteOnAuth),
            )

        object Args {
            const val emailAddress = "email_address"
        }
    }

    companion object : Authentication("") {
        override val navArgs: List<NavArg> = listOf(
            NavArg.OptionalArg(
                name = Args.navRouteOnAuth,
                type = NavType.StringType,
                nullable = false,
                defaultValue = Uri.encode(Home()),
            ),
        )

        operator fun invoke(navRouteOnAuth: String): String =
            this(Args.navRouteOnAuth to Uri.encode(navRouteOnAuth))

        object Args {
            const val navRouteOnAuth = "nav_route_on_auth"
        }
    }
}
