package id.dev.oxy.util

import android.content.Context
import com.mazenrashed.printooth.data.printable.ImagePrintable
import com.mazenrashed.printooth.data.printable.Printable
import com.mazenrashed.printooth.data.printable.TextPrintable
import com.mazenrashed.printooth.data.printer.DefaultPrinter
import id.dev.oxy.R
import id.dev.oxy.data.model.history.detail.Data
import id.dev.oxy.data.model.history.detail.SellLine
import id.dev.oxy.data.model.sales.discount.DiscountType
import kotlin.math.absoluteValue

object PrintHistoryHelper {

    // 1 line == 32 karakter normal
    // 1 line == 16 karakter large bold
    fun formatPrint(context: Context, data: Data, cashierName: String) =
        ArrayList<Printable>().apply {
            add(
                ImagePrintable.Builder(R.drawable.oxy, context.resources)
                    .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                    .setNewLinesAfter(2)
                    .build())
            add(
                TextPrintable.Builder()
                    .setText("  Perfume   Accesories  Cosmetic")
                    .setLineSpacing(DefaultPrinter.LINE_SPACING_30)
                    .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                    .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
                    .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_NORMAL)
                    .setUnderlined(DefaultPrinter.UNDERLINED_MODE_OFF)
                    .setNewLinesAfter(2)
                    .build()
            )
            add(
                TextPrintable.Builder()
                    .setText("OXY ${data.location.name}")
                    .setLineSpacing(DefaultPrinter.LINE_SPACING_30)
                    .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                    .setFontSize(DefaultPrinter.FONT_SIZE_LARGE)
                    .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                    .setUnderlined(DefaultPrinter.UNDERLINED_MODE_OFF)
                    .setNewLinesAfter(1)
                    .build()
            )
            add(
                TextPrintable.Builder()
                    .setText("${data.location.landmark ?: ""}")
                    .setLineSpacing(DefaultPrinter.LINE_SPACING_60)
                    .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                    .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
                    .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_NORMAL)
                    .setUnderlined(DefaultPrinter.UNDERLINED_MODE_OFF)
                    .setNewLinesAfter(1)
                    .build()
            )
            add(
                TextPrintable.Builder()
                    .setText("Kasir : $cashierName")
                    .setLineSpacing(DefaultPrinter.LINE_SPACING_30)
                    .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                    .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
                    .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_NORMAL)
                    .setUnderlined(DefaultPrinter.UNDERLINED_MODE_OFF)
                    .setNewLinesAfter(1)
                    .build()
            )
            add(
                TextPrintable.Builder()
                    .setText("Pelanggan : ${data.contact.name}")
                    .setLineSpacing(DefaultPrinter.LINE_SPACING_30)
                    .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                    .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
                    .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_NORMAL)
                    .setUnderlined(DefaultPrinter.UNDERLINED_MODE_OFF)
                    .setNewLinesAfter(1)
                    .build()
            )
            add(
                TextPrintable.Builder()
                    .setText("--------------------------------")
                    .setLineSpacing(DefaultPrinter.LINE_SPACING_30)
                    .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                    .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
                    .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_NORMAL)
                    .setUnderlined(DefaultPrinter.UNDERLINED_MODE_OFF)
                    .setNewLinesAfter(1)
                    .build()
            )

            data.sellLines.forEachIndexed { indexItem, sell ->
                val totalPrice = (sell.quantity.toDouble()
                    .times(sell.unitPriceBeforeDiscount.toDouble()))
                val lastItem = indexItem == data.sellLines.lastIndex

                val chunked = sell.product.name.chunked(20)
                chunked.forEachIndexed { index, string ->
                    when (chunked.size) {
                        1 -> {
                            add(
                                TextPrintable.Builder()
                                    .setText(
                                        setDynamicSpace(
                                            string,
                                            totalPrice.toString().toDecimalFormat()
                                        )
                                    )
                                    .setLineSpacing(DefaultPrinter.LINE_SPACING_30)
                                    .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                                    .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
                                    .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_NORMAL)
                                    .setUnderlined(DefaultPrinter.UNDERLINED_MODE_OFF)
                                    .setNewLinesAfter(1)
                                    .build()
                            )
                            add(
                                TextPrintable.Builder()
                                    .setText(
                                        setDynamicSpace(
                                            "${sell.quantity.substringBefore(".")} x ${
                                                sell.unitPriceBeforeDiscount.substringBefore(".")
                                            }", calculateDiscount(sell)
                                        )
                                    )
                                    .setLineSpacing(DefaultPrinter.LINE_SPACING_30)
                                    .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                                    .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
                                    .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_NORMAL)
                                    .setUnderlined(DefaultPrinter.UNDERLINED_MODE_OFF)
                                    .setNewLinesAfter(if (lastItem) 1 else 2)
                                    .build()
                            )
                        }
                        2 -> {
                            when (index) {
                                0 -> add(
                                    TextPrintable.Builder()
                                        .setText(
                                            setDynamicSpace(
                                                string,
                                                totalPrice.toString().toDecimalFormat()
                                            )
                                        )
                                        .setLineSpacing(DefaultPrinter.LINE_SPACING_30)
                                        .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                                        .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
                                        .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_NORMAL)
                                        .setUnderlined(DefaultPrinter.UNDERLINED_MODE_OFF)
                                        .setNewLinesAfter(1)
                                        .build()
                                )
                                1 -> {
                                    add(
                                        TextPrintable.Builder()
                                            .setText(
                                                setDynamicSpace(
                                                    string,
                                                    calculateDiscount(sell)
                                                )
                                            )
                                            .setLineSpacing(DefaultPrinter.LINE_SPACING_30)
                                            .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                                            .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
                                            .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_NORMAL)
                                            .setUnderlined(DefaultPrinter.UNDERLINED_MODE_OFF)
                                            .setNewLinesAfter(1)
                                            .build()
                                    )
                                    add(
                                        TextPrintable.Builder()
                                            .setText(
                                                "${sell.quantity.substringBefore(".")} x ${
                                                    sell.unitPriceBeforeDiscount.substringBefore(".")
                                                }"
                                            )
                                            .setLineSpacing(DefaultPrinter.LINE_SPACING_30)
                                            .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                                            .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
                                            .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_NORMAL)
                                            .setUnderlined(DefaultPrinter.UNDERLINED_MODE_OFF)
                                            .setNewLinesAfter(if (lastItem) 1 else 2)
                                            .build()
                                    )
                                }
                            }
                        }
                        else -> {
                            when (index) {
                                0 -> add(
                                    TextPrintable.Builder()
                                        .setText(
                                            setDynamicSpace(
                                                string,
                                                totalPrice.toString().toDecimalFormat()
                                            )
                                        )
                                        .setLineSpacing(DefaultPrinter.LINE_SPACING_30)
                                        .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                                        .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
                                        .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_NORMAL)
                                        .setUnderlined(DefaultPrinter.UNDERLINED_MODE_OFF)
                                        .setNewLinesAfter(1)
                                        .build()
                                )
                                1 -> add(
                                    TextPrintable.Builder()
                                        .setText(setDynamicSpace(string, calculateDiscount(sell)))
                                        .setLineSpacing(DefaultPrinter.LINE_SPACING_30)
                                        .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                                        .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
                                        .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_NORMAL)
                                        .setUnderlined(DefaultPrinter.UNDERLINED_MODE_OFF)
                                        .setNewLinesAfter(1)
                                        .build()
                                )
                                chunked.lastIndex -> {
                                    add(
                                        TextPrintable.Builder()
                                            .setText(string)
                                            .setLineSpacing(DefaultPrinter.LINE_SPACING_30)
                                            .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                                            .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
                                            .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_NORMAL)
                                            .setUnderlined(DefaultPrinter.UNDERLINED_MODE_OFF)
                                            .setNewLinesAfter(1)
                                            .build()
                                    )
                                    add(
                                        TextPrintable.Builder()
                                            .setText(
                                                "${sell.quantity.substringBefore(".")} x ${
                                                    sell.unitPriceBeforeDiscount.substringBefore(".")
                                                }"
                                            )
                                            .setLineSpacing(DefaultPrinter.LINE_SPACING_30)
                                            .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                                            .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
                                            .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_NORMAL)
                                            .setUnderlined(DefaultPrinter.UNDERLINED_MODE_OFF)
                                            .setNewLinesAfter(if (lastItem) 1 else 2)
                                            .build()
                                    )
                                }
                                else -> add(
                                    TextPrintable.Builder()
                                        .setText(string)
                                        .setLineSpacing(DefaultPrinter.LINE_SPACING_30)
                                        .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                                        .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
                                        .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_NORMAL)
                                        .setUnderlined(DefaultPrinter.UNDERLINED_MODE_OFF)
                                        .setNewLinesAfter(1)
                                        .build()
                                )
                            }
                        }
                    }
                }
            }

            add(
                TextPrintable.Builder()
                    .setText("--------------------------------")
                    .setLineSpacing(DefaultPrinter.LINE_SPACING_30)
                    .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                    .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
                    .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_NORMAL)
                    .setUnderlined(DefaultPrinter.UNDERLINED_MODE_OFF)
                    .setNewLinesAfter(1)
                    .build()
            )
            add(
                TextPrintable.Builder()
                    .setText(
                        setDynamicSpace("Member", data.sellLines.sumOf {
                            it.lineDiscountMember.substringBefore(".").toInt()
                        }.times(-1).toString().toDecimalFormat())
                    )
                    .setLineSpacing(DefaultPrinter.LINE_SPACING_30)
                    .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                    .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
                    .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_NORMAL)
                    .setUnderlined(DefaultPrinter.UNDERLINED_MODE_OFF)
                    .setNewLinesAfter(1)
                    .build()
            )
            add(
                TextPrintable.Builder()
                    .setText(
                        setDynamicSpace(
                            "Diskon",
                            data.discountAmount.substringBefore(".").toInt().times(-1)
                                .toString().toDecimalFormat()
                        )
                    )
                    .setLineSpacing(DefaultPrinter.LINE_SPACING_30)
                    .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                    .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
                    .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_NORMAL)
                    .setUnderlined(DefaultPrinter.UNDERLINED_MODE_OFF)
                    .setNewLinesAfter(1)
                    .build()
            )
            add(
                TextPrintable.Builder()
                    .setText("--------------------------------")
                    .setLineSpacing(DefaultPrinter.LINE_SPACING_30)
                    .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                    .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
                    .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_NORMAL)
                    .setUnderlined(DefaultPrinter.UNDERLINED_MODE_OFF)
                    .setNewLinesAfter(1)
                    .build()
            )
            add(
                TextPrintable.Builder()
                    .setText(
                        setDynamicSpaceLargeText(
                            "Total",
                            data.finalTotal.toDecimalFormat()
                        )
                    )
                    .setLineSpacing(DefaultPrinter.LINE_SPACING_30)
                    .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                    .setFontSize(DefaultPrinter.FONT_SIZE_LARGE)
                    .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                    .setUnderlined(DefaultPrinter.UNDERLINED_MODE_OFF)
                    .setNewLinesAfter(2)
                    .build()
            )
            add(
                TextPrintable.Builder()
                    .setText(
                        setDynamicSpace(
                            data.paymentLines[0].method.replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase()
                                else it.toString()
                            },
                            data.paymentLines[0].amount.toDecimalFormat()
                        )
                    )
                    .setLineSpacing(DefaultPrinter.LINE_SPACING_30)
                    .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                    .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
                    .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_NORMAL)
                    .setUnderlined(DefaultPrinter.UNDERLINED_MODE_OFF)
                    .setNewLinesAfter(1)
                    .build()
            )
            add(
                TextPrintable.Builder()
                    .setText("--------------------------------")
                    .setLineSpacing(DefaultPrinter.LINE_SPACING_30)
                    .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                    .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
                    .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_NORMAL)
                    .setUnderlined(DefaultPrinter.UNDERLINED_MODE_OFF)
                    .setNewLinesAfter(1)
                    .build()
            )
            add(
                TextPrintable.Builder()
                    .setText("Tampil Wangi dan Beda")
                    .setLineSpacing(DefaultPrinter.LINE_SPACING_30)
                    .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                    .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
                    .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_NORMAL)
                    .setUnderlined(DefaultPrinter.UNDERLINED_MODE_OFF)
                    .setNewLinesAfter(2)
                    .build()
            )
            add(
                TextPrintable.Builder()
                    .setText("ATTENTION !!")
                    .setLineSpacing(DefaultPrinter.LINE_SPACING_30)
                    .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                    .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
                    .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_NORMAL)
                    .setUnderlined(DefaultPrinter.UNDERLINED_MODE_OFF)
                    .setNewLinesAfter(1)
                    .build()
            )
            add(
                TextPrintable.Builder()
                    .setText("Barang yang sudah di BELI, tidak dapat di TUKAR atau di KEMBALI kan.")
                    .setLineSpacing(DefaultPrinter.LINE_SPACING_30)
                    .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                    .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
                    .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_NORMAL)
                    .setUnderlined(DefaultPrinter.UNDERLINED_MODE_OFF)
                    .setNewLinesAfter(2)
                    .build()
            )
            add(
                TextPrintable.Builder()
                    .setText(
                        setDynamicSpace(
                            data.transactionDate.formatToDateDetail(),
                            buildString {
                                append("#")
                                append(data.invoiceNo)
                            }
                        )
                    )
                    .setLineSpacing(DefaultPrinter.LINE_SPACING_30)
                    .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                    .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
                    .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_NORMAL)
                    .setUnderlined(DefaultPrinter.UNDERLINED_MODE_OFF)
                    .setNewLinesAfter(3)
                    .build()
            )
        }

    private fun setDynamicSpace(firstString: String, secondString: String): String {
        val maxCharacter = 31
        var space = ""
        if ((firstString + secondString).length <= maxCharacter) {
            for (i in (firstString + secondString).length..maxCharacter) {
                space += " "
            }
        }
        return buildString {
            append(firstString + space + secondString)
        }
    }

    private fun setDynamicSpaceLargeText(firstString: String, secondString: String): String {
        val maxCharacter = 15
        var space = ""
        if ((firstString + secondString).length <= maxCharacter) {
            for (i in (firstString + secondString).length..maxCharacter) {
                space += " "
            }
        }
        return buildString {
            append(firstString + space + secondString)
        }
    }

    private fun calculateDiscount(sellLine: SellLine): String {
        val totalPrice = (sellLine.quantity.toDouble()
            .times(sellLine.unitPriceBeforeDiscount.toDouble()))

        return if (sellLine.lineDiscountAmount.substringBefore(".").toInt() > 0) {
            return when (sellLine.lineDiscountType) {
                DiscountType.fixed.name -> "(-${
                    sellLine.lineDiscountAmount.substringBefore(".").toDecimalFormat()
                })"
                DiscountType.percentage.name -> {
                    val percentProduct =
                        sellLine.lineDiscountAmount.substringBefore(".")
                            .toDouble().absoluteValue.div(100)
                    val discountPercentProduct =
                        totalPrice.times(percentProduct)
                    "(-${
                        discountPercentProduct.toString().substringBefore(".")
                            .toDecimalFormat()
                    })"
                }
                else -> ""
            }
        } else ""
    }
}