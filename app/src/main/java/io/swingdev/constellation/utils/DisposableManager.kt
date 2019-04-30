package io.swingdev.constellation.utils

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class DisposableManager private constructor() {

    companion object {
        fun add(disposable: Disposable) {
            getInstance().add(disposable)
        }

        fun dispose() {
            getInstance().dispose()
        }

        private var instance: CompositeDisposable? = null

        private fun getInstance(): CompositeDisposable {
            return instance ?: synchronized(this) {
                instance ?: CompositeDisposable().also { instance = it }
            }
        }
    }
}