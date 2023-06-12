package com.example.muzix.ultis

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.google.android.material.snackbar.Snackbar

fun showSoftKeyboard(activity: Activity) {
    val imm: InputMethodManager =
        activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
}

fun hiddenSoftKeyboard(activity: Activity) {
    val imm: InputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(activity.window.decorView.windowToken, 0)
}
fun showSnackBar(viewGroup: ViewGroup){
    val snackBar = Snackbar.make(viewGroup,"", Snackbar.LENGTH_SHORT)
    val layoutParams = snackBar.view.layoutParams as ViewGroup.MarginLayoutParams
    layoutParams.setMargins(0,0,0,110)
    snackBar.view.layoutParams = layoutParams
    val spannable = SpannableStringBuilder()
    spannable.apply {
        append("Chức năng chỉ có ở ")
        append("Muzix Premium",StyleSpan(Typeface.BOLD),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    snackBar.setText(spannable)
    snackBar.show()
}