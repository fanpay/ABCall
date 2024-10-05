import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { AuthService } from './auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {


  loginForm!: FormGroup;

  constructor(
    private authService: AuthService,
    private formBuilder: FormBuilder,
    private router: Router,
    private toastrService: ToastrService
  ) { }

  ngOnInit() {
    this.loginForm = this.formBuilder.group({
      username: ["", Validators.required],
      password: ["", Validators.required]
    });
  }

  async validateLogin(dataLogin: any) {
    await this.authService.validateLogin(dataLogin);

    if (this.authService.meInfo) {
      const meInfo = localStorage.getItem('meInfo');
      if (meInfo) {
        this.toastrService.success("Bienvenido!");
        const userInfo = JSON.parse(meInfo);
        this.router.navigate([`/${userInfo.role}`]);
      }
    } else {
      this.toastrService.warning("Verifique sus datos.", "Verificacion");
    }

  }
}
