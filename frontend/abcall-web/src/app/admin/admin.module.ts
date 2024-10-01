import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminComponent } from './admin.component';
import { AgentCreateComponent } from './agent-create/agent-create.component';
import { ReactiveFormsModule } from '@angular/forms';
import { PlanComponent } from './plan/plan.component';

@NgModule({
  imports: [
    CommonModule,
    ReactiveFormsModule
  ],
  declarations: [
    AdminComponent, 
    AgentCreateComponent,
    PlanComponent],
  exports: [AdminComponent]
})
export class AdminModule { }
