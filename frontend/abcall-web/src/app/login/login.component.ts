import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { AuthService } from './auth.service';
import { Agent } from '../admin/agent-create/agent';
import { AgentService } from '../admin/agent-create/agent.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  showNewUserForm = false;
  loginForm!: FormGroup;
  newUserForm!: FormGroup;

  constructor(
    private authService: AuthService,
    private formBuilder: FormBuilder,
    private router: Router,
    private toastrService: ToastrService,
    private agentService: AgentService
  ) { }

  ngOnInit() {
    this.loginForm = this.formBuilder.group({
      username: ["", Validators.required],
      password: ["", Validators.required]
    });
    this.newUserForm = this.formBuilder.group({
      username: ["", Validators.required],
      password: ["", Validators.required],
      email: ["", [Validators.required, Validators.email]],
      dni: ["", Validators.required],
      fullName: ["", Validators.required],
      phoneNumber: ["", Validators.required]
    });
  }

  async validateLogin(dataLogin: any) {
    try {
      const validaInfo = await this.authService.validateLogin(dataLogin);

      const meInfo = await this.authService.getMeInfo(validaInfo.token);

      if (meInfo !== undefined && meInfo !== null) {
        this.toastrService.success("Bienvenido!");
        const userInfo = JSON.parse(localStorage.getItem('meInfo')!);
        this.router.navigate([`/${userInfo.role}`]);
      } else {
        this.toastrService.warning("Verifique sus datos.", "Verificacion");
      }
    } catch (error) {
      console.error('Error during login:', error);
      this.toastrService.warning("Verifique sus datos.", "Verificacion");
    }

  }

  createUser(user: Agent) {
    console.log(user);

    this.agentService.createAgent(user).subscribe({
      next: (res) => {
        console.log(res);
        this.toastrService.success(`El usuario ${user.username} fue creado!`, "Confirmación");
        this.newUserForm.reset();
        this.showNewUserForm = false;
      },
      error: (e: any) => {
        console.log(e);
        this.toastrService.warning("Error en los datos", "Verificación");
      }
    });
  }

  onNewUserClick() {
    this.showNewUserForm=!this.showNewUserForm;
  }


}
