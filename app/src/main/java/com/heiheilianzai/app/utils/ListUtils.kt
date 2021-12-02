package com.heiheilianzai.app.utils

import kr.co.voiceware.java.vtapi.SyncWordInfo
import java.lang.StringBuilder

class ListUtils {
    companion object {

        fun sListResult(list: List<SyncWordInfo>, sIndex: Int): List<SyncWordInfo> {
            //把前面已读的 sIndex 个元素去掉，只保留还未读取的词组集合
            val deleteLength = list.size - sIndex
            val newEList = list.takeLast(deleteLength)
            return newEList
        }

        fun getEString(mWordInfoPause: List<SyncWordInfo>): String? {
            val stringBuilder = StringBuilder()
            for (i in mWordInfoPause.indices) {
                val word = mWordInfoPause[i].word
                stringBuilder.append(word)
            }
            return stringBuilder.toString()
        }

    }
}