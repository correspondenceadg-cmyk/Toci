cmake_minimum_required(VERSION 3.22)
project(toci_firmware C)
set(CMAKE_C_STANDARD 11)
add_library(toci_firmware STATIC
    src/sanitize.c
    src/verify.c
    src/bootparts.c
    src/mmc.c)
target_include_directories(toci_firmware PUBLIC include)
target_compile_options(toci_firmware PRIVATE -Wall -Wextra -Werror)
