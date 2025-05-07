import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { HomeComponent } from './home/home.component';
import { ProfileComponent } from './profile/profile.component';
import { AddEditBookComponent } from './add-edit-book/add-edit-book.component';
import { canActivateAuth } from './auth/access.guard';

const routes: Routes = [
  {path: '', component: HomeComponent},

  // {path: 'me', component: ProfileComponent, canActivate: [canActivateAuth]},
  {path: 'login', component: LoginComponent},
  {path: 'register', component: RegisterComponent},

  {path: 'me', component: ProfileComponent},
  {path: 'add-book', component: AddEditBookComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {
    onSameUrlNavigation: 'reload'
  })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
