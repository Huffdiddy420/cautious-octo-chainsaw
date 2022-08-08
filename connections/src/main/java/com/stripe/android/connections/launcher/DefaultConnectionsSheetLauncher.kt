package com.stripe.android.connections.launcher

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import com.stripe.android.connections.ConnectionsSheet
import com.stripe.android.connections.ConnectionsSheetResult
import com.stripe.android.connections.ConnectionsSheetResultCallback
import com.stripe.android.connections.launcher.ConnectionsSheetContract.Result

internal class DefaultConnectionsSheetLauncher(
    private val activityResultLauncher: ActivityResultLauncher<ConnectionsSheetContract.Args>
) : ConnectionsSheetLauncher {

    constructor(
        activity: ComponentActivity,
        callback: ConnectionsSheetResultCallback
    ) : this(
        activity.registerForActivityResult(
            ConnectionsSheetContract()
        ) {
            callback.onConnectionsSheetResult(it.toExposedResult())
        }
    )

    constructor(
        fragment: Fragment,
        callback: ConnectionsSheetResultCallback
    ) : this(
        fragment.registerForActivityResult(
            ConnectionsSheetContract()
        ) {
            callback.onConnectionsSheetResult(it.toExposedResult())
        }
    )

    override fun present(configuration: ConnectionsSheet.Configuration) {
        activityResultLauncher.launch(
            ConnectionsSheetContract.Args.Default(
                configuration,
            )
        )
    }
}

private fun Result.toExposedResult(): ConnectionsSheetResult = when (this) {
    is Result.Canceled -> ConnectionsSheetResult.Canceled
    is Result.Failed -> ConnectionsSheetResult.Failed(error)
    is Result.Completed -> ConnectionsSheetResult.Completed(
        linkAccountSession = linkAccountSession,
    )
}
