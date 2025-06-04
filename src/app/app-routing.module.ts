import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { HomeComponent } from './home/home.component';
import { ProfileComponent } from './profile/profile.component';

import { canActivateAuth } from './auth/access.guard';
import { BookDetailsComponent } from './pages/book-details/book-details.component';
import {AddBookComponent} from './add-edit-book/add-book/add-book.component';
import {EditBookComponent} from './add-edit-book/edit-book/edit-book.component';

const routes: Routes = [
  { path: '', component: HomeComponent},

  { path: 'me', component: ProfileComponent, canActivate: [canActivateAuth] },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'books/:id', component: BookDetailsComponent },
  { path: 'book/add', component: AddBookComponent, canActivate: [canActivateAuth], data: { roles: ['ADMIN','LIBRARIAN'] } },
  { path: 'book/edit/:id', component: EditBookComponent, canActivate: [canActivateAuth], data: { roles: ['ADMIN','LIBRARIAN'] } },

  { path: '**', redirectTo: '', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {
    onSameUrlNavigation: 'reload'
  })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
