import com.ibm.icu.util.*;
import java.io.File

val availableLocales: Array<ULocale> = ULocale.getAvailableLocales()

   .filter { it.toString().length == 2 }
    //.filter { !it.toString().contains("_") }
    .toTypedArray()

val lastLocale = availableLocales.last().toString()

fun main() {
    println("processing ${availableLocales.size} locales, with last locale $lastLocale")
    writeJs()
}

fun writeJs() {

    File("table-header.js").bufferedWriter().use { out ->

        out.appendLine("var tableColumns = [")
        out.appendLine("""{title: "id", field: "rowid", frozen:true},  """)

        availableLocales.forEach { locale ->
            val id = locale.toString()
            out.append("""{title: "$id", field: "$id"}""")
            if (id != lastLocale) out.append(",")
            out.newLine()
        }
        out.appendLine("];")
    }

    File("table-data.js").bufferedWriter().use { out ->
        out.appendLine("var tabledata = [")

        availableLocales.forEach { row ->
            val rowId = row.toString()
            out.append("""{rowid: "$rowId", """)

            availableLocales.forEach { column ->
                val columnId = column.toString()
                out.append(""" $columnId: "${ULocale.getDisplayName(rowId, column)}" """)

                //column ending
                if (columnId != lastLocale) {
                    out.append(",")
                } else {
                    out.append("}")
                }
            }

            // row ending
            if (rowId != lastLocale) {
                out.appendLine(",")
            } else {
                out.newLine()
            }
        }
        out.appendLine("];")
    }
}

fun writeCsv() {

    File("locale.csv").bufferedWriter().use { out ->

        val header = availableLocales.joinToString(",") { it.toString() }

        out.write("locale,$header\n")

        availableLocales.forEach { uLocaleForId: ULocale? ->


            val id = uLocaleForId.toString()

            out.write("$id")
            availableLocales.forEach { uLocaleForDisplay ->
                out.write(",${ULocale.getDisplayName(id, uLocaleForDisplay)}")
            }

            out.write("\n")
        }
    }
}