import org.gradle.api.Project

inline fun <reified T : Any> Project.propertyValue(key: String, default: T? = null): T? =
    if (hasProperty(key)) {
        val propertyValue = property(key) as String

        when (T::class) {
            Char::class -> kotlin.run {
                val chars = propertyValue.toCharArray()
                require(chars.size == 1)
                chars.first()
            }
            String::class -> propertyValue

            Boolean::class -> propertyValue.toBoolean()

            Byte::class -> propertyValue.toByte()
            Short::class -> propertyValue.toShort()
            Int::class -> propertyValue.toInt()
            Long::class -> propertyValue.toLong()

            Float::class -> propertyValue.toFloat()
            Double::class -> propertyValue.toDouble()

            ByteArray::class -> propertyValue.split(',')
                .map { it.toByte() }
                .toTypedArray()
            ShortArray::class -> propertyValue.split(',')
                .map { it.toShort() }
                .toTypedArray()
            IntArray::class -> propertyValue.split(',')
                .map { it.toInt() }
                .toTypedArray()

            else -> error("Unknown type for value $propertyValue")
        } as T
    } else {
        default
    }
