package com.example.muzix.ultis

import com.example.muzix.model.History

class HistoryComparator : Comparator<History> {
    override fun compare(o1: History?, o2: History?): Int {
        return when (o1?.id.toString().compareTo(o2?.id.toString())) {
            1 -> -1
            - 1 -> 1
            else -> 0
        }
    }
}