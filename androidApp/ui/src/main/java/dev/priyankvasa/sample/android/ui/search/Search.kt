package dev.priyankvasa.sample.android.ui.search

import dev.priyankvasa.sample.android.ui.navigation.Destination

sealed class Search(path: String) : Destination("/search$path") {

    companion object : Search("")
}
