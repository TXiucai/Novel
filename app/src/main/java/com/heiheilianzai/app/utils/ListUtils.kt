package com.heiheilianzai.app.utils

import kr.co.voiceware.java.vtapi.SyncWordInfo

class ListUtils {
    companion object {

        /**
         * 获取剩下未读的文字集合
         */
        fun sListResult(list: List<SyncWordInfo>, sIndex: Int): List<SyncWordInfo> {
            //把前面已读的 sIndex 个元素去掉，只保留还未读取的词组集合
            val deleteLength = list.size - sIndex
            val newEList = list.takeLast(deleteLength)
            return newEList
        }

        /**
         * List<String> 拼接成 string
         */
        fun getEString(mWordInfoPause: List<SyncWordInfo>): String? {
            val stringBuilder = StringBuilder()
            for (i in mWordInfoPause.indices) {
                val word = mWordInfoPause[i].word
                stringBuilder.append(word)
            }
            return stringBuilder.toString()
        }

        /**
         * 返回数组指定元素的下标
         * 读书 音速 音调 倍速用
         */
        fun getElementIndex(value: Int): Int {
            val beisu = intArrayOf(50, 75, 100, 125, 150, 175, 200)
            return beisu.indexOf(value)
        }

    }
}