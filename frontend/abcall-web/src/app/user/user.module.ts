import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserComponent } from './user.component';
import { IncidentCreateComponent } from './incident-create/incident-create.component';
import { IncidentSearchComponent } from './incident-search/incident-search.component';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule
  ],
  declarations: [
    UserComponent,
    IncidentCreateComponent,
    IncidentSearchComponent
  ],
  exports: [UserComponent]
})
export class UserModule { }
