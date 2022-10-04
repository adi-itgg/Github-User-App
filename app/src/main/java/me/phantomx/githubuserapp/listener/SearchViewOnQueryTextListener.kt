package me.phantomx.githubuserapp.listener

import android.widget.SearchView

interface SearchViewOnQueryTextListener: SearchView.OnQueryTextListener {

    override fun onQueryTextSubmit(query: String?) = false

    override fun onQueryTextChange(newText: String?) = false

}