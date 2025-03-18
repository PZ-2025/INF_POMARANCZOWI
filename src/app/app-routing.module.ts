import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {LoginComponent} from './login/login.component';
import {HomeComponent} from './home/home.component';
import {ProfileComponent} from './profile/profile.component';
import {canActivateAuth} from './auth/access.guard';


const routes: Routes = [
  { path: '', component: HomeComponent },

  {path: 'me', component: ProfileComponent,canActivate: [canActivateAuth]},
  {path: 'login',component: LoginComponent}
];


@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
