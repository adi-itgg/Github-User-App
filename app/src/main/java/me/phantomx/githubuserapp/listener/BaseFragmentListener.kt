package me.phantomx.githubuserapp.listener

import androidx.viewbinding.ViewBinding

interface BaseFragmentListener<T: ViewBinding> {

    fun initBinding(): T

    fun onCreated()

}