package dadoufi.topmakelaar.util

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import dadoufi.topmakelaar.R
import dadoufi.topmakelaar.data.entities.UiState
import io.reactivex.Flowable
import io.reactivex.rxkotlin.Flowables
import java.util.concurrent.TimeUnit


fun Snackbar.config(context: Context) {
    val params = this.view.layoutParams as ViewGroup.MarginLayoutParams
    params.setMargins(12, 12, 12, 12)
    this.view.layoutParams = params

    this.view.background = context.getDrawable(R.drawable.bg_snackbar)

    ViewCompat.setElevation(this.view, 6f)
}

fun Fragment.createSnackBar(
    parentView: View,
    error: UiState.Error.RefreshError,
    duration: Int = Snackbar.LENGTH_LONG,
    actionClickListener: () -> Unit
): Snackbar {
    return Snackbar.make(parentView, error.message.toString(), duration).apply {
        setAction(R.string.retry) {
            actionClickListener()
        }
        config(view.context)
    }
}


fun <T> Flowable<T>.retryOnError(
    predicate: (Throwable) -> Boolean,
    maxRetry: Int,
    delayBeforeRetry: Long
): Flowable<T> =
    retryWhen {
        Flowables.zip(
            it.map { throwable -> if (predicate(throwable)) throwable else throw throwable },
            Flowable.interval(delayBeforeRetry, TimeUnit.MILLISECONDS)
        )
            .map { pair -> if (pair.second >= maxRetry) throw pair.first }
    }


inline fun <T> LiveData<T>.observeK(owner: LifecycleOwner, crossinline observer: (T?) -> Unit) {
    this.observe(owner, Observer { observer(it) })
}




