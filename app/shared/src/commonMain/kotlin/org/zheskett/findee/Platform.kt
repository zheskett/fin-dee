package org.zheskett.findee

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform