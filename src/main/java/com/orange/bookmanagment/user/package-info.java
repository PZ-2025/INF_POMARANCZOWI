
@ApplicationModule(
        allowedDependencies = {"shared"},
        type = ApplicationModule.Type.CLOSED,
        displayName = "user"
)

package com.orange.bookmanagment.user;

import org.springframework.modulith.ApplicationModule;