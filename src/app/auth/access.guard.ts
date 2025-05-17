import { AuthService } from './auth.service';
import { inject } from '@angular/core';
import { Router } from '@angular/router';

export const canActivateAuth =() => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const isLoggedIn = authService.isAuth;

  if (!isLoggedIn) {
    return router.createUrlTree(['/login']);
  }

  return true;
}
