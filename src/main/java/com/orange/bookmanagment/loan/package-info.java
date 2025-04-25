@ApplicationModule(
        allowedDependencies = {"shared"},
        type = ApplicationModule.Type.CLOSED,
        displayName = "loan"
)
package com.orange.bookmanagment.loan;

import org.springframework.modulith.ApplicationModule;