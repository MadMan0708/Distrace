# Tries to locate nanomsg headers and libraries.
#
# Usage:
#
#     find_package(nanomsgxx)
#
#     NANOMSGXX_ROOT_DIR may be defined beforehand to hint at install location.
#
# Variables defined after calling:
#
#     NANOMSGXX_FOUND       - whether a nanomsgxx installation is located
#     NANOMSGXX_INCLUDE_DIRS - path to nanomsgxx headers
#     NANOMSGXX_LIBRARY     - path of nanomsgxx library

find_path(NANOMSGXX_ROOT_DIR
        NAMES include/nnxx/nn.h include/nanomsg/ext/nnxx_ext.h
        )

find_path(NANOMSGXX_INCLUDE_DIRS
        NAMES nnxx/nn.h
        HINTS ${NANOMSGXX_ROOT_DIR}/include
        )

find_library(NANOMSGXX_LIBRARY
        NAMES nnxx
        HINTS ${NANOMSGXX_ROOT_DIR}/lib
        )

include(FindPackageHandleStandardArgs)
find_package_handle_standard_args(nanomsgxx DEFAULT_MSG
        NANOMSGXX_INCLUDE_DIRS
        NANOMSGXX_LIBRARY
        )

mark_as_advanced(
        NANOMSGXX_ROOT_DIR
        NANOMSGXX_INCLUDE_DIRS
        NANOMSGXX_LIBRARY
)