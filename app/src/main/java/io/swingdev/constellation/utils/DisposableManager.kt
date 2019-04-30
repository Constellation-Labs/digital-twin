package io.swingdev.constellation.utils

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

object DisposableManager {

    private var instance: CompositeDisposable = CompositeDisposable()

    fun add(disposable: Disposable) {
        instance.add(disposable)
    }

    fun dispose() {
        instance.dispose()
    }
}