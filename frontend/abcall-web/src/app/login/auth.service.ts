import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  isAuthenticated = false;

  constructor(
    private router: Router
  ) { }

  validateLogin(dataLogin: any) {
    console.log(dataLogin);

    if (dataLogin.username === "admin" && dataLogin.password === "admin") {
      this.isAuthenticated = true;
    } else if (dataLogin.username === "agent" && dataLogin.password === "agent") {
      this.isAuthenticated = true;
    } else if (dataLogin.username === "user" && dataLogin.password === "user") {
      this.isAuthenticated = true;
    } else {
      this.isAuthenticated = false;
    }
  }

  logout() {
    this.isAuthenticated = false;
    this.router.navigate(['']);
  }

  isActive() { 
    if (!this.isAuthenticated) {
      this.router.navigate(['']);
    }
  }

}
