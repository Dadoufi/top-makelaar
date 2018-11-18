package dadoufi.topmakelaar.remote.util

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.io.IOException
import java.lang.reflect.Type

class RxErrorHandlingCallAdapterFactory : CallAdapter.Factory() {

    private val _original by lazy {
        RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io())
    }

    companion object {
        fun create(): CallAdapter.Factory =
            RxErrorHandlingCallAdapterFactory()
    }

    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *> {
        val wrapped = _original.get(returnType, annotations, retrofit) as CallAdapter<out Any, *>
        return RxCallAdapterWrapper(
            retrofit,
            wrapped
        )
    }

    private class RxCallAdapterWrapper<R>(
        val _retrofit: Retrofit,
        val _wrappedCallAdapter: CallAdapter<R, *>
    ) : CallAdapter<R, Observable<R>> {

        override fun responseType(): Type = _wrappedCallAdapter.responseType()


        @Suppress("UNCHECKED_CAST")
        override fun adapt(call: Call<R>): Observable<R> {
            val adapted = (_wrappedCallAdapter.adapt(call) as Observable<R>)
            adapted.onErrorResumeNext { throwable: Throwable ->
                Observable.error(asRetrofitException(throwable))
            }

            return adapted
        }

        private fun asRetrofitException(throwable: Throwable): RetrofitException {
            // We had non-200 http error
            if (throwable is HttpException) {
                val response = throwable.response()

                if (throwable.code() == 422) {
                    // on out api 422'regex get metadata in the response. Adjust logic here based on your needs
                    return RetrofitException.httpErrorWithObject(
                        response.raw().request().url().toString(),
                        response,
                        _retrofit
                    )
                } else {
                    return RetrofitException.httpError(
                        response.raw().request().url().toString(),
                        response,
                        _retrofit
                    )
                }
            }

            // A network error happened
            if (throwable is IOException) {
                return RetrofitException.networkError(
                    throwable
                )
            }

            // We don't know what happened. We need to simply convert to an unknown error
            return RetrofitException.unexpectedError(
                throwable
            )
        }

    }
}