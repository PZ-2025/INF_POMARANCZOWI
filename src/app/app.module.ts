import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { NavbarComponent } from './navbar/navbar.component';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { HomeComponent } from './home/home.component';
import { ProfileComponent } from './profile/profile.component';
import { FormsModule } from '@angular/forms';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { AuthInterceptor } from './auth/auth.interceptor';
import { EditUserModalComponent } from './components/edit-user-modal/edit-user-modal.component';
import { EditPasswordModalComponent } from './components/edit-password-modal/edit-password-modal.component';
import { EditAvatarModalComponent } from './components/edit-avatar-modal/edit-avatar-modal.component';
import { BookDetailsComponent } from './pages/book-details/book-details.component';
import {ReportsComponent} from './report/reports.component';
import {AddBookComponent} from './add-edit-book/add-book/add-book.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    RegisterComponent,
    NavbarComponent,
    HomeComponent,
    ProfileComponent,
    AddBookComponent,
    EditUserModalComponent,
    EditPasswordModalComponent,
    EditAvatarModalComponent,
    BookDetailsComponent,
    ReportsComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    HttpClientModule,
    FormsModule
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }
  ],
  exports: [
    NavbarComponent
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
