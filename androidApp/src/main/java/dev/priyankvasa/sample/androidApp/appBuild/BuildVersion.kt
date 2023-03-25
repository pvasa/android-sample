package dev.priyankvasa.sample.androidApp.appBuild

import dev.priyankvasa.sample.data.core.util.runAppTaskCatching

internal data class BuildVersion(
    val major: Int,
    val minor: Int,
    val patch: Int,
    val code: Int,
) : Comparable<BuildVersion> {
    override fun compareTo(other: BuildVersion): Int =
        major.compareTo(other.major).takeIf { it != 0 }
            ?: minor.compareTo(other.minor).takeIf { it != 0 }
            ?: patch.compareTo(other.patch).takeIf { it != 0 }
            ?: run {
                if (code != INVALID_VERSION_CODE && other.code != INVALID_VERSION_CODE) {
                    code.compareTo(other.code)
                } else {
                    0
                }
            }

    override fun equals(other: Any?): Boolean =
        this === other ||
            (
                other is BuildVersion &&
                    major == other.major &&
                    minor == other.minor &&
                    patch == other.patch &&
                    (
                        (code == INVALID_VERSION_CODE || other.code == INVALID_VERSION_CODE) ||
                            code == other.code
                        )
                )

    override fun hashCode(): Int {
        var result = major
        result = 31 * result + minor
        result = 31 * result + patch
        if (code != INVALID_VERSION_CODE) {
            result = 31 * result + code
        }
        return result
    }

    internal companion object {
        const val INVALID_VERSION_CODE = Int.MIN_VALUE

        private const val VERSIONS_SEPARATOR = '.'
        private const val VERSION_CODE_SEPARATOR = '-'

        private val validVersionRegex = arrayOf(
            // matches "4.57.0" or "4.57.0-435" and similar
            Regex("(\\d+\\.\\d+\\.\\d+)+(-(\\d)+)*"),

            // matches "4.57.0-234-anythinghere"
            Regex("(\\d+\\.\\d+\\.\\d+)+(-(\\d)+)+(-.+)*"),
        )

        operator fun invoke(_name: String, _code: Int? = null): BuildVersion {
            validVersionRegex.first()

            val name = _name.takeIf { validVersionRegex.any { regex -> _name.matches(regex) } }
                ?: runAppTaskCatching {
                    // if the name does not match any valid regex,
                    // take the first valid part of it matching a simple version like
                    // "4.57.0-435" or "4.57.0"
                    validVersionRegex.first().toPattern().matcher(_name)
                        .run {
                            find()
                            group()
                        }
                }
                    .getOrElse { throw IllegalArgumentException("Version name $_name is not valid!", it) }

            val indexOfVersionCodeSeparator =
                name.indexOf(VERSION_CODE_SEPARATOR).coerceAtLeast(0)

            val versionName: String = name.substring(0, indexOfVersionCodeSeparator)
                .takeIf { it.isNotBlank() }
                ?: name

            val versions: List<Int> = versionName.split(VERSIONS_SEPARATOR)
                .map { it.toInt() }

            require(versions.size == 3) {
                "Version name $_name is not valid!"
            }

            val code: Int = _code ?: runAppTaskCatching {
                name.substring(
                    indexOfVersionCodeSeparator + 1,
                    name.indexOf(VERSION_CODE_SEPARATOR, (indexOfVersionCodeSeparator + 1))
                        .takeIf { it > -1 }
                        ?: name.count(),
                )
                    .toInt()
            }
                .getOrDefault(INVALID_VERSION_CODE)

            return BuildVersion(
                versions[0],
                versions[1],
                versions[2],
                code,
            )
        }
    }
}
