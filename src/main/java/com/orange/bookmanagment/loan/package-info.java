@ApplicationModule(
        allowedDependencies = {"shared", "reservation :: api", "book :: *", "user :: api"},
        type = ApplicationModule.Type.CLOSED,
        displayName = "loan"
)
package com.orange.bookmanagment.loan;

import org.springframework.modulith.ApplicationModule;