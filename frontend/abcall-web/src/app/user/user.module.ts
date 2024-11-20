import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserComponent } from './user.component';
import { IncidentCreateComponent } from './incident-create/incident-create.component';
import { IncidentSearchComponent } from './incident-search/incident-search.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { IncidentDetailComponent } from './incident-detail/incident-detail.component';
import { ChatbotComponent } from './chatbot/chatbot.component';

@NgModule({
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    RouterModule
  ],
  declarations: [
    UserComponent,
    IncidentCreateComponent,
    IncidentSearchComponent,
    IncidentDetailComponent,
    ChatbotComponent
  ],
  exports: [UserComponent]
})
export class UserModule { }
