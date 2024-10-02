import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {


  loginForm!: FormGroup;

  constructor(
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

  validateLogin(dataLogin: any) {
    console.log(dataLogin);
    if (dataLogin.username === "admin" && dataLogin.password === "admin") {
      this.router.navigate(['/admin']);
    } else if (dataLogin.username === "agent" && dataLogin.password === "agent") {
      this.router.navigate(['/agent']);
    } else if (dataLogin.username === "user" && dataLogin.password === "user") {
      this.router.navigate(['/user']);
    } else {
      this.toastrService.warning("Verifique sus datos.", "Verificacion");
    }
  }
}
