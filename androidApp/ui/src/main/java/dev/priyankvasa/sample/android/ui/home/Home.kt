package dev.priyankvasa.sample.android.ui.home

import dev.priyankvasa.sample.android.ui.navigation.Destination

sealed class Home(path: String) : Destination("/home$path") {
    object Dest1 : Home("/dest1")
    object Dest2 : Home("/dest2")
    companion object : Home("")
}
