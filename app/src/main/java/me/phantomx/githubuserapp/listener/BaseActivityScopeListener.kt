package me.phantomx.githubuserapp.listener

import androidx.viewbinding.ViewBinding

interface BaseActivityScopeListener<T: ViewBinding> {

    fun initViewBinding(): T

    fun initialize()

}