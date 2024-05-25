package com.mangg.passwordcustomview.customview

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.dicoding.mangg.storyapp.R
import com.google.android.material.textfield.TextInputLayout

class PasswordCustomView : AppCompatEditText {

    private lateinit var lockImage: Drawable
    private lateinit var visibilityOffImage: Drawable
    private lateinit var visibilityOnImage: Drawable
    private var isPassValid: Boolean = false
    private var isPasswordVisible: Boolean = false
    private var textInputLayout: TextInputLayout? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        lockImage = ContextCompat.getDrawable(context, R.drawable.ic_baseline_lock_24)!!
        visibilityOffImage = ContextCompat.getDrawable(context, R.drawable.ic_visibility_off)!!
        visibilityOnImage = ContextCompat.getDrawable(context, R.drawable.ic_visibility)!!

        transformationMethod = PasswordTransformationMethod.getInstance()
        setIcons()

        addTextChangedListener(onTextChanged = { p0: CharSequence?, p1: Int, p2: Int, p3: Int ->
            val pass = text?.trim()
            when {
                pass.isNullOrEmpty() -> {
                    isPassValid = false
                    setErrorText(resources.getString(R.string.input_pass))
                }

                pass.length < 8 -> {
                    isPassValid = false
                    setErrorText(resources.getString(R.string.pass_length))
                }

                else -> {
                    isPassValid = true
                    setErrorText(null)
                }
            }

            setIcons()
        })
    }

    private fun setIcons() {
        val endIcon = if (isPasswordVisible) visibilityOnImage else visibilityOffImage
        setButtonDrawables(startOfTheText = lockImage, endOfTheText = endIcon)
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }

    private fun setErrorText(errorText: String?) {
        textInputLayout = parent.parent as? TextInputLayout
        textInputLayout?.error = errorText
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            if (it.action == MotionEvent.ACTION_UP) {
                val drawableEnd = compoundDrawables[2]
                if (drawableEnd != null && it.rawX >= (right - drawableEnd.bounds.width())) {
                    togglePasswordVisibility()
                    return true
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            transformationMethod = PasswordTransformationMethod.getInstance()
        } else {
            transformationMethod = null
        }
        isPasswordVisible = !isPasswordVisible
        setSelection(text?.length ?: 0)
        setIcons()
    }
}
