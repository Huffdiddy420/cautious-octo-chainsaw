package com.stripe.android.paymentsheet.paymentdatacollection.ach

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class USBankAccountFormViewModel : ViewModel() {
    private val _currentScreenState: MutableStateFlow<USBankAccountFormScreenState> =
        MutableStateFlow(USBankAccountFormScreenState.NameAndEmailCollection)
    val currentScreenState: StateFlow<USBankAccountFormScreenState>
        get() = _currentScreenState

    private val _name = MutableStateFlow("")
    val name: StateFlow<String>
        get() = _name

    private val _email = MutableStateFlow("")
    val email: StateFlow<String>
        get() = _email

    fun updateScreenState(state: USBankAccountFormScreenState) {
        _currentScreenState.tryEmit(state)
    }

    fun updateName(name: String) {
        _name.tryEmit(name)
    }

    fun updateEmail(email: String) {
        _email.tryEmit(email)
    }

    class Factory(
        owner: SavedStateRegistryOwner,
        defaultArgs: Bundle? = null
    ) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            key: String,
            modelClass: Class<T>,
            savedStateHandle: SavedStateHandle
        ): T {
            return USBankAccountFormViewModel() as T
        }
    }
}
