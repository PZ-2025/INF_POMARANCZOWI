@ApplicationModule(
        allowedDependencies = {"shared", "book :: api", "user :: api"},
        type = ApplicationModule.Type.CLOSED,
        displayName = "reservation"

)

package com.orange.bookmanagment.reservation;

import org.springframework.modulith.ApplicationModule;