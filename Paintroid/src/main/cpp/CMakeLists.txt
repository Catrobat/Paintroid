cmake_minimum_required(VERSION 3.4.1)

add_library(pocketpaint SHARED
            pocketpaint_fill_algorithm.cpp )

target_link_libraries(pocketpaint
                      log)
