package com.kisman.cc.loader.antidump

import java.security.Permission

/**
 * @author _kisman_
 * @since 20:04 of 31.08.2022
 */
class CustomSecurityManager : SecurityManager() {
    override fun checkPermission(
        perm : Permission?
    ) {  }
}