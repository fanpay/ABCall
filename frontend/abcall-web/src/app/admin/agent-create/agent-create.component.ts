import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { Agent } from './agent';
import { ToastrService } from 'ngx-toastr';


@Component({
  selector: 'app-agent-create',
  templateUrl: './agent-create.component.html',
  styleUrls: ['./agent-create.component.css']
})
export class AgentCreateComponent implements OnInit {

  agentForm!: FormGroup;

  constructor(
    private formBuilder: FormBuilder,
    private toastrService: ToastrService
  ) { }

  ngOnInit() {
    this.agentForm = this.formBuilder.group({
      username: ["", Validators.required],
      password: ["", Validators.required],
      email: ["", [Validators.required, Validators.email]],
      dni: ["", Validators.required],
      fullName: ["", Validators.required],
      phoneNumber: ["", Validators.required]
    });
  }

  createAgent(agent: Agent) {
    agent.role = "agent";
    console.info("The agent to created: ", agent)
    this.toastrService.success("Confirmation", "Agente creado!")
    this.agentForm.reset();
  }

  cancelCreation() {
    this.agentForm.reset();
  }
}
