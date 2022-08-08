package com.stripe.example.activity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stripe.android.model.PaymentMethod
import com.stripe.android.model.PaymentMethodCreateParams
import com.stripe.android.payments.paymentlauncher.PaymentResult
import com.stripe.example.databinding.CreateCardPaymentMethodActivityBinding
import com.stripe.example.databinding.PaymentMethodItemBinding

class CreateCardPaymentMethodActivity : StripeIntentActivity() {
    private val viewBinding: CreateCardPaymentMethodActivityBinding by lazy {
        CreateCardPaymentMethodActivityBinding.inflate(layoutInflater)
    }

    private val cardPaymentMethodViewModel: PaymentMethodViewModel by viewModels()

    private val adapter: PaymentMethodsAdapter = PaymentMethodsAdapter()
    private val snackbarController: SnackbarController by lazy {
        SnackbarController(viewBinding.coordinator)
    }
    private val keyboardController: KeyboardController by lazy {
        KeyboardController(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        viewBinding.paymentMethods.setHasFixedSize(false)
        viewBinding.paymentMethods.layoutManager = LinearLayoutManager(this)
        viewBinding.paymentMethods.adapter = adapter

        viewBinding.cardFormView.setCardValidCallback { isValid, invalidFields ->
            viewBinding.createButton.isEnabled = isValid
            viewBinding.confirmButton.isEnabled = isValid
            Log.d(
                CARD_VALID_CALLBACK_TAG,
                "Card information is " + (if (isValid) " valid" else " invalid")
            )
            if (!isValid) {
                Log.d(CARD_VALID_CALLBACK_TAG, " Invalid fields are $invalidFields")
            }
        }

        viewBinding.createButton.setOnClickListener {
            keyboardController.hide()

            viewBinding.cardFormView.cardParams?.let {
                createPaymentMethod(PaymentMethodCreateParams.createCard(it))
            }
        }

        viewBinding.confirmButton.setOnClickListener {
            keyboardController.hide()

            viewBinding.cardFormView.cardParams?.let {
                confirmPaymentMethod(PaymentMethodCreateParams.createCard(it))
            }
        }

        viewModel.status.observe(this){
            Log.e("MLB", it)
            showSnackbar(it)
        }

        viewModel.paymentResultLiveData.observe(this){
            completeInProgress()
            val msg = when(it){
                PaymentResult.Canceled -> "Cancelled"
                PaymentResult.Completed -> "Completed"
                is PaymentResult.Failed -> "Failed: ${it.throwable}"
            }
            showSnackbar("PaymentIntent confirmation result: $msg")
        }

        viewBinding.toggleCardFormView.setOnClickListener {
            viewBinding.cardFormView.isEnabled = !viewBinding.cardFormView.isEnabled
            showSnackbar(
                if (viewBinding.cardFormView.isEnabled) "CardFormView Enabled"
                else "CardFormView Disabled"
            )
        }
    }

    private fun createPaymentMethod(params: PaymentMethodCreateParams) {
        startInProgress()
        cardPaymentMethodViewModel.createPaymentMethod(params).observe(this) { result ->
            completeInProgress()
            result.fold(
                onSuccess = ::onCreatedPaymentMethod,
                onFailure = {
                    showSnackbar(it.message.orEmpty())
                }
            )
        }
    }

    private fun confirmPaymentMethod(params: PaymentMethodCreateParams) {
        startInProgress()
        createAndConfirmPaymentIntent(
            "US",
            params
        )
    }

    private fun showSnackbar(message: String) {
        snackbarController.show(message)
    }

    private fun startInProgress() {
        viewBinding.progressBar.visibility = View.VISIBLE
        viewBinding.createButton.isEnabled = false
        viewBinding.confirmButton.isEnabled = false
    }

    private fun completeInProgress() {
        viewBinding.progressBar.visibility = View.INVISIBLE
        viewBinding.createButton.isEnabled = true
        viewBinding.confirmButton.isEnabled = true
    }

    private fun onCreatedPaymentMethod(paymentMethod: PaymentMethod?) {
        if (paymentMethod != null) {
            adapter.paymentMethods.add(0, paymentMethod)
            adapter.notifyItemInserted(0)
        } else {
            showSnackbar("Created null PaymentMethod")
        }
    }

    private class PaymentMethodsAdapter :
        RecyclerView.Adapter<PaymentMethodsAdapter.PaymentMethodViewHolder>() {
        val paymentMethods: MutableList<PaymentMethod> = mutableListOf()

        init {
            setHasStableIds(true)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentMethodViewHolder {
            return PaymentMethodViewHolder(
                PaymentMethodItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }

        override fun getItemCount(): Int {
            return paymentMethods.size
        }

        override fun onBindViewHolder(holder: PaymentMethodViewHolder, position: Int) {
            holder.setPaymentMethod(paymentMethods[position])
        }

        override fun getItemId(position: Int): Long {
            return requireNotNull(paymentMethods[position].id).hashCode().toLong()
        }

        class PaymentMethodViewHolder internal constructor(
            private val viewBinding: PaymentMethodItemBinding
        ) : RecyclerView.ViewHolder(viewBinding.root) {
            internal fun setPaymentMethod(paymentMethod: PaymentMethod) {
                val card = paymentMethod.card
                viewBinding.paymentMethodId.text = paymentMethod.id
                viewBinding.brand.text = card?.brand?.displayName.orEmpty()
                viewBinding.last4.text = card?.last4.orEmpty()
            }
        }
    }

    private companion object {
        private const val CARD_VALID_CALLBACK_TAG = "CardValidCallback"
    }
}
